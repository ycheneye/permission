package com.xmcc.filter;

import com.xmcc.entity.SysUser;
import com.xmcc.utils.ThreadUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class LoginFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.setCharacterEncoding("utf-8");

        String uri = request.getRequestURI();//获取请求路径
        if (uri.contains("login")||uri.contains("signin")||uri.contains("bootstrap")){
            filterChain.doFilter(request, response);
        }else {
            SysUser user = (SysUser) request.getSession().getAttribute("loggingUser");
            if (user != null){
                //开始绑定已经登录的用户和此次请求
                ThreadUtil.addUser(user);
                ThreadUtil.addRequest(request);
                filterChain.doFilter(request, response);
            }else {
                response.sendRedirect("/signin.jsp");
                return;
            }
        }
    }

    @Override
    public void destroy() {

    }
}
