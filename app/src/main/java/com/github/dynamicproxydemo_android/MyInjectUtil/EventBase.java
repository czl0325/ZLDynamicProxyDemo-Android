package com.github.dynamicproxydemo_android.MyInjectUtil;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)//注解的注解
public @interface EventBase {
    //监听的方法
    String listenerSetter();
    //事件类型
    Class<?> listenerType();
    //回调方法
    String callbackMethod();
}
