package com.example.hzxr.openfiredemo.UI


import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Button
import com.example.hzxr.openfiredemo.R
import com.example.hzxr.openfiredemo.net.XmppConnection
import com.example.hzxr.openfiredemo.util.ToastUtil
import org.jivesoftware.smack.PacketListener
import org.jivesoftware.smack.filter.PacketFilter
import org.jivesoftware.smack.packet.Packet
import org.jivesoftware.smack.packet.Presence

class MainActivity : BaseActivity() {

    private lateinit var addFriendBt: Button
    private lateinit var getFriendListBt: Button
    private var name: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addFriendBt = findViewById(R.id.addFriend_Bt)
        getFriendListBt = findViewById(R.id.getfriendList_Bt)

        name = intent.getStringExtra("name")
        addSubscriptionListener()

        addFriendBt.setOnClickListener {
            goToAddFriend()
        }

        getFriendListBt.setOnClickListener {
            goToFriendList()
        }
    }

    private fun goToAddFriend() {
        val intent = Intent(this, AddFriendActivity::class.java)
        startActivityForResult(intent, 1)
    }

    private fun goToFriendList() {
        //TODO:Friendlist to show（）
        val intent = Intent(this, FriendsActivity::class.java)
        startActivity(intent)
    }

    private fun addSubscriptionListener() {
        val filter = PacketFilter { packet ->
            if (packet is Presence) {
                Log.d("TAG", "recv request")
                if (packet.type.equals(Presence.Type.subscribe)) {
                    Log.d("TAG", "recv subscribe")
                    return@PacketFilter true
                }
            }
            return@PacketFilter false
        }
        XmppConnection.getConnection()?.addPacketListener(subscriptionPacketListener, filter)
    }

    private val subscriptionPacketListener = PacketListener { packet ->
        //        if (packet.from.contains(name + "@" + XmppConnection.SERVER_NAME))
//            return@PacketListener
        Log.d("TAG", "recv add friend request")
        val msg = Message()
        msg.obj = packet
        msg.what = ADD_FRIEND
        handler.handleMessage(msg)
    }

    @SuppressLint("HandlerLeak")
    private val handler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            when (msg?.what) {
                ADD_FRIEND -> {
                    Log.d("TAG", "Show the request !")
                    runOnUiThread {
                        val name = (msg.obj as Packet).from.toString()
                        val builder = android.app.AlertDialog.Builder(this@MainActivity)
                        builder.setTitle("添加好友请求")
                        builder.setMessage("收到来自 " + name + " 的好友邀请")
                        builder.setPositiveButton("同意", { _, _ -> replyFotAddFriends(OK, name) })
                        builder.setNegativeButton("拒绝", { _, _ -> replyFotAddFriends(NO, name) })
                        builder.show()
                    }
                }
            }
        }
    }

    private fun replyFotAddFriends(willing: Int, fromName: String) {
        when (willing) {
            OK -> {
                val thread = Thread({
                    val presence = Presence(Presence.Type.subscribed)
                    presence.to = fromName
                    XmppConnection.getConnection()?.sendPacket(presence)
                    val roster = XmppConnection.getConnection()?.roster
                    roster?.createEntry(fromName, null, arrayOf("Friends"))
                })
                thread.start()
            }
            NO -> {
                val thread = Thread({
                    val presence = Presence(Presence.Type.unsubscribe)
                    presence.to = fromName
                    XmppConnection.getConnection()?.sendPacket(presence)
                })
                thread.start()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    companion object {
        val ADD_FRIEND = 0x11
        val OK = 0x12
        val NO = 0x13
    }

}
