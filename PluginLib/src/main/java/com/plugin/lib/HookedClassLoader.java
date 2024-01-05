package com.plugin.lib;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

public class HookedClassLoader extends ClassLoader implements InvocationHandler {
    // 构造方法
    public HookedClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        System.out.println(className);
        // 在加载类之前进行判断，如果是需要 hook 的类，则执行 hook 操作
        if (className.equals("java.lang.System")) {
            return hookSystemClass();
        }
        return super.loadClass(className);
    }

    // 自定义 hook System.loadLibrary() 的逻辑
    private Class<?> hookSystemClass() throws ClassNotFoundException {
        try {
            Class<?> systemClass = findLoadedClass("java.lang.System");
            if (systemClass != null) {
                Method loadLibraryMethod = systemClass.getDeclaredMethod("loadLibrary", String.class);
                loadLibraryMethod.setAccessible(true);
                Object proxy = Proxy.newProxyInstance(
                        HookedClassLoader.class.getClassLoader(),
                        new Class<?>[]{loadLibraryMethod.getDeclaringClass()},
                        this);

                // 替换原始的 loadLibrary 方法为代理对象
                Field modifiersField = Field.class.getDeclaredField("modifiers");
                modifiersField.setAccessible(true);
                modifiersField.setInt(loadLibraryMethod, loadLibraryMethod.getModifiers() & ~Modifier.FINAL);
                Field methodAccessorField = Method.class.getDeclaredField("methodAccessor");
                methodAccessorField.setAccessible(true);
                methodAccessorField.set(loadLibraryMethod, null);
                loadLibraryMethod.invoke(systemClass, proxy);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 返回系统类，继续执行原始的加载逻辑
        return Class.forName("java.lang.System", false, getParent());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("事件");
        if (args != null && args.length > 0 && args[0] instanceof String) {
            String libraryName = (String) args[0];
            System.out.println("调用了 loadLibrary 方法，参数为：" + libraryName);
        }
        return method.invoke(System.class, args);
    }
}
