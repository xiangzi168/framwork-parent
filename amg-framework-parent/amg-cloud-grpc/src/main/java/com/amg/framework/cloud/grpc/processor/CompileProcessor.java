package com.amg.framework.cloud.grpc.processor;

import com.amg.framework.boot.utils.reflect.ReflectionUtils;
import com.google.auto.service.AutoService;
import com.sun.tools.javac.processing.JavacProcessingEnvironment;
import com.sun.tools.javac.processing.JavacRoundEnvironment;
import com.sun.tools.javac.util.Options;
import net.devh.boot.grpc.server.service.GrpcService;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.io.File;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;


@AutoService(Processor.class)
public class CompileProcessor extends AbstractProcessor {

    private Types mTypeUtils;
    private Messager mMessager;
    private Filer mFiler;
    private Elements mElementUtils;
    private String serverName;

    public CompileProcessor() {
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mTypeUtils = processingEnvironment.getTypeUtils();
        mMessager = processingEnvironment.getMessager();
        mFiler = processingEnvironment.getFiler();
        mElementUtils = processingEnvironment.getElementUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(GrpcService.class.getCanonicalName());
        return annotations;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        JavacRoundEnvironment javacRoundEnvironment = (JavacRoundEnvironment) roundEnvironment;
        Object obj = ReflectionUtils.getFieldValue(javacRoundEnvironment, "processingEnv");
        JavacProcessingEnvironment javacProcessingEnvironment = (JavacProcessingEnvironment)obj;
        Options options = Options.instance(javacProcessingEnvironment.getContext());
        File sourcePath = new File(options.get("-s"));
        String source = sourcePath.getParentFile().getParentFile().getParentFile().getParentFile().getPath();
        serverName = (source.split(Pattern.quote(System.getProperty("file.separator")))[source.split(Pattern.quote(System.getProperty("file.separator"))).length - 1]).split("\\-")[1];
        // 收集所有源码文件
        CompileCheck.colloc(source);
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(GrpcService.class);
        for (Element e : elements) {
            TypeElement typeElement = (TypeElement) e;
            Name fullClassName = typeElement.getQualifiedName();
            // 获取源码路径
            String filePath = CompileCheck.sorcuesMap.get(fullClassName.toString());
            if (!CompileCheck.checkMethod(fullClassName.toString(), filePath, serverName)) {
                throw new CompileException("Class " + fullClassName.toString() + " of proto entity must contain parameters 'string requestId、 string errorCode、 string msg、 bool success、 * data'" +
                        " and Method must contain parameters generic 'StreamObserver<...>' and Method must mark annotation '@Override'");
            }
        }
        return false;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

}

