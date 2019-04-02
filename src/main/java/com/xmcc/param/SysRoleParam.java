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
public class SysRoleParam {
    private Integer id;

    @NotBlank(message = "角色名称不能为空")
    @Length(min = 2,max = 20,message = "名称长度需要在2-20个字之间")
    private String name;

    private Integer type;
    @NotNull(message = "角色状态必须指定")
    private Integer status;

    @Length(max = 150,message = "备注长度需要在150字以内")
    private String remark;
}
