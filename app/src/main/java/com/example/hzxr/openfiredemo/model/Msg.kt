package com.example.hzxr.openfiredemo.model

/**
 * Created by Hzxr on 2018/1/8.
 */
class Msg(var content: String, var userId: String, var type: String) {

    constructor(content: String, userId: String, type: String, filePath: String): this(content, userId, type)

    override fun toString(): String {
        return "UserId: " + userId + " content: " + content + " type: " + type
    }
}