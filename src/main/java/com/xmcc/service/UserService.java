package com.xmcc.service;

import com.xmcc.beans.PageBean;
import com.xmcc.entity.SysUser;
import com.xmcc.param.SysUserParam;
import com.xmcc.param.SysUserParam2;

public interface UserService {
    SysUser login(SysUserParam param);

    void insertUser(SysUserParam2 param2);

    PageBean<SysUser> getPageBean(PageBean<SysUser> pageBean,int id);

    void updateUser(SysUserParam2 param2);
}
