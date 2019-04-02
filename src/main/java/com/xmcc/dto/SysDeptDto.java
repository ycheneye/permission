package com.xmcc.dto;

import com.xmcc.entity.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@ToString
public class SysDeptDto extends SysDept {
    private List<SysDeptDto> deptList = new ArrayList<>();

    //把SysDept的数据封装到SysDeptDto
    public static SysDeptDto adaptor(SysDept sysDept){
        SysDeptDto sysDeptDto = new SysDeptDto();
        BeanUtils.copyProperties(sysDept, sysDeptDto);
        return sysDeptDto;
    }
}
