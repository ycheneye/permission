package com.xmcc.dao;

import com.xmcc.dto.SysLogDto;
import com.xmcc.entity.SysLog;
import com.xmcc.entity.SysLogWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysLogWithBLOBs record);

    int insertSelective(SysLogWithBLOBs record);

    SysLogWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysLogWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(SysLogWithBLOBs record);

    int updateByPrimaryKey(SysLog record);

    List<SysLogWithBLOBs> selectBySearch(@Param("logDto") SysLogDto logDto);
}