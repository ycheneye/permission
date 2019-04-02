package com.xmcc.dao;

import com.xmcc.entity.SysRoleAcl;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;

public interface SysRoleAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleAcl record);

    int insertSelective(SysRoleAcl record);

    SysRoleAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleAcl record);

    int updateByPrimaryKey(SysRoleAcl record);

    void deleteByRoleId(@Param("roleId") int roleId);

    void insertByRoleAclList(@Param("roleAclsList") ArrayList<SysRoleAcl> roleAclsList);
}