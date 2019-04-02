package com.xmcc.dao;

import com.xmcc.entity.SysAcl;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

public interface SysAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAcl record);

    int insertSelective(SysAcl record);

    SysAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAcl record);

    int updateByPrimaryKey(SysAcl record);

    List<SysAcl> selectAclByAclModuleId(@Param("acModuleId") Integer id);

    SysAcl findByName(@Param("name") String name);

    List<SysAcl> findAclByUserId(@Param("userId")int userId);

    List<SysAcl> findAllAcl();

    List<SysAcl> findAclByRoleId(@Param("roleId")int roleId);

    SysAcl findByUrl(@Param("uri") String uri);
}