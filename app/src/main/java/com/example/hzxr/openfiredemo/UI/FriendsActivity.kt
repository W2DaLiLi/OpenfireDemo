package com.example.hzxr.openfiredemo.UI

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.example.hzxr.openfiredemo.R
import com.example.hzxr.openfiredemo.net.XmppConnection
import org.jivesoftware.smack.XMPPException

/**
 * Created by Hzxr on 2018/1/7.
 */
class FriendsActivity: AppCompatActivity() {

    private lateinit var friendListRv: RecyclerView
    private lateinit var adapter: FriendsRecyclerAdapter
    private val friendList: ArrayList<HashMap<String, String>> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)
        friendListRv = findViewById(R.id.friend_list_rv)
        friendListRv.layoutManager = LinearLayoutManager(this)
        adapter = FriendsRecyclerAdapter(this, friendList)
        friendListRv.adapter = adapter
        loadFriendsData()
    }

    private fun loadFriendsData() {
        val thread = Thread({
            try {
                val roster = XmppConnection.getConnection()?.roster
                val entries = roster?.entries ?: return@Thread
                for (item in entries) {
                    val map = HashMap<String, String>()
                    map.put("User", item.user)
                    map.put("Name", item.user.substring(0, item.user.indexOf("@")))
                    friendList.add(map)
                }
                Log.d("TAG", friendList.toString())
            } catch (e: XMPPException) {
                e.printStackTrace()
            }
        })
        thread.start()
    }

}