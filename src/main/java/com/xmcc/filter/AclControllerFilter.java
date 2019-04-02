package com.xmcc.filter;

import com.xmcc.entity.SysAcl;
import com.xmcc.entity.SysUser;
import com.xmcc.service.impl.CoreService;
import com.xmcc.utils.ApplicationContextHelper;
import com.xmcc.utils.JsonData;
import com.xmcc.utils.JsonMapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AclControllerFilter implements Filter {

    private static final String noAuthUrl = "/sys/user/noAuth.page";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.setCharacterEncoding("utf-8");
        String uri = request.getRequestURI();//获取请求路径

        if (uri.contains("login")||uri.contains("signin")||uri.contains("bootstrap")||uri.contains("noAuth")){
            filterChain.doFilter(request, response);
            return;
        }else if (!uri.contains("/sys/log")){
            request.getSession().removeAttribute("param");
        }
        // 拿到用户对应的权限,使用工具类调用service
        CoreService coreService = ApplicationContextHelper.popBean(CoreService.class);
        if (!coreService.hasAcl(uri)){
            //判断请求是何种请求,根据请求的不同返回不同的情况
            if (uri.endsWith(".json")){
                JsonData fail = JsonData.fail("没有操作权限");
                response.setHeader("Content-Type","application/json;charset=utf-8");
                response.getWriter().write(JsonMapper.obj2String(fail));
            }else {
                response.setHeader("Content-Type", "text/html");
                response.getWriter().print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                        + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n"
                        + "<title>跳转中...</title>\n" + "</head>\n" + "<body>\n" + "跳转中，请稍候...\n" + "<script type=\"text/javascript\">//<![CDATA[\n"
                        + "window.location.href='" + noAuthUrl + "?ret='+encodeURIComponent(window.location.href);\n" + "//]]></script>\n" + "</body>\n" + "</html>\n");
            }
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
