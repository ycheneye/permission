package com.xmcc.dao;

import com.xmcc.entity.SysUser;
import com.xmcc.param.SysUserParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser findByName(@Param("username") String username);

    List<SysUser> selectUserByDeptId(@Param("deptId") int id);

    List<SysUser> selectUserListByRoleId(@Param("roleId") int roleId);

    List<SysUser> findAllUser();
}