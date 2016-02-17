package com.easyshop.bean.express.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface ExpressParam {

    /**
     * 是否必填
     */
    public boolean isRequired() default false;

    /**
     * 字段需要的长度
     */
    public int length() default 0;

    /**
     * 是否需要拼接
     * 
     * @return
     */
    public boolean join() default false;

    public ExpressParamType paramType() default ExpressParamType.String;
}
