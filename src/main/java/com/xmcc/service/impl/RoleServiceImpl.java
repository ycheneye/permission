package com.xmcc.service.impl;


import com.xmcc.beans.LogType;
import com.xmcc.dao.SysLogMapper;
import com.xmcc.dao.SysRoleAclMapper;
import com.xmcc.dao.SysRoleMapper;
import com.xmcc.entity.SysAcl;
import com.xmcc.entity.SysLogWithBLOBs;
import com.xmcc.entity.SysRole;
import com.xmcc.entity.SysRoleAcl;
import com.xmcc.exception.ParamException;
import com.xmcc.param.SysRoleParam;
import com.xmcc.service.RoleService;
import com.xmcc.utils.BeanValidator;
import com.xmcc.utils.IpUtil;
import com.xmcc.utils.JsonMapper;
import com.xmcc.utils.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private SysRoleMapper roleMapper;

    @Autowired
    private SysLogMapper logMapper;

    @Autowired
    private CoreService coreService;

    @Autowired
    private SysRoleAclMapper roleAclMapper;

    @Override
    public List<SysRole> showRole() {
        return roleMapper.findAllRole();
    }

    @Override
    public void insertRole(SysRoleParam param) {
        BeanValidator.check(param);
        SysRole role = SysRole.builder().name(param.getName()).remark(param.getRemark()).status(param.getStatus()).type(param.getType()).
                operateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset())).operateTime(new Date()).operator(ThreadUtil.getUser().getUsername()).build();
        if (roleMapper.findByName(role.getName()) != null){
            throw new ParamException("角色名称不能相同!");
        }
        roleMapper.insertSelective(role);
        saveRoleLog(null, role);
    }

    @Override
    public void updateRole(SysRoleParam param) {
        BeanValidator.check(param);
        SysRole role = SysRole.builder().id(param.getId()).name(param.getName()).remark(param.getRemark()).status(param.getStatus()).type(param.getType()).
                operateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset())).operateTime(new Date()).operator(ThreadUtil.getUser().getUsername()).build();

        SysRole oldRole = roleMapper.selectByPrimaryKey(param.getId());
        if (!oldRole.getName().equals(param.getName())){
            if (roleMapper.findByName(role.getName()) != null){
                throw new ParamException("角色名称不能相同!");
            }
        }
        roleMapper.updateByPrimaryKeySelective(role);
        saveRoleLog(oldRole, role);
    }

    /**
     * 这个方法写复杂了 难得改  相关逻辑参考角色用户的更新
     * @param roleId
     * @param aclIds
     */
    @Override
    public void updateRoleTree(int roleId, String aclIds) {
        List<SysAcl> roleAcls = coreService.findAclByRoleId(roleId);//拿到传过来的角色对应的权限
        String[] newAclIds = aclIds.split(",");
        ArrayList<String> oldList = new ArrayList<>();
        List<String> aclIdList = Arrays.asList(newAclIds);

        for (SysAcl acl : roleAcls) {
            oldList.add(String.valueOf(acl.getId()));
        }

        if (roleAcls.size() == newAclIds.length) {
            oldList.removeAll(aclIdList);//从老的id们里移除新的id
            if (oldList.size() == 0) {
                return;
            }
        }
        //先删除该角色下的所有权限
        roleAclMapper.deleteByRoleId(roleId);
        //再为该角色添加选择的权限
        ArrayList<SysRoleAcl> roleAclsList = new ArrayList<>();
        for (int i = 0; i < newAclIds.length; i++) {
            SysRoleAcl roleAcl = SysRoleAcl.builder().aclId(Integer.parseInt(newAclIds[i])).roleId(roleId).
                    operateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset())).operateTime(new Date()).operator(ThreadUtil.getUser().getUsername()).build();
            roleAclsList.add(roleAcl);
        }
        roleAclMapper.insertByRoleAclList(roleAclsList);

        //把两个String类型的集合转换成Integer类型
        ArrayList<Integer> oldList1 = new ArrayList<>();
        for (String s : oldList) {
            oldList1.add(Integer.parseInt(s));
        }

        ArrayList<Integer> newList1 = new ArrayList<>();
        for (String s : aclIdList) {
            newList1.add(Integer.parseInt(s));
        }
        saveRoleAclLog(roleId, oldList1, newList1);
    }


    @Override
    public void saveRoleLog(SysRole before, SysRole after){
        SysLogWithBLOBs sysLogWithBLOBs = new SysLogWithBLOBs();
        sysLogWithBLOBs.setType( LogType.TYPE_ROLE );
        sysLogWithBLOBs.setOperateTime( new Date() );
        sysLogWithBLOBs.setOperator( ThreadUtil.getUser().getUsername() );
        sysLogWithBLOBs.setOperateIp( IpUtil.getUserIP( ThreadUtil.getRequset() ) );
        sysLogWithBLOBs.setTargetId( after==null ? before.getId():after.getId() );
        sysLogWithBLOBs.setOldValue( before==null? "": JsonMapper.obj2String( before ) );
        sysLogWithBLOBs.setNewValue( after==null? "": JsonMapper.obj2String( after ) );
        sysLogWithBLOBs.setStatus( 0 );
        logMapper.insertSelective( sysLogWithBLOBs );
    }

    @Override
    public void saveRoleAclLog(int roleid,List<Integer> before, List<Integer> after){
        SysLogWithBLOBs sysLogWithBLOBs = new SysLogWithBLOBs();
        sysLogWithBLOBs.setType( LogType.TYPE_ROLE_ACL);
        sysLogWithBLOBs.setOperateTime( new Date() );
        sysLogWithBLOBs.setOperator( ThreadUtil.getUser().getUsername() );
        sysLogWithBLOBs.setOperateIp( IpUtil.getUserIP( ThreadUtil.getRequset() ) );
        sysLogWithBLOBs.setTargetId(roleid);
        sysLogWithBLOBs.setOldValue( before==null? "": JsonMapper.obj2String( before ) );
        sysLogWithBLOBs.setNewValue( after==null? "": JsonMapper.obj2String( after ) );
        sysLogWithBLOBs.setStatus( 0 );
        logMapper.insertSelective( sysLogWithBLOBs );
    }
}
