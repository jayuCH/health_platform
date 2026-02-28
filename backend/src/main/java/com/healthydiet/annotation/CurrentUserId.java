package com.healthydiet.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 当前登录用户ID注解
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUserId {

    /**
     * 是否必需，默认true
     */
    @AliasFor("required")
    boolean value() default true;

    /**
     * 是否必需
     */
    boolean required() default true;
}
