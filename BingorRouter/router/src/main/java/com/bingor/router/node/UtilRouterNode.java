package com.bingor.router.node;

import android.app.Activity;
import android.content.Context;

import com.bingor.router.Util;
import com.bingor.router.exception.RouterNodeTypeNotMatchException;
import com.bingor.router.impl.CallBack;
import com.bingor.router.impl.RouterNodeExecutorActivity;
import com.bingor.router.impl.RouterNodeExecutorCallback;
import com.bingor.router.impl.RouterNodeExecutorContext;
import com.bingor.router.impl.RouterNodeExecutorNormal;


/**
 * 工具类型节点
 * Created by HXB on 2018/7/23.
 */
public class UtilRouterNode extends RouterNode {
    public UtilRouterNode(Class<?> cls) {
        super(cls);
    }


    public void executeNode(String jsonParams) throws RouterNodeTypeNotMatchException {
        if (new Util().isMatchInterface(RouterNodeExecutorNormal.class, cls)) {
            try {
                RouterNodeExecutorNormal routerNodeExecutorNormal = (RouterNodeExecutorNormal) cls.newInstance();
                routerNodeExecutorNormal.executeNode(jsonParams);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            throw new RouterNodeTypeNotMatchException(cls.getClass().getSimpleName(), RouterNodeExecutorNormal.class.getSimpleName());
        }
    }

    public void executeNode(String jsonParams, CallBack callBack) throws RouterNodeTypeNotMatchException {
        if (new Util().isMatchInterface(RouterNodeExecutorCallback.class, cls)) {
            try {
                RouterNodeExecutorCallback routerNodeExecutorCallback = (RouterNodeExecutorCallback) cls.newInstance();
                routerNodeExecutorCallback.executeNode(jsonParams, callBack);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            throw new RouterNodeTypeNotMatchException(cls.getClass().getSimpleName(), RouterNodeExecutorNormal.class.getSimpleName());
        }
    }

    public void executeNode(Activity activity, String jsonParams, CallBack callBack) throws RouterNodeTypeNotMatchException {
        if (new Util().isMatchInterface(RouterNodeExecutorActivity.class, cls)) {
            try {
                RouterNodeExecutorActivity routerNodeExecutorActivity = (RouterNodeExecutorActivity) cls.newInstance();
                routerNodeExecutorActivity.executeNode(activity, jsonParams, callBack);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            throw new RouterNodeTypeNotMatchException(cls.getClass().getSimpleName(), RouterNodeExecutorNormal.class.getSimpleName());
        }
    }

    public void executeNode(Context context, String jsonParams, CallBack callBack) throws RouterNodeTypeNotMatchException {
        if (new Util().isMatchInterface(RouterNodeExecutorContext.class, cls)) {
            try {
                RouterNodeExecutorContext routerNodeExecutorContext = (RouterNodeExecutorContext) cls.newInstance();
                routerNodeExecutorContext.executeNode(context, jsonParams, callBack);
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else {
            throw new RouterNodeTypeNotMatchException(cls.getClass().getSimpleName(), RouterNodeExecutorNormal.class.getSimpleName());
        }
    }
}
