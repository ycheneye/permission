package com.xmcc.dto;

import com.xmcc.entity.SysAcl;
import lombok.*;
import org.springframework.beans.BeanUtils;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class SysAclDto extends SysAcl {
    private boolean checked;
    private boolean hasAcl;

    public static SysAclDto adaptor(SysAcl acl){
        SysAclDto aclDto = new SysAclDto();
        BeanUtils.copyProperties(acl, aclDto);
        return aclDto;
    }
}
