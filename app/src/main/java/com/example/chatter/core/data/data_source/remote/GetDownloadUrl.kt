package com.example.chatter.core.data.data_source.remote

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.util.UUID

class GetDownloadUrl {

    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private lateinit var storageReference: StorageReference
    private val imageFolder: String = "image"
    suspend fun uploadImage(
        image: ByteArray
    ): String? {
        println("😎 RemoteImageStorage: uploadImage started ")
        // Unique file name
        val uniqueFileName = "user_image_${UUID.randomUUID()}.jpg"

        // Step 1: Create reference
        storageReference = storage.reference.child("$imageFolder/$uniqueFileName")

        // Step 2: Upload image
        storageReference.putBytes(image).await()
        println("✅ Image uploaded successfully")

        // Step 3: Get download URL
        val downloadUrl = storageReference.downloadUrl.await().toString()
        println("✅ Download URL: $downloadUrl")

        return downloadUrl
    }
}