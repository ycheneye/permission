package com.xmcc.controller;


import com.xmcc.beans.PageBean;
import com.xmcc.entity.SysUser;
import com.xmcc.param.SysUserParam;
import com.xmcc.param.SysUserParam2;
import com.xmcc.service.UserService;
import com.xmcc.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
@RequestMapping("/sys/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping("/login.page")
    @ResponseBody
    public ModelAndView login(SysUserParam param, HttpSession session) throws IOException {
        SysUser user = userService.login(param);
        session.setAttribute("loggingUser", user);
        log.info(user.toString());
        return new ModelAndView("admin");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData insertUser(SysUserParam2 param2){
        userService.insertUser(param2);
        return JsonData.success();
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData getPageBean(PageBean<SysUser> pageBean,int deptId){
        PageBean<SysUser> bean = userService.getPageBean(pageBean,deptId);
        return JsonData.success(bean);
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateUser(SysUserParam2 param2){
        userService.updateUser(param2);
        return JsonData.success();
    }

    @RequestMapping("/noAuth.page")
    public ModelAndView noAuth(){
        return new ModelAndView("noAuth");
    }
}
