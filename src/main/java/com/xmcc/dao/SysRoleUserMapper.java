package com.xmcc.dao;

import com.xmcc.entity.SysRoleUser;
import com.xmcc.entity.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;


public interface SysRoleUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleUser record);

    int insertSelective(SysRoleUser record);

    SysRoleUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleUser record);

    int updateByPrimaryKey(SysRoleUser record);

    void deleteUserByRoleId(@Param("roleId") int roleId);

    void bathInsert(@Param("roleUsers")ArrayList<SysRoleUser> roleUsers);
}