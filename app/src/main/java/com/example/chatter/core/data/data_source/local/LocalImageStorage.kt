package com.example.chatter.core.data.data_source.local

import android.content.Context
import java.io.File
import java.io.IOException
import java.util.UUID

class LocalImageStorage(
    private val context: Context
) {

    private val USER_IMAGES = "user_images"
    private val CHATS_IMAGES = "chats_images"

    private lateinit var folder : File
    private lateinit var fileName : String

    /**
     * Save the Binary data as a file in internal storage.
     * The folder will always contain exactly one image file.
     */
    fun writeData(data: ByteArray , isUserImage : Boolean = true): String? {
        println("🧠 writeData called")
        return try {
            if(isUserImage){// Create or get the folder
                folder = File(context.filesDir, USER_IMAGES)
                if (!folder.exists()) folder.mkdirs()

                // Delete all old images first
                folder.listFiles()?.forEach { it.delete() }
                fileName = "${USER_IMAGES}_${UUID.randomUUID()}.jpg"

            }else{
                folder = File(context.filesDir, CHATS_IMAGES)
                if (!folder.exists()) folder.mkdirs()
                fileName = "${CHATS_IMAGES}_${UUID.randomUUID()}.jpg"
            }
            // Create a new unique file name (e.g., user_image_<uuid>.jpg)
            val newFile = File(folder, fileName)

            // Write bytes to the file
            newFile.writeBytes(data)

            println("🧠 Saved new image: ${newFile.absolutePath}")

            // Return the absolute path
            newFile.absolutePath

        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    /**
     * Delete the folder and all its files.
     */
    fun deleteData(fileName : String): Boolean {
        val folder = File(context.filesDir, fileName)
        println("🧠 Deleting folder: ${folder.absolutePath}")
        return if (folder.exists()) folder.deleteRecursively() else false
    }

    fun readBytesFromPath(path: String): ByteArray? {
        return try {
            val file = File(path)
            if (file.exists()) file.readBytes() else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}