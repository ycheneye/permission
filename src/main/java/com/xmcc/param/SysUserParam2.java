package com.xmcc.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;


@Getter
@Setter
@ToString
public class SysUserParam2 {

    private Integer id;

    @NotBlank(message = "用户名不可以为空")
    @Length(max = 20,message = "用户名长度需要在20个字以内")
    private String username;

    @NotBlank(message = "电话不可以为空")
    @Length(max = 13,message = "电话长度需要在13个字以内")
    private String telephone;

    @NotBlank(message = "邮箱不可以为空")
    @Length(max = 50,message = "邮箱长度需要在50个字以内")
    private String mail;

    @NotNull(message = "用户部门不可以为空")
    private Integer deptId;

    @Length(max = 200,message = "备注长度需要在200个字以内")
    private String remark;


    @NotNull(message = "用户状态必须指定")
    private Integer status;
}
