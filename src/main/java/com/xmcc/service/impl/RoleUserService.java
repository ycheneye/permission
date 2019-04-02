package com.xmcc.service.impl;

import com.xmcc.beans.LogType;
import com.xmcc.dao.SysLogMapper;
import com.xmcc.dao.SysRoleUserMapper;
import com.xmcc.dao.SysUserMapper;
import com.xmcc.entity.SysLogWithBLOBs;
import com.xmcc.entity.SysRoleUser;
import com.xmcc.entity.SysUser;
import com.xmcc.utils.IpUtil;
import com.xmcc.utils.JsonMapper;
import com.xmcc.utils.ThreadUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RoleUserService {

    @Autowired
    private SysUserMapper userMapper;
    @Autowired
    private SysRoleUserMapper roleUserMapper;

    @Autowired
    private SysLogMapper logMapper;

    public Map getUserMap(int roleId){
        //根据角色id查询该角色下状态正常的用户
        List<SysUser> users = userMapper.selectUserListByRoleId(roleId);
        //查询出所有状态正常的用户
        List<SysUser> allUser = userMapper.findAllUser();
        allUser.removeAll(users);

        HashMap<String, List<SysUser>> map = new HashMap<>();
        map.put("unselected", allUser);
        map.put("selected", users);
        return map;
    }

    public void updateRoleUsers(int roleId, List<Integer> userIds) {
        List<SysUser> users = userMapper.selectUserListByRoleId(roleId);
        ArrayList<Integer> oldIds = new ArrayList<>();
        for (SysUser user:users) {
            oldIds.add(user.getId());
        }
        if (userIds.size() == users.size()){
            oldIds.removeAll(userIds);
            if (CollectionUtils.isEmpty(oldIds)){
                return;
            }
        }
        //先移除该角色下所有的用户
        roleUserMapper.deleteUserByRoleId(roleId);
        if (userIds.size() > 0){
            ArrayList<SysRoleUser> roleUsers = new ArrayList<>();
            for (Integer id: userIds) {
                SysRoleUser roleUser = SysRoleUser.builder().roleId(roleId).userId(id).
                        operateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset())).operateTime(new Date()).operator(ThreadUtil.getUser().getUsername()).build();
                roleUsers.add(roleUser);
            }
            roleUserMapper.bathInsert(roleUsers);
            saveRoleUserLog(roleId, oldIds, userIds);
        }else {
            saveRoleUserLog(roleId, oldIds, userIds);
        }
    }

    public void saveRoleUserLog(int roleid,List<Integer> before, List<Integer> after){
        SysLogWithBLOBs sysLogWithBLOBs = new SysLogWithBLOBs();
        sysLogWithBLOBs.setType( LogType.TYPE_ROLE_USER);
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
