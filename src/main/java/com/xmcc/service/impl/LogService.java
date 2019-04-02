package com.xmcc.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmcc.beans.LogType;
import com.xmcc.beans.PageBean;
import com.xmcc.dao.*;
import com.xmcc.dto.SysLogDto;
import com.xmcc.entity.*;
import com.xmcc.exception.ParamException;
import com.xmcc.param.SysLogParam;
import com.xmcc.service.AclModuleService;
import com.xmcc.service.DeptService;
import com.xmcc.service.RoleService;
import com.xmcc.utils.BeanValidator;
import com.xmcc.utils.IpUtil;
import com.xmcc.utils.JsonMapper;
import com.xmcc.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.security.acl.Acl;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
@SuppressWarnings("all")
public class LogService {

    @Autowired
    private SysLogMapper logMapper;

    @Autowired
    private SysDeptMapper deptMapper;

    @Autowired
    private DeptService deptService;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private SysAclModuleMapper aclModuleMapper;

    @Autowired
    private AclModuleService aclModuleService;

    @Autowired
    private SysAclMapper aclMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleUserService roleUserService;

    public PageBean<SysLogWithBLOBs> getLogPageBean(SysLogParam params, PageBean<SysLogWithBLOBs> pageBean , HttpSession session){
        BeanValidator.check(pageBean);

        //处理模糊查询的参数
        SysLogParam param = null;
        if (params.getType() == null&&
                (params.getAfterSeg() == ""||params.getAfterSeg() == null) &&
                (params.getBeforeSeg() == ""||params.getBeforeSeg() == null) &&
                (params.getFromTime() == ""||params.getFromTime() == null)  &&
                (params.getOperator() == ""||params.getOperator() == null) &&
                (params.getToTime() == ""||params.getToTime() == null)){
            param = (SysLogParam) session.getAttribute("param");
        }else {
            session.setAttribute("param", params);
            param = params;
        }


        //封装成dto
        SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SysLogDto logDto = new SysLogDto();
        if (param != null){
            logDto.setType(param.getType());
            if (StringUtils.isNotBlank(param.getBeforeSeg())){
                logDto.setBeforeSeg("%"+param.getBeforeSeg()+"%");
            }
            if (StringUtils.isNotBlank(param.getAfterSeg())){
                logDto.setAfterSeg("%"+param.getAfterSeg()+"%");
            }
            if (StringUtils.isNotBlank(param.getOperator())){
                logDto.setOperator("%"+param.getOperator()+"%");
            }
            //处理时间
            try {
                if (StringUtils.isNotBlank(param.getFromTime())){
                    logDto.setFromTime(formater.parse(param.getFromTime()));
                }
                if (StringUtils.isNotBlank(param.getToTime())){
                    logDto.setToTime(formater.parse(param.getToTime()));
                }
            } catch (ParseException e) {
                throw new ParamException("日期格式错误！");
            }
        }

        PageHelper.startPage(pageBean.getPageNo(), pageBean.getPageSize());
        List<SysLogWithBLOBs> logWithBLOBs =  logMapper.selectBySearch(logDto);
        PageInfo<SysLogWithBLOBs> pageInfo = new PageInfo<>(logWithBLOBs);
        pageBean.setTotal((int) pageInfo.getTotal());
        pageBean.setData(pageInfo.getList());
        return pageBean;
    }

    //记录日志相关方法


    public void saveUserLog(SysUser before, SysUser after){
        SysLogWithBLOBs sysLogWithBLOBs = new SysLogWithBLOBs();
        sysLogWithBLOBs.setType( LogType.TYPE_USER );
        sysLogWithBLOBs.setOperateTime( new Date() );
        sysLogWithBLOBs.setOperator( ThreadUtil.getUser().getUsername() );
        sysLogWithBLOBs.setOperateIp( IpUtil.getUserIP( ThreadUtil.getRequset() ) );
        sysLogWithBLOBs.setTargetId( after==null ? before.getId():after.getId() );
        sysLogWithBLOBs.setOldValue( before==null? "": JsonMapper.obj2String( before ) );
        sysLogWithBLOBs.setNewValue( after==null? "": JsonMapper.obj2String( after ) );
        sysLogWithBLOBs.setStatus( 0 );
        logMapper.insertSelective( sysLogWithBLOBs );
    }

