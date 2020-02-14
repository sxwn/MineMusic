package com.xiaowei.libcompiler;

import com.google.auto.service.AutoService;
import com.xiaowei.libpermission.PermissionDenied;
import com.xiaowei.libpermission.PermissionGrant;
import com.xiaowei.libpermission.PermissionRational;

import java.io.IOException;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

/**
 * 注解处理器
 */
@AutoService(Process.class)
public class RuntimePermissionAbstractProcessor extends AbstractProcessor {
    private Elements elementUtils;
    private Messager messager;

    private HashMap<String, MethodInfo> methodMap = new HashMap<>();
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        elementUtils = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
        filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        methodMap.clear();

        messager.printMessage(Diagnostic.Kind.NOTE, "process start ...");
        if(!handleAnnotationInfo(roundEnv, PermissionGrant.class)) {
            return false;
        };
        if(!handleAnnotationInfo(roundEnv, PermissionDenied.class)) {
            return false;
        };
        if(!handleAnnotationInfo(roundEnv, PermissionRational.class)) {
            return false;
        };

        for (String className : methodMap.keySet()) {
            MethodInfo methodInfo = methodMap.get(className);
            try {
                JavaFileObject sourceFile = filer.createSourceFile(methodInfo.packageName + "." + methodInfo.fileName);
                Writer writer = sourceFile.openWriter();
                writer.write(methodInfo.generateJavaCode());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                messager.printMessage(Diagnostic.Kind.NOTE, "write file failed:" + e.getMessage());
            }
        }

        messager.printMessage(Diagnostic.Kind.NOTE, "process end ...");

        return false;
    }

    private boolean handleAnnotationInfo(RoundEnvironment roundEnv, Class<? extends Annotation> annotation) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
        for (Element element : elements) {
            if (!checkMethodValidator(element, annotation)) {
                return false;
            }
            ExecutableElement methodElement = (ExecutableElement) element;
            TypeElement enclosingElement = (TypeElement) methodElement.getEnclosingElement();
            String className = enclosingElement.getQualifiedName().toString();

            MethodInfo methodInfo = methodMap.get(className);
            if (methodInfo == null) {
                methodInfo = new MethodInfo(elementUtils, enclosingElement);
                methodMap.put(className, methodInfo);
            }

            Annotation annotationClazz = methodElement.getAnnotation(annotation);
            String methodName = methodElement.getSimpleName().toString();
            List<? extends VariableElement> parameters = methodElement.getParameters();

            if (parameters == null || parameters.size() < 1) {
                String message = "the method %s marked by annotation %s must have an nuique parameter [String[] permissions]";
                throw new IllegalArgumentException(String.format(message, methodName, annotationClazz.getClass().getSimpleName()));
            }

            if (annotationClazz instanceof PermissionGrant) {
                int requestCode = ((PermissionGrant) annotationClazz).value();
                methodInfo.grantMethodMap.put(requestCode, methodName);
            } else if (annotationClazz instanceof PermissionDenied) {
                int requestCode = ((PermissionDenied) annotationClazz).value();
                methodInfo.deniedMethodMap.put(requestCode, methodName);
            } else if (annotationClazz instanceof PermissionRational) {
                int requestCode = ((PermissionRational) annotationClazz).value();
                methodInfo.rationalMethodMap.put(requestCode, methodName);
            }
        }
        return true;
    }

    private boolean checkMethodValidator(Element element, Class<? extends Annotation> annotation) {
        if (element.getKind() != ElementKind.METHOD) {
            return false;
        }
        if (ClassValidator.isPrivate(element) || ClassValidator.isAbstract(element)) {
            return false;
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportList = new HashSet<String>();
        supportList.add(PermissionGrant.class.getCanonicalName());
        supportList.add(PermissionDenied.class.getCanonicalName());
        supportList.add(PermissionRational.class.getCanonicalName());
        return supportList;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
