package com.xmcc.dao;

import com.xmcc.entity.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    int countByParentAndDeptName(@Param("parentId") Integer parentId,@Param("deptName") String deptName,@Param("deptId") Integer deptId);

    List<SysDept> findAllDept();

    List<SysDept> selectDeptByLevel(@Param("level") String level);
}