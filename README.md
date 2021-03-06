# ZLDynamicProxyDemo-Android
使用动态代理来实现类似butterknife的功能


原本我们写一个按钮的点击事件，应该是这么写的。

```JAVA
        Button btn = findViewById(R.id.btn1);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "触发了单击事件", Toast.LENGTH_SHORT).show();
            }
        });
```

上面有几个公有的参数，比如说setOnClickListener代表调用方法，内部相应类是View.OnClickListener.class，内部的实现方法是onClick(View v)，可以提取出来，写成一个注解文件，如下：

```JAVA
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
```

再写两个注解类：

```JAVA
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@EventBase( listenerSetter = "setOnClickListener",
            listenerType = View.OnClickListener.class,
            callbackMethod = "onClick")
public @interface OnClick {
    //设置哪些控件的id需要点击
    int []value();
}
```

```JAVA
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@EventBase( listenerSetter = "setOnLongClickListener",
            listenerType = View.OnLongClickListener.class,
            callbackMethod = "onLongClick")
public @interface OnLongClick {
    int[] value();
}
```

一个用来实现OnClick，一个用来实现OnLongClick，方法都在EventBase里面配置好了。

然后就可以开始动态代理了，注入的时候看你想要的是单击事件还是长按事件。

```JAVA
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
```

调用同butterknife

```JAVA
    @OnClick(R.id.btn1)
    public void myTouch(View view) {
        Toast.makeText(this, "触发了单击事件", Toast.LENGTH_SHORT).show();
    }
 
    @OnLongClick(R.id.btn1)
    public boolean myLongTouch(View view) {
        Toast.makeText(this, "触发了长按事件", Toast.LENGTH_SHORT).show();
        return true;
    }
```   
当然butterknife不是通过动态代理来实现的，他的效率比动态代理高了很多。
