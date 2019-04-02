package com.xmcc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmcc.beans.PageBean;
import com.xmcc.dao.SysUserMapper;
import com.xmcc.entity.SysUser;
import com.xmcc.exception.ParamException;
import com.xmcc.param.SysUserParam;
import com.xmcc.param.SysUserParam2;
import com.xmcc.service.UserService;
import com.xmcc.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private LogService logService;

    @Override
    public SysUser login(SysUserParam param) {
        BeanValidator.check(param);
        String username = param.getUsername();
        String password = param.getPassword();
        String errorMsg="";
        String pwd = null;
        try {
            pwd = Md5Util.encodeByMd5(password);
        } catch (Exception e) {
            e.printStackTrace();
        }

        SysUser user = userMapper.findByName(username);
        if (user == null){
            errorMsg = "用户不存在";
        }else if (!user.getPassword().equals(pwd)){
            errorMsg = "用户密码不正确";
        }else if (user.getStatus() != 1){
            errorMsg = "用户状态异常";
        }else {
            return user;
        }
        throw new ParamException(errorMsg);
    }

    @Override
    public void insertUser(SysUserParam2 param2) {
        BeanValidator.check(param2);
        String username = param2.getUsername();//得到账户名
        SysUser user = userMapper.findByName(username);//查找是否存在同名
        if (user!=null){
            throw new ParamException("账户名称不能相同");
        }
        SysUser sysUser = null;
        String initPwd = null;
        try {
            //将相关属性设置进去
            initPwd = PasswordUtil.randomPassword();//生成初始密码
            sysUser = SysUser.builder().username(param2.getUsername()).telephone(param2.getTelephone()).mail(param2.getMail())
                    .deptId(param2.getDeptId()).status(param2.getStatus()).remark(param2.getRemark()).password(Md5Util.encodeByMd5(initPwd))
                    .operateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset())).operateTime(new Date()).operator(ThreadUtil.getUser().getUsername()).build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int i = userMapper.insertSelective(sysUser);
        if (i == 1){//添加成功
            //记录日志
            logService.saveUserLog(null, sysUser);

            MailUtils.sendMail(sysUser.getMail(), "恭喜您成功我司的一份子，您的初始登录密码为："+initPwd+"，登录后请记得修改密码哟。", "小码聪聪邮件");
        }else {
            throw new ParamException("添加失败，请稍后重试！");
        }

    }

    @Override
    public PageBean<SysUser> getPageBean(PageBean<SysUser> pageBean,int deptId) {
        BeanValidator.check(pageBean);

        PageHelper.startPage(pageBean.getPageNo(), pageBean.getPageSize());
        List<SysUser> users = userMapper.selectUserByDeptId(deptId);//查找到对应部门下的员工
        PageInfo<SysUser> pageInfo = new PageInfo<>(users);
        pageBean.setTotal((int) pageInfo.getTotal());
        pageBean.setData(users);

        return pageBean;
    }

    @Override
    public void updateUser(SysUserParam2 param2) {
        BeanValidator.check(param2);
        SysUser user = SysUser.builder().id(param2.getId()).username(param2.getUsername()).telephone(param2.getTelephone()).mail(param2.getMail())
                .deptId(param2.getDeptId()).status(param2.getStatus()).remark(param2.getRemark()).operateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset()))
                .operateTime(new Date()).operator(ThreadUtil.getUser().getUsername()).build();

        SysUser oldUser = userMapper.selectByPrimaryKey(param2.getId());
        if (!oldUser.getUsername().equals(param2.getUsername())){//判断有没有修改用户名
            if (userMapper.findByName(user.getUsername()) != null){
                throw new ParamException("不能存在名称相同的用户");
            }
        }
        userMapper.updateByPrimaryKeySelective(user);
        //记录日志
        logService.saveUserLog(oldUser, user);
    }
}
