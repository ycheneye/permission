package com.xmcc.dao;

import com.xmcc.entity.SysAclModule;
import com.xmcc.entity.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAclModule record);

    int insertSelective(SysAclModule record);

    SysAclModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);

    List<SysAclModule> findAllAclModule();

    int countByParentAndaclModuleName(@Param("parentId") Integer parentId, @Param("aclModuleName")String aclModuleName, @Param("aclModuleId")Integer aclModuleId);

    List<SysAclModule> selectAclModuleByLevel(@Param("level") String level);
}