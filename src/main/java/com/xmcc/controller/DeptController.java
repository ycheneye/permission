package com.xmcc.controller;


import com.xmcc.dto.SysDeptDto;
import com.xmcc.param.SysDeptParam;
import com.xmcc.service.DeptService;
import com.xmcc.service.TreeService;
import com.xmcc.utils.JsonData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping("/sys/dept")
public class DeptController {

    @Resource
    private DeptService deptService;
    @Resource
    private TreeService treeService;

    @RequestMapping("/dept.page")
    public ModelAndView login(){
        return new ModelAndView("dept");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JsonData save(SysDeptParam param){
        deptService.insertDept(param);
        return JsonData.success();
    }


    @RequestMapping("/tree.json")
    @ResponseBody
    public JsonData generateDeptTree(){
        List<SysDeptDto> deptTree = treeService.generateDeptTree();
        return JsonData.success(deptTree);
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JsonData updateDept(SysDeptParam param){
        deptService.updateDept(param);
        return JsonData.success();
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JsonData updateDept(int id){
        deptService.deleteDept(id);
        return JsonData.success();
    }
}
