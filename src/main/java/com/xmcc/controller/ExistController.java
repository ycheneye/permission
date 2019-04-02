package com.xmcc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class ExistController {
    @RequestMapping("/logout.page")
    public ModelAndView logOut(HttpSession session){
        session.invalidate();
        return new ModelAndView("redirect:signin.jsp");

    }
}
