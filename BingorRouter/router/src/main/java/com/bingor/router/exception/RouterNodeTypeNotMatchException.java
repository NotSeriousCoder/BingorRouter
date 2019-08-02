package com.bingor.router.exception;

/**
 * Created by HXB on 2018/7/23.
 */
public class RouterNodeTypeNotMatchException extends Exception {

    public RouterNodeTypeNotMatchException(String type, String typeTarget) {
        super(type + "类型的节点不能作为" + typeTarget + "类型的节点来使用");
    }

}
