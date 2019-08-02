package com.bingor.router.impl;

/**
 * Created by HXB on 2018/7/23.
 */
public interface CallBack {

    public void onSuccess(String resultJson);

    public void onFail(String reason, int code, String resultJson);
}
