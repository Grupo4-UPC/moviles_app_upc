package com.upc.grupo4.atencionservicio.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignatureClient(

    var uri: Uri? = null, // Nullable URI of the captured photo
    var filePath: String? = null // Store the absolute path to the file
) : Parcelable