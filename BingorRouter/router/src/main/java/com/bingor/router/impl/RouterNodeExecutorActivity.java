package com.bingor.router.impl;

import android.app.Activity;

/**
 * Created by HXB on 2018/7/25.
 */
public interface RouterNodeExecutorActivity extends RouterNodeExecutor {
    void executeNode(Activity activity, String jsonParams, CallBack callBack);
}
