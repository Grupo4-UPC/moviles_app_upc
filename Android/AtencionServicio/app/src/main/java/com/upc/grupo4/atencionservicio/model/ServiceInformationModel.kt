package com.upc.grupo4.atencionservicio.model

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceInformationModel(
    var clientName: String,
    val cellphone: String,
    val address: String,
    val typeService: String,
    val product: String,
    val dateService: String,
    val observation: String,
    val serviceShift: String,
    val addressReference: String,
    var clientId: String? = null,
    var observations: String? = null,
    var extraInformation: String? = null,
    var isSigned: Boolean? = null,

    ) : Parcelable
