package com.bingor.router.node;

import android.content.Context;
import android.content.Intent;

/**
 * Intent类型节点
 * Created by HXB on 2018/7/23.
 */
public class IntentRouterNode extends RouterNode {
    private String action;

    public IntentRouterNode(Class target) {
        super(target);
    }

    public IntentRouterNode(Class target, String action) {
        super(target);
        this.action = action;
    }

    public Intent getIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, cls);
        return intent;
    }

    public Intent getIntent(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        return intent;
    }

    public Intent getIntent() {
        Intent intent = new Intent();
        intent.setAction(action);
        return intent;
    }
}
