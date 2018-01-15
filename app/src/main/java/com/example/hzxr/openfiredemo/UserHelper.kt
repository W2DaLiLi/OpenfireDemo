package com.example.hzxr.openfiredemo

/**
 * Created by Hzxr on 2018/1/8.
 */
object UserHelper {

    var userName: String? = null
    var toUserId: String? = null

    val toUserName
    get() = toUserId?.substring(0, toUserId?.indexOf("@")!!)
}