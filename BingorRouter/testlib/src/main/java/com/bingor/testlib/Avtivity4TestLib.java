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
public class Avtivity4TestLib extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView tv = new TextView(this);
        tv.setText("这里是  Avtivity4TestLib");
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IntentRouterNode node = Router.findNode(Keys.KEY_ACTIVITY_COM_BINGOR_TEST2LIB_AVTIVITY4TEST2LIB);
                    Intent intent = node.getIntent(getBaseContext());
                    Intent intent2 = new Intent(getBaseContext(), Avtivity2222.class);

                    Log.d("HXB", intent.toString());
                    Log.d("HXB", intent2.toString());
                    startActivity(intent);
                } catch (RouterNodeNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        setContentView(tv);
    }
}
