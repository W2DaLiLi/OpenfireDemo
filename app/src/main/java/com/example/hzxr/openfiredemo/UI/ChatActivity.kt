package com.example.hzxr.openfiredemo.UI

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.example.hzxr.openfiredemo.R
import com.example.hzxr.openfiredemo.UI.Adapter.MessageRecyclerViewAdapter
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        initView()

        val outingUser = intent.getStringExtra("User")
        comingMessageListener()//好友消息监听
        sendBt.setOnClickListener {
            sendMessage(outingUser)
        }
    }

    private fun initView(){
        sendBt = findViewById(R.id.send_message)
        messageListRv = findViewById(R.id.message_list)
        editMessageEt = findViewById(R.id.edit_message)
    }

    private fun comingMessageListener(){
        val chatManager = XmppConnection.getConnection()?.chatManager?: return
        chatManager.addChatListener { chat, _ ->
            chat.addMessageListener { _, message ->
                if (message.body != null)
                Log.d("TAG", "body:" + message.body + "languages:"+ message.bodyLanguages)
            }
        }
    }

    private fun sendMessage(user: String){
        Thread({
            val chatManager = XmppConnection.getConnection()?.chatManager?: return@Thread
            val newChat = chatManager.createChat(user, null)
            try {
                val msg = Message()
                msg.body = "Hello" + user
                newChat.sendMessage(msg)
            }catch (e: XMPPException){
                e.printStackTrace()
            }
        }).start()
    }
}