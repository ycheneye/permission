package com.xmcc.utils;

import com.xmcc.exception.ParamException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        ModelAndView mv = null;
        String uri = request.getRequestURI().toString();
        String errorMsg = "系统异常，请联系管理员";
        if(uri.endsWith(".page")){
            if(ex instanceof ParamException){
                JsonData data = JsonData.fail(ex.getMessage());
                mv = new ModelAndView("/WEB-INF/views/exception.jsp", data.toMap());
                mv.addObject("jsonData",data.toMap() );
            }else{
                JsonData data = JsonData.fail(ex.getMessage());
                mv = new ModelAndView("/WEB-INF/views/exception.jsp");
                mv.addObject("jsonData",data.toMap() );
            }
        }else {
            if(ex instanceof ParamException){
                JsonData data = JsonData.fail(ex.getMessage());
                mv = new ModelAndView("jsonView", data.toMap());
            }else{
                JsonData data = JsonData.fail(ex.getMessage());
                mv = new ModelAndView("jsonView", data.toMap());
            }
        }

        return mv;
    }
}
