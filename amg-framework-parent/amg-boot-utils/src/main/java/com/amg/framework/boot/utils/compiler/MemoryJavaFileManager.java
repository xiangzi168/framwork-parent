package com.amg.framework.boot.utils.compiler;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.nio.CharBuffer;
import java.util.*;


public class MemoryJavaFileManager extends ForwardingJavaFileManager<JavaFileManager> {

    // compiled classes in bytes:
    final Map<String, byte[]> classBytes = new HashMap<String, byte[]>();

    final Map<String, List<JavaFileObject>> classObjectPackageMap = new HashMap<>();

    MemoryJavaFileManager(JavaFileManager fileManager) {
        super(fileManager);
    }

    public Map<String, byte[]> getClassBytes() {
        return new HashMap<String, byte[]>(this.classBytes);
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
        classBytes.clear();
    }

    @Override
    public Iterable<JavaFileObject> list(Location location,
                                         String packageName,
                                         Set<JavaFileObject.Kind> kinds,
                                         boolean recurse)
            throws IOException {
        Iterable<JavaFileObject> it = super.list(location, packageName, kinds, recurse);

        if (kinds.contains(JavaFileObject.Kind.CLASS)) {
            final List<JavaFileObject> javaFileObjectList = classObjectPackageMap.get(packageName);
            if (javaFileObjectList != null) {
                if (it != null) {
                    for (JavaFileObject javaFileObject : it) {
                        javaFileObjectList.add(javaFileObject);
                    }
                }
                return javaFileObjectList;
            } else {
                return it;
            }
        } else {
            return it;
        }
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof MemoryInputJavaClassObject) {
            return ((MemoryInputJavaClassObject) file).inferBinaryName();
        }
        return super.inferBinaryName(location, file);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind,
                                               FileObject sibling) throws IOException {
        if (kind == JavaFileObject.Kind.CLASS) {
            return new MemoryOutputJavaClassObject(className);
        } else {
            return super.getJavaFileForOutput(location, className, kind, sibling);
        }
    }

    JavaFileObject makeStringSource(String className, final String code) {
        String classPath = className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension;

        return new SimpleJavaFileObject(URI.create("string:///" + classPath), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharBuffer getCharContent(boolean ignoreEncodingErrors) {
                return CharBuffer.wrap(code);
            }
        };
    }

    void makeBinaryClass(String className, final byte[] bs) {
        JavaFileObject javaFileObject = new MemoryInputJavaClassObject(className, bs);

        String packageName = "";
        int pos = className.lastIndexOf('.');
        if (pos > 0) {
            packageName = className.substring(0, pos);
        }
        List<JavaFileObject> javaFileObjectList = classObjectPackageMap.get(packageName);
        if (javaFileObjectList == null) {
            javaFileObjectList = new LinkedList<>();
            javaFileObjectList.add(javaFileObject);

            classObjectPackageMap.put(packageName, javaFileObjectList);
        } else {
            javaFileObjectList.add(javaFileObject);
        }
    }

    class MemoryInputJavaClassObject extends SimpleJavaFileObject {
        final String className;
        final byte[] bs;

        MemoryInputJavaClassObject(String className, byte[] bs) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
            this.className = className;
            this.bs = bs;
        }

        @Override
        public InputStream openInputStream() {
            return new ByteArrayInputStream(bs);
        }

        public String inferBinaryName() {
            return className;
        }
    }

    class MemoryOutputJavaClassObject extends SimpleJavaFileObject {
        final String className;

        MemoryOutputJavaClassObject(String className) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.CLASS.extension), Kind.CLASS);
            this.className = className;
        }

        @Override
        public OutputStream openOutputStream() {
            return new FilterOutputStream(new ByteArrayOutputStream()) {
                @Override
                public void close() throws IOException {
                    out.close();
                    ByteArrayOutputStream bos = (ByteArrayOutputStream) out;
                    byte[] bs = bos.toByteArray();
                    classBytes.put(className, bs);
                    makeBinaryClass(className, bs);
                }
            };
        }
    }
}