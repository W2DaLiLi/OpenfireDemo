package com.example.hzxr.openfiredemo.UI

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.EditText
import com.example.hzxr.openfiredemo.R

/**
 * Created by Hzxr on 2018/1/8.
 */
class ChatActivity: AppCompatActivity() {

    private lateinit var sendBt: Button
    private lateinit var messageListRv: RecyclerView
    private lateinit var editMessageEt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initView()
    }

    private fun initView(){
        sendBt = findViewById(R.id.send_message)
        messageListRv = findViewById(R.id.message_list)
        editMessageEt = findViewById(R.id.edit_message)
    }
}