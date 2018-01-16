package com.example.hzxr.openfiredemo.util

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.util.Log

/**
 * Created by Hzxr on 2018/1/16.
 */
object ImageHelper {

    fun handlerImagePathOnkitKat(context: Context, data: Intent): String? {
        var imagePath: String? = null
        val uri = data.data
        if (DocumentsContract.isDocumentUri(context, uri)) {
            Log.d("TAG", "DocumentType")
            val docId = DocumentsContract.getDocumentId(uri)
            if ("com.android.providers.media.documents".equals(uri.authority)) {
                val id = docId.split(':')[1]
                val selection = MediaStore.Images.Media._ID + "=" + id
                imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection)
            } else if ("com.android.provider.downloads.documents".equals(uri.authority)) {
                val contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), docId.toLong())
                imagePath = getImagePath(context, contentUri, null)
            }
        } else if ("content".equals(uri.scheme)) {
            imagePath = getImagePath(context, uri, null)
        } else if ("file".equals(uri.scheme)) {
            Log.d("TAG", "FileType")
            imagePath = uri.path
        }
        return imagePath
    }

    private fun getImagePath(context: Context, uri: Uri, selection: String?) : String?{
        var path: String? = null
        val cursor = context.contentResolver.query(uri, null, selection, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
            }
        }
        cursor.close()
        return path
    }
}