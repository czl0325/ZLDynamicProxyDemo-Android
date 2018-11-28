package com.github.dynamicproxydemo_android.MyInjectUtil;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@EventBase( listenerSetter = "setOnClickListener",
            listenerType = View.OnClickListener.class,
            callbackMethod = "onClick")
public @interface OnClick {
    //设置哪些控件的id需要点击
    int []value() default -1;
}
