package com.xmcc.dto;

import com.xmcc.entity.SysLogWithBLOBs;
import lombok.*;

import java.util.Date;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class SysLogDto extends SysLogWithBLOBs {
    private Integer type;
    private String beforeSeg;
    private String afterSeg;
    private Date fromTime;
    private Date toTime;
}
