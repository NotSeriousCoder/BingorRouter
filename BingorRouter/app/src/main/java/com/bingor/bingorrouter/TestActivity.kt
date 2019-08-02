package com.bingor.bingorrouter

import android.app.Activity
import android.os.Bundle
import com.bingor.annotation.RouterNode

/**
 * Created by Bingor on 2019/7/27.
 */
@RouterNode(nodeType = RouterNode.NODE_TYPE_ACTIVITY)
class TestActivity : Activity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}