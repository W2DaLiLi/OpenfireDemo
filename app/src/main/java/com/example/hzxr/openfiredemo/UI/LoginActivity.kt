package com.example.hzxr.openfiredemo.UI

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.hzxr.openfiredemo.R
import com.example.hzxr.openfiredemo.UserHelper
import com.example.hzxr.openfiredemo.net.XmppConnection
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Presence

/**
 * Created by Hzxr on 2018/1/6.
 */
class LoginActivity: BaseActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginBt: Button
    private lateinit var registerBt: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        username = findViewById(R.id.account_Et)
        password = findViewById(R.id.password_Et)
        loginBt = findViewById(R.id.login_Bt)
        registerBt = findViewById(R.id.register_Bt)

        loginBt.setOnClickListener {
            onLogin()
        }

        registerBt.setOnClickListener {
            gotoRegister()
        }
    }

    private fun onLogin() {
        //TODO:响应登陆button，获取Username，password，传给链接服务器，设计广播接收器，更具返回登陆结果的广播基于反馈
        val name = username.text.toString()
        val key = password.text.toString()
        Log.d("test",name+" "+key)
        if (name.isEmpty() || key.isEmpty() || name.equals("") || key.equals("")) return
        val thread = Thread({
            try {
                XmppConnection.getConnection()?.login(name, key)?: return@Thread
                val presence = Presence(Presence.Type.available)
                XmppConnection.getConnection()?.sendPacket(presence)
                Log.d("TAG","登陆成功")
                handler.sendEmptyMessage(1)
            } catch (e: XMPPException) {
                Log.d("TAG",e.message)
                Log.d("TAG",e.xmppError.toString())
                XmppConnection.closeConnecttion()
                handler.sendEmptyMessage(2)
            }
        })
        thread.start()
    }

    private fun gotoRegister(){
        //跳转到注册页面
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("HandlerLeak")
    private val handler = object : Handler() {
        override fun handleMessage(msg: android.os.Message) {
            if (msg.what == 1) {
                Toast.makeText(this@LoginActivity, "登陆成功", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.putExtra("name", username.text.toString())
                UserHelper.userName = username.text.toString()
                startActivity(intent)
            } else if (msg.what == 2) {
                Toast.makeText(this@LoginActivity, "登陆失败", Toast.LENGTH_SHORT).show()
            }
        }
    }
}