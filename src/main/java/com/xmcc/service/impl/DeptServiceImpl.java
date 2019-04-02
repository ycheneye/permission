package com.xmcc.service.impl;

import com.xmcc.beans.LogType;
import com.xmcc.dao.SysDeptMapper;
import com.xmcc.dao.SysLogMapper;
import com.xmcc.dao.SysUserMapper;
import com.xmcc.entity.SysDept;
import com.xmcc.entity.SysLogWithBLOBs;
import com.xmcc.exception.ParamException;
import com.xmcc.param.SysDeptParam;
import com.xmcc.service.DeptService;
import com.xmcc.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class DeptServiceImpl implements DeptService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private SysUserMapper userMapper;

    @Autowired
    private SysLogMapper logMapper;


    @Override
    public void insertDept(SysDeptParam param) {
        BeanValidator.check(param);//检查参数
        if (checkExist(param.getParentId(), param.getName(), param.getId()) > 0){//查询到了重复数据
            throw new ParamException("同一层级下存在相同部门！");
        }
        SysDept dept = SysDept.builder().id(param.getId()).name(param.getName()).parentId(param.getParentId()).seq(param.getSeq()).remark(param.getRemark()).build();
        dept.setLevel(LevelUtil.contact(getLevel(param.getParentId()), param.getParentId()));//通过计算获得
        dept.setOperateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset()));
        dept.setOperateTime(new Date());
        dept.setOperator(ThreadUtil.getUser().getUsername());
        sysDeptMapper.insertSelective(dept);

        //记录日志
        saveDeptLog(null, dept);
    }

    @Override
    public void updateDept(SysDeptParam param) {
        BeanValidator.check(param);
        //拿到待更新的数据封装到dept里去
        SysDept after = SysDept.builder().id(param.getId()).remark(param.getRemark()).seq(param.getSeq()).
                parentId(param.getParentId()).name(param.getName()).
                level(getLevel(param.getParentId()) == null ? "0": getLevel(param.getParentId())+"."+param.getParentId())
                .operator(ThreadUtil.getUser().getUsername()).operateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset())).operateTime(new Date()).build();

        if (checkExist(after.getParentId(), after.getName(), after.getId()) > 0){
            throw new ParamException("同层级下已存在相同部门");
        }

        SysDept before = sysDeptMapper.selectByPrimaryKey(after.getId());//获取数据库里的旧部门数据
        if ( before== null){
            throw new ParamException("该部门已被其他管理员删除,请刷新页面。");
        }else {
            recursionUpdateDept(after, before);
        }

    }

    @Override
    public void deleteDept(int id) {
        SysDept dept = sysDeptMapper.selectByPrimaryKey(id);//先查找到需要删除的部门
        if (dept!=null) {
            List<SysDept> sysDepts = sysDeptMapper.selectDeptByLevel(dept.getLevel() + "." + dept.getId());
            if (sysDepts.size() != 0) {//表示有子部门
                throw new ParamException("该部门下有子部门，不能直接删除");
            } else {
                if (userMapper.selectUserByDeptId(id).size() != 0) {//表示该部门下有员工
                    throw new ParamException("该部门下有员工，不能直接删除");
                } else {
                    sysDeptMapper.deleteByPrimaryKey(id);

                    //记录日志
                    saveDeptLog(dept, null);
                }
            }
        }else {
            throw new ParamException("待删除的部门不存在");
        }
    }


    @Override
    public void recursionUpdateDept(SysDept after,SysDept before){
        if (!after.getLevel().equals(before.getLevel())){//判断有没有修改层级
            List<SysDept> nextDepts = sysDeptMapper.selectDeptByLevel(before.getLevel() + "." + before.getId());
            if (nextDepts != null){//判断需要修改的部门下还有没有子部门
                for (SysDept dept:nextDepts) {//循环遍历为子部门设置新的level
                    //需要保存一份旧数据
                    SysDept sysDept_before = new SysDept();
                    BeanUtils.copyProperties(dept, sysDept_before);

                    dept.setLevel(after.getLevel()+"."+after.getId());//设置新的level

                    recursionUpdateDept(dept, sysDept_before);
                }
            }
        }
        sysDeptMapper.updateByPrimaryKeySelective(after);

        //记录日志
        saveDeptLog(before, after);
    }


    /**
     * 判断同意层级下是否有相同部门
     */
    public int checkExist(Integer parentId,String deptName,Integer deptId){
        return sysDeptMapper.countByParentAndDeptName(parentId, deptName, deptId);
    }

    /**
     * 根据id获取自己的部门等级
     * @param id
     * @return
     */
    public String getLevel(int id){
        SysDept dept = sysDeptMapper.selectByPrimaryKey(id);
        if (dept == null){
            return  null;
        }
        return dept.getLevel();

    }

    /**
     * 记录日志的方法
     * @param before
     * @param after
     */
    @Override
    public void saveDeptLog(SysDept before, SysDept after){
        SysLogWithBLOBs sysLogWithBLOBs = new SysLogWithBLOBs();
        sysLogWithBLOBs.setType( LogType.TYPE_DEPT );
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
