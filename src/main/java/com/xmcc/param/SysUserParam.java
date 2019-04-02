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
public class SysUserParam {
    private Integer id;

    @NotBlank(message = "用户名不可以为空")
    @Length(max = 20,message = "用户名长度需要在20个字以内")
    private String username;

    @NotBlank(message = "密码不可以为空")
    @Length(max = 13,message = "密码长度需要在13个字以内")
    private String password;

}
