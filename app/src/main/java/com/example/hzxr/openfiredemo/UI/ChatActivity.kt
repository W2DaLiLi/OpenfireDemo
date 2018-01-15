package com.example.hzxr.openfiredemo.UI

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.hzxr.openfiredemo.R
import com.example.hzxr.openfiredemo.UI.Adapter.MessageRecyclerViewAdapter
import com.example.hzxr.openfiredemo.UserHelper
import com.example.hzxr.openfiredemo.model.Msg
import com.example.hzxr.openfiredemo.net.XmppConnection
import org.jivesoftware.smack.ChatManager
import org.jivesoftware.smack.XMPPConnection
import org.jivesoftware.smack.XMPPException
import org.jivesoftware.smack.packet.Message
import org.jivesoftware.smackx.filetransfer.FileTransfer
import java.io.File

/**
 * Created by Hzxr on 2018/1/8.
 */
class ChatActivity: BaseActivity() {

    private lateinit var sendBt: Button
    private lateinit var messageListRv: RecyclerView
    private lateinit var editMessageEt: EditText
    private lateinit var adapter: MessageRecyclerViewAdapter
    private lateinit var title: TextView
    private lateinit var picSendBt: Button
    private val msgList: ArrayList<Msg> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val outingUser = intent.getStringExtra("User")

        initView(outingUser)
        comingMessageListener(outingUser)//好友消息监听
        sendBt.setOnClickListener {
            sendMessage(outingUser)
        }

        picSendBt.setOnClickListener { openPictures() }
    }

    private fun initView(outingUser: String){
        sendBt = findViewById(R.id.send_message)
        picSendBt = findViewById(R.id.send_picture)
        messageListRv = findViewById(R.id.message_list)
        editMessageEt = findViewById(R.id.edit_message)
        title = findViewById(R.id.title_tv)
        title.text = outingUser

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

    private fun openPictures(){
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(intent, PICTURE_RESULT)
    }

    class SendFileTask: AsyncTask<String, String, Int>(){
        override fun doInBackground(vararg p0: String?): Int {
            if (p0.size < 0) return -1
            val file_path = p0[0]
            val toId = p0[1] + "/Smack"
            val fileTransferManager = XmppConnection.getFileTransferManager() ?: return -1
            val file = File(file_path)
            Log.d("TAG", "the file " + file.toString())
            if (file.exists().equals(false)) return -1
            val outgoingFileTransfer = fileTransferManager.createOutgoingFileTransfer(toId)
            try {
                outgoingFileTransfer.sendFile(file, "recv img")
                while (!outgoingFileTransfer.isDone) {
                    if (outgoingFileTransfer.status.equals(FileTransfer.Status.error))
                        Log.d("TAG", "ERROR : " + outgoingFileTransfer.error)
                    else {
                        Log.d("TAG", outgoingFileTransfer.status.toString())
                        Log.d("TAG", outgoingFileTransfer.progress.toString())
                    }
                    try {
                        Thread.sleep(1000)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
                if (outgoingFileTransfer.isDone) {

                }
            } catch (e: XMPPException) {
                e.printStackTrace()
            }
            return 0
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("TAG", "come back from pic activity")
        if (requestCode == PICTURE_RESULT && data != null){
            Log.d("TAG","data is not null")
            val uri = data.data
            if (!TextUtils.isEmpty(uri.authority)){
                Log.d("TAG", uri.toString())
                //TODO：解析文件路径,根据版本获取绝对路径和相对路径
            }
        }
    }

    companion object {
        val PICTURE_RESULT = 0x33
    }
}