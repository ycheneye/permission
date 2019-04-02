package com.xmcc.service;

import com.xmcc.dto.SysAclModuleDto;
import com.xmcc.dto.SysDeptDto;
import com.xmcc.entity.SysAclModule;

import java.util.List;

public interface TreeService<T> {
    List<SysDeptDto> generateDeptTree();

    List<SysAclModuleDto> generateAclModuleTree();

    List<SysAclModuleDto> generateUserAclTree(int roleId);
}
