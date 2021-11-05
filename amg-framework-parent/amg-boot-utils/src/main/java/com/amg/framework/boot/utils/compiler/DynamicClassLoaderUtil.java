package com.amg.framework.boot.utils.compiler;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;


public class DynamicClassLoaderUtil extends URLClassLoader {

    Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

    public DynamicClassLoaderUtil(Map<String, byte[]> classBytes) {
        super(new URL[0], DynamicClassLoaderUtil.class.getClassLoader());
        this.classBytes.putAll(classBytes);
    }

    /**
     * 对外提供的工具方法，加载指定的java源码，得到Class对象
     * @param javaSrc java源码
     * @return
     */
    public static Class<?> load(String javaSrc) throws ClassNotFoundException {
        /**
         * 先试用动态编译工具，编译java源码，得到类的全限定名和class字节码的字节数组信息
         */
        Map<String, byte[]> bytecode = DynamicCompiler.compile(javaSrc);
        if(bytecode != null) {
            /**
             * 传入动态类加载器
             */
            DynamicClassLoaderUtil classLoader = new DynamicClassLoaderUtil(bytecode);
            /**
             * 加载得到Class对象
             */
            return classLoader.loadClass(bytecode.keySet().iterator().next());
        } else {
            throw new ClassNotFoundException("can not found class");
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] buf = classBytes.get(name);
        if (buf == null) {
            return super.findClass(name);
        }
        classBytes.remove(name);
        return defineClass(name, buf, 0, buf.length);
    }

    public static void main(String[] args) throws ClassNotFoundException {
        load("java源码内容");
    }

}
