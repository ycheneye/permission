package com.xmcc.service.impl;

import com.xmcc.beans.LogType;
import com.xmcc.dao.SysAclMapper;
import com.xmcc.dao.SysAclModuleMapper;
import com.xmcc.dao.SysLogMapper;
import com.xmcc.entity.SysAclModule;
import com.xmcc.entity.SysDept;
import com.xmcc.entity.SysLogWithBLOBs;
import com.xmcc.exception.ParamException;
import com.xmcc.param.SysAclModelParam;
import com.xmcc.service.AclModuleService;
import com.xmcc.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class AclModuleServiceImpl implements AclModuleService {

    @Autowired
    private SysAclModuleMapper aclModuleMapper;

    @Autowired
    private SysAclMapper aclMapper;

    @Autowired
    private SysLogMapper logMapper;


    @Override
    public void insertAclModule(SysAclModelParam param) {
        BeanValidator.check(param);//检查参数
        if (checkExist(param.getParentId(), param.getName(), param.getId()) > 0){
            throw new ParamException("同一层级下存在相同权限模块!");
        }
        SysAclModule aclModule = SysAclModule.builder().name(param.getName()).parentId(param.getParentId()).
                level(LevelUtil.contact(getLevel(param.getParentId()), param.getParentId())).seq(param.getSeq()).
                status(param.getStatus()).remark(param.getRemark()).operateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset())).
                operateTime(new Date()).operator(ThreadUtil.getUser().getUsername()).build();

        aclModuleMapper.insertSelective(aclModule);

        //记录日志
        saveAclModuleLog(null, aclModule);
    }

    @Override
    public void updateAclModule(SysAclModelParam param) {
        BeanValidator.check(param);

        SysAclModule after = SysAclModule.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId()).
                level(LevelUtil.contact(getLevel(param.getParentId()), param.getParentId())).seq(param.getSeq()).
                status(param.getStatus()).remark(param.getRemark()).operateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset())).
                operateTime(new Date()).operator(ThreadUtil.getUser().getUsername()).build();

        if (checkExist(after.getParentId(), after.getName(), after.getId()) > 0){
            throw new ParamException("同层级下已存在相同权限模块!");
        }

        SysAclModule before = aclModuleMapper.selectByPrimaryKey(after.getId());//获取数据库里的旧权限模块数据
        if ( before== null){
            throw new ParamException("该权限模块已被其他管理员删除,请刷新页面。");
        }else {
            recursionUpdateAclModule(after, before);
        }
    }

    @Override
    public void deleteAclModule(Integer id) {
        SysAclModule aclModule = aclModuleMapper.selectByPrimaryKey(id);//先查找到需要删除的部门
        if (aclModule!=null) {
            List<SysAclModule> aclModules = aclModuleMapper.selectAclModuleByLevel(aclModule.getLevel() + "." + aclModule.getId());
            if (aclModules.size() != 0) {//表示有子权限模块
                throw new ParamException("该权限模块下有子权限模块，不能直接冻结");
            } else {
                if (aclMapper.selectAclByAclModuleId(id).size() != 0) {
                    throw new ParamException("该权限模块下有权限，不能直接冻结");
                } else {
                    //保留一份旧数据用作记录日志
                    SysAclModule module = new SysAclModule();
                    BeanUtils.copyProperties(aclModule, module);

                    //冻结操作
                    aclModule.setStatus(0);
                    aclModuleMapper.updateByPrimaryKeySelective(aclModule);

                    //记录日志
                    saveAclModuleLog(module, aclModule);
                }
            }
        }else {
            throw new ParamException("待冻结的权限模块不存在");
        }
    }


    @Override
    public void recursionUpdateAclModule(SysAclModule after,SysAclModule before){
        if (!after.getLevel().equals(before.getLevel())){//判断有没有修改层级
            List<SysAclModule> nextAclModules = aclModuleMapper.selectAclModuleByLevel(before.getLevel() + "." + before.getId());
            if (nextAclModules != null){//判断需要修改的部门下还有没有子部门
                for (SysAclModule aclModule:nextAclModules) {//循环遍历为子部门设置新的level
                    //需要保存一份旧数据
                    SysAclModule sysAclModule_before = new SysAclModule();
                    BeanUtils.copyProperties(aclModule, sysAclModule_before);

                    aclModule.setLevel(after.getLevel()+"."+after.getId());//设置新的level

                    recursionUpdateAclModule(aclModule, sysAclModule_before);
                }

            }
        }
        aclModuleMapper.updateByPrimaryKeySelective(after);

        //记录日志
        saveAclModuleLog(before, after);
    }



    /**
     * 判断同意层级下是否有相同权限模块
     */
    public int checkExist(Integer parentId,String aclModuleName,Integer aclModuleId){
        return aclModuleMapper.countByParentAndaclModuleName(parentId, aclModuleName, aclModuleId);
    }

    /**
     * 根据id获取自己的权限模块等级
     * @param id
     * @return
     */
    public String getLevel(int id){
        SysAclModule aclModule = aclModuleMapper.selectByPrimaryKey(id);
        if (aclModule == null){
            return  null;
        }
        return aclModule.getLevel();

    }


    @Override
    public void saveAclModuleLog(SysAclModule before, SysAclModule after){
        SysLogWithBLOBs sysLogWithBLOBs = new SysLogWithBLOBs();
        sysLogWithBLOBs.setType( LogType.TYPE_ACL_MODULE );
        sysLogWithBLOBs.setOperateTime( new Date() );
        sysLogWithBLOBs.setOperator( ThreadUtil.getUser().getUsername() );
        sysLogWithBLOBs.setOperateIp( IpUtil.getUserIP( ThreadUtil.getRequset() ) );
        sysLogWithBLOBs.setTargetId( after==null ? before.getId():after.getId() );
        sysLogWithBLOBs.setOldValue( before==null? "": JsonMapper.obj2String( before ) );
        sysLogWithBLOBs.setNewValue( after==null? "": JsonMapper.obj2String( after ) );
        sysLogWithBLOBs.setStatus( 0 );
        logMapper.insertSelective( sysLogWithBLOBs );
    }
}
