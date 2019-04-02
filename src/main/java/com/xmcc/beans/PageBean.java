package com.xmcc.beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Min;
import java.util.List;

@ToString
@Getter
@Setter
@AllArgsConstructor
public class PageBean<T> {

    @Min(value = 1,message = "大于等于1")
    private int pageNo = 1;//当前页

    @Min(value = 10,message = "大于等于10")
    private int pageSize = 10;//每页显示几行

//    private int offset;//偏移量


    private Integer total;


    private List<T> data;
}
