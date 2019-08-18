package com.bingor.test2lib;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.bingor.annotation.Module;
import com.bingor.annotation.RouterNode;

/**
 * Created by Bingor on 2019/8/16.
 */
@Module(module = "test2Lib")
@RouterNode(nodeType = RouterNode.NODE_TYPE_ACTIVITY)
public class Avtivity4Test2Lib extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setText("这里是Avtivity4Test2Lib");

        setContentView(tv);
    }
}
