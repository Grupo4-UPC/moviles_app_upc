package com.upc.grupo4.atencionservicio.model

import android.net.Uri

data class PhotoReference(
    val type: PhotoType, // To identify which button/category this photo belongs to
    var uri: Uri? = null, // Nullable URI of the captured photo
    var filePath: String? = null // Store the absolute path to the file
)

// Enum to define the types of photos
enum class PhotoType {
    ADDITIONAL,
    RIGHT,
    LEFT,
    FRONT
}