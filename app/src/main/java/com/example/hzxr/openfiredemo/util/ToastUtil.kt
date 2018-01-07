package com.example.hzxr.openfiredemo.util

import android.content.Context
import android.widget.Toast

/**
 * Created by Hzxr on 2018/1/6.
 */
object ToastUtil {
    fun showShort(context: Context, string: String){
        Toast.makeText(context, string, Toast.LENGTH_SHORT).show()
    }

    fun showLong(context: Context, string: String){
        Toast.makeText(context, string, Toast.LENGTH_LONG).show()
    }
}