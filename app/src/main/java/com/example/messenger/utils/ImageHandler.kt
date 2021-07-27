package com.example.messenger.utils

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.bumptech.glide.Glide
import com.example.messenger.MessengerApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


object ImageHandler {
    fun convertBitmapToBase64(bitmap: Bitmap?): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        var options = 100
        while (byteArrayOutputStream.toByteArray().size / 1024 > 100) {
            byteArrayOutputStream.reset()
            options -= 10
            bitmap?.compress(Bitmap.CompressFormat.JPEG, options, byteArrayOutputStream)
            if (options == 0) break
        }
        val imageBytes = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)
    }

    fun convertBase64ToBitmap(imageString: String): Bitmap {
        val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    fun saveImageToInternalStorage(
        bitmapImage: Bitmap?,
        name: String
    ): String? {
        if (bitmapImage == null) return null
        val contextWrapper = ContextWrapper(MessengerApplication.applicationContext())
        val directory: File = contextWrapper.getDir("imageDir", Context.MODE_PRIVATE)
        val myPath = File(directory, name)
        FileOutputStream(myPath).use {
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }
        return myPath.absolutePath
    }

    fun loadBitmapFromStorage(path: String): Bitmap {
        return runBlocking(Dispatchers.IO) {
                Glide.with(MessengerApplication.applicationContext()).asBitmap().load(File(path))
                    .submit().get()
        }
    }
}