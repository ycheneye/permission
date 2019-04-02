package com.xmcc.service;

import com.xmcc.beans.PageBean;
import com.xmcc.entity.SysRole;
import com.xmcc.entity.SysUser;
import com.xmcc.param.SysRoleParam;

import java.util.List;

public interface RoleService {
    List<SysRole> showRole();

    void insertRole(SysRoleParam param);

    void updateRole(SysRoleParam param);

    void updateRoleTree(int roleId, String aclIds);

    void saveRoleLog(SysRole before, SysRole after);

    void saveRoleAclLog(int roleid,List<Integer> before, List<Integer> after);
}
