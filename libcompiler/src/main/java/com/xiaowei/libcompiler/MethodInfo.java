package com.xiaowei.libcompiler;

import java.util.HashMap;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

public class MethodInfo {

    private static final String PROXY_NAME = "PermissionProxy";
    private String fileName;

    private String packageName;
    private String className;
    public HashMap<Integer, String> grantMethodMap = new HashMap<>();
    public HashMap<Integer, String> deniedMethodMap = new HashMap<>();
    public HashMap<Integer, String> rationalMethodMap = new HashMap<>();

    public MethodInfo(Elements elementUtils, TypeElement typeElement) {
        PackageElement packageElement = elementUtils.getPackageOf(typeElement);
        packageName = packageElement.getQualifiedName().toString();
        className = ClassValidator.getClassName(typeElement,packageName);

        fileName = className +"$$" + PROXY_NAME;
    }

    public void generateJavaCode() {
        StringBuilder builder = new StringBuilder();
        builder.append("// generate code. do not modify\n");
        builder.append("package ").append(packageName).append(";\n\n");
        builder.append("import ");

    }
}
