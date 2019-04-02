package com.xmcc.controller;

import com.xmcc.beans.PageBean;
import com.xmcc.entity.SysLogWithBLOBs;
import com.xmcc.param.SysLogParam;
import com.xmcc.service.impl.LogService;
import com.xmcc.utils.JsonData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/sys/log")
@Slf4j
public class LogController {

    @Autowired
    private LogService logService;

    @RequestMapping("/log.page")
    public ModelAndView noAuth(){
        return new ModelAndView("log");
    }


    @RequestMapping("/page.json")
    @ResponseBody
    public JsonData getPageBean(PageBean<SysLogWithBLOBs> pageBean, SysLogParam params , HttpSession session){
        PageBean<SysLogWithBLOBs> logPageBean = logService.getLogPageBean(params, pageBean ,session);
        return JsonData.success(logPageBean);
    }

    @RequestMapping("/recover.json")
    @ResponseBody
    public JsonData recover(Integer id){
        logService.recover(id);
        return JsonData.success();
    }
}
