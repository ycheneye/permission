package com.xmcc.controller;


import com.xmcc.dto.SysAclModuleDto;
import com.xmcc.entity.SysRole;
import com.xmcc.param.SysRoleParam;
import com.xmcc.service.RoleService;
import com.xmcc.service.TreeService;
import com.xmcc.service.impl.RoleUserService;
import com.xmcc.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys/role")
@Slf4j
public class RoleController {

    @Resource
    private RoleService roleService;

    @Autowired
    private TreeService treeService;

    @Autowired
    private RoleUserService roleUserService;

    @RequestMapping("/role.page")
    public String intoRole(){
        return "role";
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JsonData showRole(){
        List<SysRole> roles = roleService.showRole();
        return JsonData.success(roles);
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData insertRole(SysRoleParam param){
        roleService.insertRole(param);
        return JsonData.success();
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateRole(SysRoleParam param){
        roleService.updateRole(param);
        return JsonData.success();
    }

    @RequestMapping("/users.json")
    @ResponseBody
    public JsonData showUserByRoleId(@RequestParam("roleId") int roleId){
        Map map = roleUserService.getUserMap(roleId);
        return JsonData.success(map);
    }

    @RequestMapping("/roleTree.json")
    @ResponseBody
    public JsonData showRoleTree(int roleId){
        List<SysAclModuleDto> list = treeService.generateUserAclTree(roleId);
        return JsonData.success(list);
    }

    @RequestMapping("/changeAcls.json")
    @ResponseBody
    public JsonData updateRoleTree(int roleId,String aclIds){
        roleService.updateRoleTree(roleId,aclIds);
        return JsonData.success();
    }

    @RequestMapping("/changeUsers.json")
    @ResponseBody
    public JsonData updateRoleUsers(int roleId,@RequestParam("userIds") List<Integer> userIds){
        roleUserService.updateRoleUsers(roleId,userIds);
        return JsonData.success();
    }

}
