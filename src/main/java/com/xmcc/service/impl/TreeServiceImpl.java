package com.xmcc.service.impl;

import com.google.common.collect.ArrayListMultimap;
import com.xmcc.dao.SysAclMapper;
import com.xmcc.dao.SysAclModuleMapper;
import com.xmcc.dao.SysDeptMapper;
import com.xmcc.dto.SysAclDto;
import com.xmcc.dto.SysAclModuleDto;
import com.xmcc.dto.SysDeptDto;
import com.xmcc.entity.SysAcl;
import com.xmcc.entity.SysAclModule;
import com.xmcc.entity.SysDept;
import com.xmcc.service.TreeService;
import com.xmcc.utils.LevelUtil;
import com.xmcc.utils.ThreadUtil;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
@Slf4j
public class TreeServiceImpl<T> implements TreeService<T> {

    @Autowired
    private SysDeptMapper sysDeptMapper;

    @Autowired
    private SysAclModuleMapper aclModuleMapper;

    @Autowired
    private SysAclMapper aclMapper;

    @Autowired
    private CoreService coreService;

    @Override
    public List<SysDeptDto> generateDeptTree() {
        List<SysDept> allDept = sysDeptMapper.findAllDept();//查找全部部门
        ArrayList<SysDeptDto> deptDtos = new ArrayList<>();

        //转换成dto的集合
        for (SysDept sysDept: allDept) {
            SysDeptDto deptDto = SysDeptDto.adaptor(sysDept);
            deptDtos.add(deptDto);
        }
        return classifyDept(deptDtos);
    }

    @Override
    public List<SysAclModuleDto> generateAclModuleTree() {
        List<SysAclModule> allAclModule = aclModuleMapper.findAllAclModule();
        ArrayList<SysAclModuleDto> aclModuleDtos = new ArrayList<>();

        for (SysAclModule aclModule : allAclModule) {
            SysAclModuleDto acModuleDto = SysAclModuleDto.adaptor(aclModule);
            aclModuleDtos.add(acModuleDto);
        }
        Class<?> aclModuleDto = null;
        try {
            aclModuleDto = Class.forName("com.xmcc.dto.SysAclModuleDto");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classify((ArrayList<T>) aclModuleDtos, aclModuleDto);
    }

    @Override
    public List<SysAclModuleDto> generateUserAclTree(int roleId) {
        //查询出所有的权限，封装到dto里去
        List<SysAcl> allAcl = aclMapper.findAllAcl();
        ArrayList<SysAclDto> aclDtos = new ArrayList<>();
        for (SysAcl acl : allAcl) {
            SysAclDto aclDto = SysAclDto.adaptor(acl);
            aclDtos.add(aclDto);
        }


        List<SysAcl> userAcls = coreService.findAclByUserId(ThreadUtil.getUser().getId());
        List<SysAcl> roleAcls = coreService.findAclByRoleId(roleId);
        ArrayListMultimap<Integer, SysAclDto> multimap = ArrayListMultimap.create();
        for (SysAclDto dto : aclDtos) {

            //根据当前登录用户拥有的权限，设置每条权限的hasAcl
            if (userAcls.contains(dto)) {//注意：这里比较的是地址是否包含，我们需要比较的不是地址包含，就必须要重写SysAcl类的EqualsAndHashCode
                dto.setHasAcl(true);
            }
            //根据传入的roleId设置每条权限的checked
            if (roleAcls.contains(dto)) {
                dto.setChecked(true);
            }

            //把权限分类，不同的权限模块包装好各自的权限点
            multimap.put(dto.getAclModuleId(), dto);
        }

        List<SysAclModuleDto> aclModuleTree = generateAclModuleTree();//先获取一个权限模块树
        //用递归为每个权限模块添加自己的权限点
        recursionmoduleAddAcl(aclModuleTree, multimap);

        return aclModuleTree;//这就是一个包含了权限点的权限模块树
    }

    //把部门分类
    public List<SysDeptDto> classifyDept(ArrayList<SysDeptDto> deptDtos){
        ArrayListMultimap<String, SysDeptDto> multimap = ArrayListMultimap.create();
        ArrayList<SysDeptDto> rootList = new ArrayList<>();//放置根节点(主部门)
        for (SysDeptDto deptDto:deptDtos) {
            if (deptDto.getLevel().equals("0")){//表示你是主部门
                rootList.add(deptDto);
            }
            multimap.put(deptDto.getLevel(), deptDto);
        }
        //为主部门排序
        Collections.sort(rootList,new SortList());
        recursionDeptDto(rootList, multimap);
        return rootList;
    }

    //分类方法
    public List classify(ArrayList<T> dtos,Class<?> c) {
        ArrayListMultimap<String, T> multimap = ArrayListMultimap.create();
        ArrayList<T> rootList = new ArrayList<>();//放置根节点
        Method getLevel = null;
        try {
            getLevel = c.getMethod("getLevel");
            for (T t : dtos) {
                if (getLevel.invoke(t).equals("0")) {//表示你是主部门
                    rootList.add(t);
                }
                multimap.put((String) getLevel.invoke(t), t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        recursion(rootList, multimap,c);
        return rootList;
    }

    //递归生成树
    public void recursionDeptDto(List<SysDeptDto> rootList,ArrayListMultimap<String, SysDeptDto> multimap){
        for (SysDeptDto deptDto:rootList) {//先操作主部门
            String nextLevel = LevelUtil.contact(deptDto.getLevel(), deptDto.getId());//拿到主部门的下一层级部门的level
            List<SysDeptDto> nextDepts = multimap.get(nextLevel);//拿到主部门下对应的小部门们
            if (nextDepts != null){
                //先按照seq排序
                Collections.sort(nextDepts,new SortList());

                deptDto.setDeptList(nextDepts);

                //万一小部门下也有部门怎么办，采用递归来实现
                recursionDeptDto(nextDepts, multimap);
            }
        }
    }

    public void recursion(List<T> rootList,ArrayListMultimap<String, T> multimap,Class<?> c){
        try {
            Method getId = c.getMethod("getId");
            Method getLevel = c.getMethod("getLevel");
            Field aclModuleList = c.getDeclaredField("aclModuleList");
            aclModuleList.setAccessible(true);
            String name= (aclModuleList == null)?"DeptList":"AclModuleList";
            for (T t:rootList) {
                String nextLevel =  LevelUtil.contact((String) getLevel.invoke(t), (Integer) getId.invoke(t));
                List<T> nexts = multimap.get(nextLevel);
                if (nexts != null){
                    Method setList = c.getMethod("set"+name,List.class);
                    setList.invoke(t,(Object) nexts);
                    recursion(nexts, multimap,c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void recursionmoduleAddAcl(List<SysAclModuleDto> aclModuleTree ,ArrayListMultimap<Integer, SysAclDto> multimap ){
        if (aclModuleTree == null){
            return;
        }
        for (SysAclModuleDto aclModuleDto: aclModuleTree) {
            aclModuleDto.setAclList(multimap.get(aclModuleDto.getId()));
            recursionmoduleAddAcl(aclModuleDto.getAclModuleList(), multimap);
        }
    }

    //提取出匿名子类对象写成内部类
    class SortList implements Comparator<SysDeptDto>{

        @Override
        public int compare(SysDeptDto o1, SysDeptDto o2) {
            return o1.getSeq()-o2.getSeq();
        }
    }
}
