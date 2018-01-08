package com.example.hzxr.openfiredemo.UI

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.hzxr.openfiredemo.R
import com.example.hzxr.openfiredemo.UI.Adapter.MessageRecyclerViewAdapter
import com.example.hzxr.openfiredemo.UserHelper
import com.example.hzxr.openfiredemo.model.Msg
import com.example.hzxr.openfiredemo.net.XmppConnection
import org.jivesoftware.smack.ChatManager
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Message

/**
 * Created by Hzxr on 2018/1/8.
 */
class ChatActivity: AppCompatActivity() {

    private lateinit var sendBt: Button
    private lateinit var messageListRv: RecyclerView
    private lateinit var editMessageEt: EditText
    private lateinit var adapter: MessageRecyclerViewAdapter
    private val msgList: ArrayList<Msg> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initView()

        val outingUser = intent.getStringExtra("User")
        comingMessageListener(outingUser)//好友消息监听
        sendBt.setOnClickListener {
            sendMessage(outingUser)
        }
    }

    private fun initView(){
        sendBt = findViewById(R.id.send_message)
        messageListRv = findViewById(R.id.message_list)
        editMessageEt = findViewById(R.id.edit_message)

        adapter = MessageRecyclerViewAdapter(this, msgList)
        messageListRv.layoutManager = LinearLayoutManager(this)
        messageListRv.adapter = adapter
    }

    private fun comingMessageListener(outingUser: String){
        val chatManager = XmppConnection.getConnection()?.chatManager?: return
        chatManager.addChatListener { chat, _ ->
            chat.addMessageListener { _, message ->
                if (message.body != null)
                Log.d("TAG", "body:" + message.body + "languages:"+ message.bodyLanguages)
                val msg = Msg(message.body, outingUser,"COME")
                msgList.add(msg)
                Log.d("TAG", msgList.toString())
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun sendMessage(user: String){
        Thread({
            val chatManager = XmppConnection.getConnection()?.chatManager?: return@Thread
            val newChat = chatManager.createChat(user, null)
            val content = editMessageEt.text.toString()
            try {
                val msg = Message()
                msg.body = content
                newChat.sendMessage(msg)
                val name = UserHelper.userName?: return@Thread
                val outmsg = Msg(content, name, "OUT")
                msgList.add(outmsg)
                Log.d("TAG", msgList.toString())
                handler.sendEmptyMessage(1)
            }catch (e: XMPPException){
                e.printStackTrace()
            }
        }).start()
    }

    @SuppressLint("HandlerLeak")
    private val handler = object : Handler(){
        override fun handleMessage(msg: android.os.Message?) {
            if (msg?.what == 1){
                adapter.notifyDataSetChanged()
                messageListRv.scrollToPosition(adapter.itemCount - 1)
                editMessageEt.text = null
            }
        }
    }
}