package com.xmcc.param;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class SysLogParam {

    private Integer type;
    private String beforeSeg;
    private String afterSeg;
    private String operator;
    private String fromTime;
    private String toTime;

}
