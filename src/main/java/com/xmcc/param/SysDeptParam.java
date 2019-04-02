package com.xmcc.param;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Setter
@Getter
@ToString
public class SysDeptParam {

    private Integer id;

    @NotBlank(message = "不可以为空")
    @Length(max = 15,min = 2,message = "长度需要在2-15个字之间")
    private String name;

    private Integer parentId;

    @NotNull(message = "不可以为空")
    private Integer seq;

    @Length(max = 150,message = "备注需要在150个字以内")
    private String remark;


}
