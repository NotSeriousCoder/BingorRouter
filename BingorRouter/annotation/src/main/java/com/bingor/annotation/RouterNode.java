package com.bingor.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface RouterNode {
    // Activity
    int NODE_TYPE_ACTIVITY = 0;
    // Service
    int NODE_TYPE_SERVICE = 1;
    // Receiver
    int NODE_TYPE_RECEIVER = 2;


    int NODE_TYPE_UTIL_JSON = 3;
    int NODE_TYPE_UTIL_JSON_CALLBACK = 4;
    int NODE_TYPE_UTIL_JSON_CONTEXT_CALLBACK = 5;
    int NODE_TYPE_UTIL_JSON_ACTIVITY_CALLBACK = 6;

    //节点类型
    int nodeType();

    String action() default "";
}
