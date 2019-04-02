package com.xmcc.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;

@Slf4j
public class JsonMapper {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 通用配置 序列化：对象-----json串   反序列化：json串-------对象
        //设置在反序列化时忽略在JSON字符串中存在，而在Java中不存在的属性
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //设置Jackson序列化时只包含不为空的对象
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        //设置json的日期格式
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }

    public static <T> String obj2String(T src) {
        if (src == null) {
            return null;
        }
        try {
            //如果传入的对象本身就是一个字符串，那么直接返回，如果不是，进行转换后返回
            return src instanceof String ? (String) src : objectMapper.writeValueAsString(src);
        } catch (Exception e) {
            //通过日志记录异常信息
            log.warn("parse object to String exception, error:{}", e);
            return null;
        }
    }

    /**
     *  泛型类型在编译的时候都会别转成Object不会留下任何预设对象的信息，所以必须要通过这种方式才能获取到泛型类型
     *  使用TypeReference可以明确的指定反序列化的类型
     *  这个东西我们会用就可以了，如果感兴趣，可以简单百度一下但是不要把心思放在这个上面
     * @return
     */
    public static <T> T string2Obj(String src, TypeReference<T> typeReference) {
        if (src == null || typeReference == null) {
            return null;
        }
        try {
            return (T) (typeReference.getType().equals(String.class) ? src : objectMapper.readValue(src, typeReference));
        } catch (Exception e) {
//            log.warn("parse String to Object exception, String:{}, TypeReference<T>:{}, error:{}", src, typeReference.getType(), e);
            return null;
        }
    }
}
