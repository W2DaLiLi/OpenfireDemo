package com.example.hzxr.openfiredemo.UI.Adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.hzxr.openfiredemo.R
import com.example.hzxr.openfiredemo.model.Msg

/**
 * Created by Hzxr on 2018/1/8.
 */
class MessageRecyclerViewAdapter (private val context: Context,
                                  private val msgList: ArrayList<Msg>):RecyclerView.Adapter<MessageRecyclerViewAdapter.MessageViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MessageViewHolder {
        return when(viewType){
            TYPE_COME -> MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_coming_msg, parent), viewType)
            TYPE_OUT -> MessageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_outing_msg, parent), viewType)
            else -> throw Throwable("Error Message Type ViewHolder")
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder?, position: Int) {
        val name = msgList[position].userId
        holder?.msgHead?.text = name.substring(0, name.indexOf("@"))
        holder?.msgContent?.text = msgList[position].content
    }

    override fun getItemCount(): Int {
        return msgList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(msgList[position].type){
            "COME" -> TYPE_COME
            "OUT" -> TYPE_OUT
            else -> ERROR_TYPE
        }
    }

    class MessageViewHolder(itemView: View, type: Int): RecyclerView.ViewHolder(itemView){
        val msgHead: TextView
        val msgContent: TextView
        init {
            if (type == TYPE_COME){
                msgHead = itemView.findViewById(R.id.msg_coming_head)
                msgContent = itemView.findViewById(R.id.msg_coming_content)
            } else {
                msgHead = itemView.findViewById(R.id.msg_outing_head)
                msgContent = itemView.findViewById(R.id.msg_coming_content)
            }
        }
    }

    companion object {
        val TYPE_COME = 0x22
        val TYPE_OUT = 0x23
        val ERROR_TYPE = 0x24
    }
}