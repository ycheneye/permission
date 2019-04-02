package com.xmcc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xmcc.beans.PageBean;
import com.xmcc.dao.SysAclMapper;
import com.xmcc.entity.SysAcl;
import com.xmcc.exception.ParamException;
import com.xmcc.param.SysAclParam;
import com.xmcc.service.AclService;
import com.xmcc.utils.BeanValidator;
import com.xmcc.utils.IpUtil;
import com.xmcc.utils.ThreadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;


@Service
public class AclServiceImpl implements AclService {

    @Resource
    private SysAclMapper aclMapper;

    @Autowired
    private LogService logService;

    @Override
    public PageBean<SysAcl> getPageBean(PageBean<SysAcl> pageBean, int aclModuleId) {
        BeanValidator.check(pageBean);

        PageHelper.startPage(pageBean.getPageNo(), pageBean.getPageSize());
        List<SysAcl> sysAcls = aclMapper.selectAclByAclModuleId(aclModuleId);
        PageInfo<SysAcl> pageInfo = new PageInfo<>(sysAcls);
        pageBean.setData(pageInfo.getList());
        pageBean.setTotal((int) pageInfo.getTotal());
        return pageBean;
    }

    @Override
    public void insertAcl(SysAclParam param) {
        BeanValidator.check(param);

        String name = param.getName();//得到权限名
        SysAcl sysAcl = aclMapper.findByName(name);
        if (sysAcl != null){
            throw new ParamException("权限名称不能相同");
        }

        // 生成专属代码
        Date operate_time = new Date();
        SimpleDateFormat formater = new SimpleDateFormat("yyyyMMddHHmmss_");
        String code = formater.format(operate_time);

        SysAcl acl = SysAcl.builder().name(param.getName()).aclModuleId(param.getAclModuleId()).url(param.getUrl()).
                remark(param.getRemark()).seq(param.getSeq()).status(param.getStatus()).type(param.getType()).code(code+new Random().nextInt(100)).
                operateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset())).operateTime(operate_time).operator(ThreadUtil.getUser().getUsername()).build();
        aclMapper.insertSelective(acl);
        logService.saveAclLog(null, acl);
    }

    @Override
    public void updateAcl(SysAclParam param) {
        BeanValidator.check(param);
        SysAcl acl = SysAcl.builder().id(param.getId()).name(param.getName()).aclModuleId(param.getAclModuleId()).url(param.getUrl()).
                remark(param.getRemark()).seq(param.getSeq()).status(param.getStatus()).type(param.getType()).
                operateIp(IpUtil.getRemoteIp(ThreadUtil.getRequset())).operateTime(new Date()).operator(ThreadUtil.getUser().getUsername()).build();

        SysAcl oldAcl = aclMapper.selectByPrimaryKey(param.getId());
        if (!oldAcl.getName().equals(param.getName())){
            if (aclMapper.findByName(acl.getName()) != null){
                throw new ParamException("权限名称不能相同");
            }
        }

        aclMapper.updateByPrimaryKeySelective(acl);
        logService.saveAclLog(oldAcl, acl);
    }
}
