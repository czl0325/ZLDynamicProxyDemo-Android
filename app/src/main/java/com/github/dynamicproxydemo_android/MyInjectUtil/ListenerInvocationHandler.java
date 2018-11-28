package com.github.dynamicproxydemo_android.MyInjectUtil;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

public class ListenerInvocationHandler implements InvocationHandler {
    //activity   真实对象
    private Context context;
    private Map<String,Method> methodMap;

    public ListenerInvocationHandler(Context context, Map<String, Method> methodMap) {
        this.context = context;
        this.methodMap = methodMap;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String name = method.getName();
        //决定是否需要进行代理
        Method metf = methodMap.get(name);
        Object ret = null;
        if (metf != null) {
            try {
                ret = metf.invoke(context, args);
                Log.e("czl","success-->>" + method.getName());
            } catch (Exception e) {
                Log.e("czl","fail-->>" + e.getLocalizedMessage());
            }
            return ret;
        } else {
            return method.invoke(proxy, args);
        }
    }
}
