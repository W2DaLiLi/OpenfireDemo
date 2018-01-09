package com.example.hzxr.openfiredemo.UI

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Window

/**
 * Created by Hzxr on 2018/1/9.
 */
abstract class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
    }

}