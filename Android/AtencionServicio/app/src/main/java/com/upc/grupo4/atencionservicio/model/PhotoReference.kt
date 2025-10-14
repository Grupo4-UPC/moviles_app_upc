package com.upc.grupo4.atencionservicio.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PhotoReference(
    val type: PhotoType, // To identify which button/category this photo belongs to
    var uri: Uri? = null, // Nullable URI of the captured photo
    var filePath: String? = null // Store the absolute path to the file
) : Parcelable

// Enum to define the types of photos
@Parcelize
enum class PhotoType : Parcelable {
    ADDITIONAL,
    RIGHT,
    LEFT,
    FRONT,
    SIGNATURE
}