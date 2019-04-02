package com.xmcc.utils;


import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xmcc.exception.ParamException;
import org.apache.commons.collections4.MapUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

//定义BeanValidator类：用于校验JavaBean 参数是否合法问题
public class BeanValidator {

    //创建Validator工厂
    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    /**
     * 验证普通javabean对象
     * @param t 泛型
     * @param groups
     * @param <T> 定义泛型
     * @return Map集合 ，key是属性名 value是错误信息
     */
    public static <T> Map<String, String> validate(T t, Class... groups) {
        Validator validator = validatorFactory.getValidator();
        Set validateResult = validator.validate(t, groups);

        if (validateResult.isEmpty()) {
            //如果结果为空 表示没有错误 直接返回一个空的Map集合
            return Collections.emptyMap();
        } else {
            //如果不为空，那么将错误信息封装到Map集合中 返回Map集合
            LinkedHashMap errors = Maps.newLinkedHashMap();//相当于 new LinkedHashMap();
            //遍历结果封装数据
            Iterator iterator = validateResult.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation violation = (ConstraintViolation)iterator.next();
                errors.put(violation.getPropertyPath().toString(), violation.getMessage());
            }
            return errors;
        }
    }

    /**
     * 验证List集合中的javaBean
     * @param collection  集合
     * @return Map集合
     */
    public static Map<String, String> validateList(Collection<?> collection) {
        //google给我们提供的一个工具类：用于判断集合是否为空，如果为空 直接抛出异常
        Preconditions.checkNotNull(collection);
        Iterator iterator = collection.iterator();
        Map errors;

        do {
            if (!iterator.hasNext()) {
                return Collections.emptyMap();
            }
            Object object = iterator.next();
            //循环验证集合中的每个对象
            errors = validate(object, new Class[0]);
        } while (errors.isEmpty());

        return errors;
    }

    /**
     * 通用的验证方法
     * @param first
     * @param objects
     * @return
     */
    public static Map<String, String> validateObject(Object first, Object... objects) {
        if (objects != null && objects.length > 0) {
            return validateList(Lists.asList(first, objects));
        } else {
            return validate(first, new Class[0]);
        }
    }

    /**
     * 根据验证结果的处理方法
     * @param param
     * @throws ParamException
     */
    public static void check(Object param) throws ParamException {
        Map<String, String> map = BeanValidator.validateObject(param);
        //如果Map部位空 说明 参数出现异常
        if (MapUtils.isNotEmpty(map)) {
            //抛出异常 交给全局异常处理器处理
            throw new ParamException(map.toString());
        }
    }
}
