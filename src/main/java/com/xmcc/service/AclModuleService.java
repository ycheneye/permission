package com.xmcc.service;

import com.xmcc.entity.SysAclModule;
import com.xmcc.param.SysAclModelParam;

public interface AclModuleService {

    void insertAclModule(SysAclModelParam param);


    void updateAclModule(SysAclModelParam param);

    void deleteAclModule(Integer id);

    void recursionUpdateAclModule(SysAclModule after, SysAclModule before);

    void saveAclModuleLog(SysAclModule before, SysAclModule after);
}
