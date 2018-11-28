package com.github.dynamicproxydemo_android.MyInjectUtil;

import android.view.View;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@EventBase( listenerSetter = "setOnLongClickListener",
            listenerType = View.OnLongClickListener.class,
            callbackMethod = "onLongClick")
public @interface OnLongClick {
    int[] value() default -1;
}
