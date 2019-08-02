package com.bingor.router.impl;

import android.content.Context;

/**
 * Created by HXB on 2018/7/25.
 */
public interface RouterNodeExecutorContext extends RouterNodeExecutor {
    void executeNode(Context context, String jsonParams, CallBack callBack);
}
