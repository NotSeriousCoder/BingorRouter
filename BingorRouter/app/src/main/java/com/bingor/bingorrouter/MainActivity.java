package com.bingor.bingorrouter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bingor.annotation.Module;
import com.bingor.annotation.RouterNode;

@Module(module = "app")
@RouterNode(nodeType = RouterNode.NODE_TYPE_ACTIVITY)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void jump(View view) {
        startActivity(new Intent(this, TestActivity.class));
    }
}
