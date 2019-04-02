package com.xmcc.controller;


import com.xmcc.beans.PageBean;
import com.xmcc.entity.SysAcl;
import com.xmcc.entity.SysUser;
import com.xmcc.param.SysAclParam;
import com.xmcc.param.SysUserParam2;
import com.xmcc.service.AclService;
import com.xmcc.utils.JsonData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/sys/acl")
public class AclController {

    @Resource
    private AclService aclService;

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData getPageBean(PageBean<SysAcl> pageBean, int aclModuleId){
        PageBean<SysAcl> bean = aclService.getPageBean(pageBean,aclModuleId);
        return JsonData.success(bean);
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData insertUser(SysAclParam param){
        aclService.insertAcl(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateAcl(SysAclParam param){
        aclService.updateAcl(param);
        return JsonData.success();
    }
}