    public void saveAclLog(SysAcl before, SysAcl after){
        SysLogWithBLOBs sysLogWithBLOBs = new SysLogWithBLOBs();
        sysLogWithBLOBs.setType( LogType.TYPE_ACL );
        sysLogWithBLOBs.setOperateTime( new Date() );
        sysLogWithBLOBs.setOperator( ThreadUtil.getUser().getUsername() );
        sysLogWithBLOBs.setOperateIp( IpUtil.getUserIP( ThreadUtil.getRequset() ) );
        sysLogWithBLOBs.setTargetId( after==null ? before.getId():after.getId() );
        sysLogWithBLOBs.setOldValue( before==null? "": JsonMapper.obj2String( before ) );
        sysLogWithBLOBs.setNewValue( after==null? "": JsonMapper.obj2String( after ) );
        sysLogWithBLOBs.setStatus( 0 );
        logMapper.insertSelective( sysLogWithBLOBs );
    }

    //记录日志相关方法 --end

    //恢复操作相关方法

    public void recover(Integer id) {
        SysLogWithBLOBs log = logMapper.selectByPrimaryKey(id);
        if (log.getStatus() == 0){
            switch (log.getType()){
                case LogType.TYPE_DEPT:
                    SysDept oldDept = null;SysDept newDept = null;
                    if (log.getNewValue() != "" && log.getOldValue() != "") {//需要进行更新操作
                        newDept = JsonMapper.string2Obj(log.getNewValue(), new TypeReference<SysDept>() {});
                        oldDept = JsonMapper.string2Obj(log.getOldValue(), new TypeReference<SysDept>() {});
                        oldDept.setOperator(ThreadUtil.getUser().getUsername());
                        oldDept.setOperateTime(new Date());
                        oldDept.setOperateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset()));

                        deptService.recursionUpdateDept(oldDept, newDept);
                        deptService.saveDeptLog(newDept, oldDept);
                    }else if (log.getNewValue() == "" && log.getOldValue() != ""){//需要进行添加操作
                        oldDept = JsonMapper.string2Obj(log.getOldValue(), new TypeReference<SysDept>() {});
                        oldDept.setOperator(ThreadUtil.getUser().getUsername());
                        oldDept.setOperateTime(new Date());
                        oldDept.setOperateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset()));
                        deptMapper.insertSelective(oldDept);
                        deptService.saveDeptLog(null, oldDept);
                    }else if (log.getNewValue() != "" && log.getOldValue() == ""){//需要进行删除操作
                        newDept = JsonMapper.string2Obj(log.getNewValue(), new TypeReference<SysDept>() {});
                        newDept.setOperator(ThreadUtil.getUser().getUsername());
                        newDept.setOperateTime(new Date());
                        newDept.setOperateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset()));
                        deptMapper.deleteByPrimaryKey(newDept.getId());
                        deptService.saveDeptLog(newDept, null);
                    }
                    //改变log的状态
                    log.setStatus(1);
                    logMapper.updateByPrimaryKeySelective(log);
                    break;


                case LogType.TYPE_USER:
                    SysUser oldUser = null;SysUser newUser = null;
                    if (log.getOldValue() == "") {
                        newUser = JsonMapper.string2Obj(log.getNewValue(), new TypeReference<SysUser>() {});

                        oldUser = new SysUser();
                        BeanUtils.copyProperties(newUser, oldUser);

                        newUser.setOperator(ThreadUtil.getUser().getUsername());
                        newUser.setOperateTime(new Date());
                        newUser.setOperateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset()));
                        newUser.setStatus(2);
                        userMapper.updateByPrimaryKeySelective(newUser);
                        saveUserLog(oldUser, newUser);
                    }else{
                        oldUser = JsonMapper.string2Obj(log.getOldValue(), new TypeReference<SysUser>() {});
                        newUser = JsonMapper.string2Obj(log.getNewValue(), new TypeReference<SysUser>() {});
                        oldUser.setOperator(ThreadUtil.getUser().getUsername());
                        oldUser.setOperateTime(new Date());
                        oldUser.setOperateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset()));
                        userMapper.updateByPrimaryKeySelective(oldUser);
                        saveUserLog(newUser, oldUser);
                    }
                    //改变log的状态
                    log.setStatus(1);
                    logMapper.updateByPrimaryKeySelective(log);
                    break;

                case LogType.TYPE_ACL_MODULE:
                    SysAclModule oldAclModule = null;SysAclModule newAclModule = null;
                    if (log.getOldValue() == "") {
                        newAclModule = JsonMapper.string2Obj(log.getNewValue(), new TypeReference<SysAclModule>() {});

                        oldAclModule = new SysAclModule();
                        BeanUtils.copyProperties(newAclModule, oldAclModule);

                        newAclModule.setOperator(ThreadUtil.getUser().getUsername());
                        newAclModule.setOperateTime(new Date());
                        newAclModule.setOperateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset()));
                        newAclModule.setStatus(2);
                        aclModuleMapper.updateByPrimaryKeySelective(newAclModule);
                        aclModuleService.saveAclModuleLog(oldAclModule, newAclModule);
                    }else{
                        oldAclModule = JsonMapper.string2Obj(log.getOldValue(), new TypeReference<SysAclModule>() {});
                        newAclModule = JsonMapper.string2Obj(log.getNewValue(), new TypeReference<SysAclModule>() {});
                        oldAclModule.setOperator(ThreadUtil.getUser().getUsername());
                        oldAclModule.setOperateTime(new Date());
                        oldAclModule.setOperateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset()));
                        aclModuleService.recursionUpdateAclModule(oldAclModule, newAclModule);
                        aclModuleService.saveAclModuleLog(newAclModule, oldAclModule);
                    }
                    //改变log的状态
                    log.setStatus(1);
                    logMapper.updateByPrimaryKeySelective(log);
                    break;

                case LogType.TYPE_ACL:
                    SysAcl oldAcl = null;SysAcl newAcl = null;
                    if (log.getOldValue() == "") {
                        newAcl = JsonMapper.string2Obj(log.getNewValue(), new TypeReference<SysAcl>() {});

                        oldAcl = new SysAcl();
                        BeanUtils.copyProperties(newAcl, oldAcl);

                        newAcl.setOperator(ThreadUtil.getUser().getUsername());
                        newAcl.setOperateTime(new Date());
                        newAcl.setOperateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset()));
                        newAcl.setStatus(2);
                        aclMapper.updateByPrimaryKeySelective(newAcl);
                        saveAclLog(oldAcl, newAcl);
                    }else{
                        oldAcl = JsonMapper.string2Obj(log.getOldValue(), new TypeReference<SysAcl>() {});
                        newAcl = JsonMapper.string2Obj(log.getNewValue(), new TypeReference<SysAcl>() {});
                        oldAcl.setOperator(ThreadUtil.getUser().getUsername());
                        oldAcl.setOperateTime(new Date());
                        oldAcl.setOperateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset()));
                        aclMapper.updateByPrimaryKeySelective(oldAcl);
                        saveAclLog(newAcl, oldAcl);
                    }
                    //改变log的状态
                    log.setStatus(1);
                    logMapper.updateByPrimaryKeySelective(log);
                    break;

                case LogType.TYPE_ROLE:
                    SysRole oldRole = null;SysRole newRole = null;
                    if (log.getOldValue() == "") {
                        newRole = JsonMapper.string2Obj(log.getNewValue(), new TypeReference<SysRole>() {});

                        oldRole = new SysRole();
                        BeanUtils.copyProperties(newRole, oldRole);

                        newRole.setOperator(ThreadUtil.getUser().getUsername());
                        newRole.setOperateTime(new Date());
                        newRole.setOperateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset()));
                        newRole.setStatus(0);
                        roleMapper.updateByPrimaryKeySelective(newRole);
                        roleService.saveRoleLog(oldRole, newRole);
                    }else{
                        oldRole = JsonMapper.string2Obj(log.getOldValue(), new TypeReference<SysRole>() {});
                        newRole = JsonMapper.string2Obj(log.getNewValue(), new TypeReference<SysRole>() {});
                        oldRole.setOperator(ThreadUtil.getUser().getUsername());
                        oldRole.setOperateTime(new Date());
                        oldRole.setOperateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset()));
                        roleMapper.updateByPrimaryKeySelective(oldRole);
                        roleService.saveRoleLog(newRole, oldRole);
                    }
                    //改变log的状态
                    log.setStatus(1);
                    logMapper.updateByPrimaryKeySelective(log);
                    break;

                case LogType.TYPE_ROLE_ACL:
                    String oldValue = log.getOldValue();
                    String oldIds = oldValue.substring(1, oldValue.length() - 1);
                    String newValue = log.getNewValue();
                    String newIds = newValue.substring(1, newValue.length() - 1);


                    roleService.updateRoleTree(log.getTargetId(), oldIds==null ? "":oldIds);

                    //改变log的状态
                    log.setStatus(1);
                    logMapper.updateByPrimaryKeySelective(log);
                    break;

                case LogType.TYPE_ROLE_USER:
                    List<Integer> old = JsonMapper.string2Obj(log.getOldValue(), new TypeReference<List<Integer>>() {});
                    List<Integer> newii = JsonMapper.string2Obj(log.getNewValue(), new TypeReference<List<Integer>>() {});
                    roleUserService.updateRoleUsers(log.getTargetId(), old);

                    //改变log的状态
                    log.setStatus(1);
                    logMapper.updateByPrimaryKeySelective(log);
                    break;

            }
        }else {
            throw new ParamException("该部门已经恢复过数据，不能再次恢复！");
        }
    }


}

