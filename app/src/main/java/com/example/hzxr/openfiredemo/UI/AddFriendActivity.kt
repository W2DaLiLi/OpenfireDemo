package com.example.hzxr.openfiredemo.UI

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.hzxr.openfiredemo.R
import com.example.hzxr.openfiredemo.net.XmppConnection
import com.example.hzxr.openfiredemo.util.ToastUtil
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Presence

/**
 * Created by Hzxr on 2018/1/6.
 */
class AddFriendActivity : BaseActivity() {

    private lateinit var friendNameEt: EditText
    private lateinit var submitBt: Button
    private lateinit var title: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        friendNameEt = findViewById(R.id.friendName_Et)
        submitBt = findViewById(R.id.submit_Bt)
        title = findViewById(R.id.title_tv)

        title.text = "添加好友"

        submitBt.setOnClickListener {
            submit()
        }
    }

    private fun submit() {
        val name = friendNameEt.text.toString()
        if (TextUtils.isEmpty(name)) return
        val thread = Thread({
            //            val subscription = Presence(Presence.Type.subscribe)
//            subscription.to(name + "@localhost")
//            XmppConnection.getConnection()?.sendPacket(subscription)
            val roster = XmppConnection.getConnection()?.roster
            try {
                roster?.createEntry(name + "@localhost", null, arrayOf("Friends"))
                handler.sendEmptyMessage(1)
            } catch (e: XMPPException) {
                e.printStackTrace()
                handler.sendEmptyMessage(2)

            }
        })
        thread.start()
    }

    @SuppressLint("HandlerLeak")
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            when(msg?.what){
                1 -> ToastUtil.showShort(this@AddFriendActivity, "添加成功")
                2 -> ToastUtil.showShort(this@AddFriendActivity, "添加失败")
            }
            setResult(1)
            finish()
        }
    }
}