package com.xmcc.service;


import com.xmcc.entity.SysDept;
import com.xmcc.param.SysDeptParam;


public interface DeptService {

    void insertDept(SysDeptParam param);

    void updateDept(SysDeptParam param);

    void deleteDept(int id);

    void recursionUpdateDept(SysDept after, SysDept before);

    void saveDeptLog(SysDept before, SysDept after);
}
