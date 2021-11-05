package com.amg.framework.cloud.grpc.proxy;

import javassist.*;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


@Component
public class CircuitProxy {

    private static Logger logger = LoggerFactory.getLogger(CircuitProxy.class);

    private static ConcurrentHashMap hashMap = new ConcurrentHashMap();


    public static void buildCircuit(Resource resource) {
        if (resource.getFilename().endsWith("BlockingStub.class")
                && !isExist(resource)) {
            CtClass stubClass = null; // 获取Stub类
            try {
                ClassPool pool = ClassPool.getDefault();
                pool.appendClassPath(new LoaderClassPath(Thread.currentThread().getContextClassLoader()));
                String classPath = getClassPath(resource);
                stubClass = pool.getCtClass(classPath);
                CtMethod[] ctMethods = stubClass.getDeclaredMethods();

                // 获取rpc方法
                for(CtMethod cm : ctMethods) {
                    if (!cm.getName().equals("build")) {
                        CtMethod mnew = CtNewMethod.copy(cm, cm.getName() + "#" + UUID.randomUUID().toString(), stubClass, null);
                        stubClass.addMethod(mnew);
                        StringBuilder sb = new StringBuilder();
                        sb.append("{return ");
                        sb.append("(");
                        sb.append(cm.getReturnType().getName());
                        sb.append(")");
                        sb.append("com.amg.framework.cloud.grpc.thread.CircuitExecute.circuit(this, \"");
                        sb.append(mnew.getName());
                        sb.append("\",  new Object[]{");
                        CtClass[] parameters = cm.getParameterTypes();
                        for (int i = 0; i < parameters.length; i ++) {
                            sb.append("$" + (i + 1) + (i == parameters.length - 1 ? "" : ","));
                        }
                        sb.append("});}");
                        cm.setBody(sb.toString());
                    }
                }
                stubClass.toClass();
               // stubClass.detach();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static boolean isExist(Resource resource) {
        String classPath = getClassPath(resource);
        if (hashMap.containsKey(classPath)) {
            return true;
        } else {
            hashMap.put(classPath, classPath);
            return false;
        }
    }


    private static String getClassPath(Resource resource) {
        String classPath = resource.getDescription().replaceAll("\\/", ".").replaceAll("\\\\", ".");
        String[] split = classPath.split(".com.");
        classPath = "com." + split[split.length - 1].replaceAll(".class]", "");
        return classPath;
    }

}
