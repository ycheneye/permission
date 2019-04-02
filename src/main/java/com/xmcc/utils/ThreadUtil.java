package com.xmcc.utils;

import com.xmcc.entity.SysUser;

import javax.servlet.http.HttpServletRequest;

public class ThreadUtil {
    public static ThreadLocal<SysUser> userHolder = new ThreadLocal<>();
    public static ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<>();

    public static void addUser(SysUser user){
        userHolder.set(user);
    }

    public static void addRequest(HttpServletRequest request){
        requestHolder.set(request);
    }

    public static SysUser getUser(){
        return userHolder.get();
    }
    public static HttpServletRequest getRequset(){
        return requestHolder.get();
    }

    public static void untying(){
        userHolder.remove();
        requestHolder.remove();
    }
}
