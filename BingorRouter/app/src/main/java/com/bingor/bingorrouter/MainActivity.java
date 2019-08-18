package com.bingor.bingorrouter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.bingor.annotation.App;
import com.bingor.annotation.Module;
import com.bingor.annotation.RouterNode;
import com.bingor.router.Router;
import com.bingor.router.RouterNoteInitializer;
import com.bingor.testlib.Avtivity4TestLib;

@App()
@Module(module = "app")
@RouterNode(nodeType = RouterNode.NODE_TYPE_ACTIVITY)
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RouterNoteInitializer.init();
        Log.d("HXB",Router.rules.toString());
    }

    public void jump(View view) {
        startActivity(new Intent(this, Avtivity4TestLib.class));
    }
}
