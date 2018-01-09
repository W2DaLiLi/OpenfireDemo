package com.example.hzxr.openfiredemo.UI

import android.annotation.SuppressLint
import android.opengl.ETC1
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.hzxr.openfiredemo.R
import com.example.hzxr.openfiredemo.net.XmppConnection
import org.jivesoftware.smack.SmackConfiguration
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.filter.AndFilter
import org.jivesoftware.smack.filter.PacketFilter
import org.jivesoftware.smack.filter.PacketIDFilter
import org.jivesoftware.smack.filter.PacketTypeFilter
import org.jivesoftware.smack.packet.IQ
import org.jivesoftware.smack.packet.Registration

/**
 * Created by Hzxr on 2018/1/6.
 */
class RegisterActivity : BaseActivity() {

    private lateinit var emailEt: EditText
    private lateinit var usernameEt: EditText
    private lateinit var passwordEt: EditText
    private lateinit var registerBt: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initView()
        registerBt.setOnClickListener {
            register()
        }
    }

    private fun initView() {
        emailEt = findViewById(R.id.email_Et)
        usernameEt = findViewById(R.id.username_Et)
        passwordEt = findViewById(R.id.password_Et)
        registerBt = findViewById(R.id.register_Bt)
    }

    private fun register() {
        val name = usernameEt.text.toString()
        val key = passwordEt.text.toString()
        val email = emailEt.text.toString()
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(key) || TextUtils.isEmpty(email)) return
        val thread = Thread({
            try {
                val reg = Registration()
                reg.type = IQ.Type.SET
                reg.to = XmppConnection.getConnection()?.serviceName
                reg.setUsername(name)
                reg.setPassword(key)
                reg.addAttribute("email", email)
                val filter = AndFilter(PacketIDFilter(reg.packetID), PacketTypeFilter(IQ::class.java))
                val collector = XmppConnection.getConnection()?.createPacketCollector(filter)
                XmppConnection.getConnection()?.sendPacket(reg)

                val result = collector?.nextResult(SmackConfiguration.getPacketReplyTimeout().toLong()) as IQ
                collector.cancel()
                if (result == null) {
                    val msg = Message()
                    msg.obj = "服务器无返回结果"
                    handler.sendMessage(msg)
                } else if (result.type == IQ.Type.ERROR) {
                    if (result.error.toString().toLowerCase().equals("conflict(409)")) {
                        val msg = Message()
                        msg.obj = "账号已存在"
                        handler.sendMessage(msg)
                    } else {
                        val msg = Message()
                        msg.obj = "注册失败"
                        handler.sendMessage(msg)
                    }
                } else if (result.type == IQ.Type.RESULT) {
                    val msg = Message()
                    msg.obj = "注册成功"
                    handler.sendMessage(msg)
                }

            } catch (e: XMPPException) {
                e.printStackTrace()
            }
        })
        thread.start()
    }

    @SuppressLint("HandlerLeak")
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            Toast.makeText(this@RegisterActivity, msg?.obj.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}