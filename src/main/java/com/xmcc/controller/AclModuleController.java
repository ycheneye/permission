package com.xmcc.controller;


import com.xmcc.param.SysAclModelParam;
import com.xmcc.param.SysDeptParam;
import com.xmcc.service.AclModuleService;
import com.xmcc.service.TreeService;
import com.xmcc.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/sys/aclModule")
@Slf4j
public class AclModuleController {

    @Resource
    private TreeService treeService;

    @Resource
    private AclModuleService aclModuleService;


    @RequestMapping("/acl.page")
    public ModelAndView login(){
        return new ModelAndView("acl");
    }


    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData generateDeptTree(){
        List aclModuleTree = treeService.generateAclModuleTree();
        return JsonData.success(aclModuleTree);
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(SysAclModelParam param){
        aclModuleService.insertAclModule(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateDept(SysAclModelParam param){
        aclModuleService.updateAclModule(param);
        return JsonData.success();
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData updateDept(int id){
        aclModuleService.deleteAclModule(id);
        return JsonData.success();
    }
}
