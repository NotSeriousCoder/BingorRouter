package com.bingor.testlib;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bingor.annotation.Module;
import com.bingor.annotation.RouterNode;

/**
 * Created by Bingor on 2019/8/1.
 */
@Module(module = "testLib")
@RouterNode(nodeType = RouterNode.NODE_TYPE_SERVICE)
public class TestService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
