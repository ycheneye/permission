package com.xmcc.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.xmcc.beans.CachePreFix;
import com.xmcc.dao.SysAclMapper;
import com.xmcc.dao.SysUserMapper;
import com.xmcc.entity.SysAcl;
import com.xmcc.entity.SysUser;
import com.xmcc.utils.JsonMapper;
import com.xmcc.utils.ThreadUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoreService {

    @Autowired
    private SysAclMapper aclMapper;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private CacheService cacheService;

    //跟据当前登录用户查询出所对应的权限
    public List<SysAcl> findAclByUserId(int userId){

        if (isSuperManager(userId)){
            return aclMapper.findAllAcl();
        }
        List<SysAcl> acls =  aclMapper.findAclByUserId(userId);
        return acls;
    }


    //判断前台传过来的是哪个角色，从而获取对应的权限
    public List<SysAcl> findAclByRoleId(int roleId){
        List<SysAcl> acls =  aclMapper.findAclByRoleId(roleId);
        return acls;
    }

    //判断当前登录用户是否为超级管理员
    private boolean isSuperManager(int userId){
        SysUser user = userMapper.selectByPrimaryKey(userId);
        if (user.getUsername().contains("Admin")){
            return true;
        }
        return false;
    }



    /**
     * 判断当前登录用户是否拥有某权限
     * @param uri 请求路径
     * @return
     */
    public boolean hasAcl(String uri) {
        Integer id = ThreadUtil.getUser().getId();
        if (isSuperManager(id)) {
            return true;
        }

        //判断请求路径是否被控制起来，因为有些路径在数据库里是没有被规定的，这种路径是公共的
        if (aclMapper.findByUrl(uri) == null) {
            return true;
        }

        //先从缓存读
        String data = cacheService.readCache("userAcls", CachePreFix.SYS);
        List<SysAcl> acls = null;
        if (StringUtils.isBlank(data)) {
            acls = findAclByUserId(id);
            cacheService.setCache(JsonMapper.obj2String(acls) , 86400, "userAcls",CachePreFix.SYS);
        }else {
            acls = JsonMapper.string2Obj(data, new TypeReference<List<SysAcl>>() {});//把从缓存读取的String类型的json数据转换成对象
        }

        for (SysAcl acl : acls) {
            if (acl.getUrl().contains(uri)) {
                return true;
            }
        }

        return false;
    }
}
