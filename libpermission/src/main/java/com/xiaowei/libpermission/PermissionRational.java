package com.xiaowei.libpermission;


import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
public @interface PermissionRational {
    int value();
}
