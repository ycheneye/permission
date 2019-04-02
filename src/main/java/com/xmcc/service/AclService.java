package com.xmcc.service;

import com.xmcc.beans.PageBean;
import com.xmcc.entity.SysAcl;
import com.xmcc.param.SysAclParam;

public interface AclService {

    PageBean<SysAcl> getPageBean(PageBean<SysAcl> pageBean, int aclModuleId);

    void insertAcl(SysAclParam param);

    void updateAcl(SysAclParam param);
}
