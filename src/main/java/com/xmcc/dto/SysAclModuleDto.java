package com.xmcc.dto;

import com.xmcc.entity.SysAclModule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class SysAclModuleDto extends SysAclModule {

    private List<SysAclModuleDto> aclModuleList = new ArrayList<>();//用于存储子权限模块

    private List<SysAclDto> aclList;//用于存储子权限

    public static SysAclModuleDto adaptor(SysAclModule aclModule){
        SysAclModuleDto acModuleDto = new SysAclModuleDto();
        BeanUtils.copyProperties(aclModule, acModuleDto);
        return acModuleDto;
    }
}
