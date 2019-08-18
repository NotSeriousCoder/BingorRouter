package com.bingor.testlib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bingor.annotation.RouterNode;
import com.bingor.router.Keys;
import com.bingor.router.Router;
import com.bingor.router.exception.RouterNodeNotFoundException;
import com.bingor.router.node.IntentRouterNode;

/**
 * Created by Bingor on 2019/8/16.
 */
@RouterNode(nodeType = RouterNode.NODE_TYPE_ACTIVITY)
public class Avtivity2222 extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setText("!@#$%^&*()");
        setContentView(tv);
    }
}
