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
public class SysAclModelParam{

    private Integer id;

    @NotBlank(message = "名称不可以为空")
    @Length(max = 15,min = 2,message = "长度需要在2-15个字之间")
    private String name;

    private Integer parentId;

    @NotNull(message = "顺序不可以为空")
    private Integer seq;

    @NotNull(message = "状态不可以为空")
    private Integer status;

    @Length(max = 150,message = "备注需要在150个字以内")
    private String remark;

}
