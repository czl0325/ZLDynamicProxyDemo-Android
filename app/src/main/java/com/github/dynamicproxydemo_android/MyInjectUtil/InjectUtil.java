package com.github.dynamicproxydemo_android.MyInjectUtil;

import android.content.Context;
import android.view.View;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class InjectUtil {
    public static void inject(Context context) {
        injectLayout(context);
        injectViews(context);
        injectEvents(context);
    }

    private static void injectLayout(Context context) {
        Class<?> clazz = context.getClass();
        ContentView contentView = clazz.getAnnotation(ContentView.class);
        if (contentView != null) {
            int layoutId = contentView.value();
            try {
                Method method = clazz.getMethod("setContentView", int.class);
                method.invoke(context, layoutId);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private static void injectViews(Context context) {
        //获取activity
        Class<?> clazz = context.getClass();
        //获取到MainActivity里面所有的成员变量 包含 textView
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            BindView bindView = field.getAnnotation(BindView.class);
            if (bindView == null) {
                continue;
            }
            int viewId = bindView.value();
            try {
                Method method = clazz.getMethod("findViewById",int.class);
                View view = (View) method.invoke(context, viewId);
                field.setAccessible(true);
                field.set(context, view);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //注入事件
    private static void injectEvents(Context context) {
        //获取activity
        Class<?> clazz = context.getClass();
        //获取activity的所有方法
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Annotation[] annotations = method.getAnnotations();
            for (Annotation annotation : annotations) {
                //获取注解 anntionType   OnClick  OnLongClck
                Class<?> annotationType = annotation.annotationType();
                //获取注解的注解   onClick 注解上面的EventBase
                EventBase eventBase = annotationType.getAnnotation(EventBase.class);
                if (eventBase == null) {
                    continue;
                }
                /*
                开始获取事件三要素  通过反射注入进去
                1 listenerSetter  返回     setOnClickListener字符串
                 */
                String listenerSetter = eventBase.listenerSetter();
                //得到 listenerType--》 View.OnClickListener.class,
                Class<?> listenerType = eventBase.listenerType();
                //callMethod--->onClick
                String callbackMethod = eventBase.callbackMethod();

                Map<String, Method> methodMap = new HashMap<>();
                methodMap.put(callbackMethod, method);

                try {
                    Method valueMethod = annotationType.getDeclaredMethod("value");
                    int[] viewIds = (int[])valueMethod.invoke(annotation);
                    for (int viewId : viewIds) {
                        //通过反射拿到控件
                        Method findViewById = clazz.getMethod("findViewById",int.class);
                        View view = (View)findViewById.invoke(context, viewId);
                        if (view == null) {
                            continue;
                        }
                        Method setOnClickListener = view.getClass().getMethod(listenerSetter, listenerType);
                        ListenerInvocationHandler handler = new ListenerInvocationHandler(context, methodMap);
                        /**
                         * 类比 于
                         * textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                         */
                        Object proxy = Proxy.newProxyInstance(listenerType.getClassLoader(),
                                new Class[]{listenerType}, handler);
                        setOnClickListener.invoke(view, proxy);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
