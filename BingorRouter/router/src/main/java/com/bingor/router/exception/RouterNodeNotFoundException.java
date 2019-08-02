package com.bingor.router.exception;

/**
 * Created by HXB on 2018/7/23.
 */
public class RouterNodeNotFoundException extends Exception {

    public RouterNodeNotFoundException(String key) {
        super("找不到节点" + key);
    }

}
