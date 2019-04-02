package com.xmcc.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
public class SysAclParam {
    private Integer id;

    @NotBlank(message = "名称不可以为空")
    @Length(min = 2,max = 20,message = "长度需要在2-20个字之间")
    private String name;

    @NotNull(message = "权限模块需要指定")
    private Integer aclModuleId;

    @NotNull(message = "顺序需要指定")
    private Integer seq;

    @Length(max = 150,message = "备注长度需要在150个字以内")
    private String remark;

    @NotNull(message = "状态需要指定")
    private Integer status;

    @Length(min = 6,max = 100,message = "链接长度需要在6-100个字之间")
    private String url;

    @NotNull(message = "种类需要指定")
    private Integer type;

}
