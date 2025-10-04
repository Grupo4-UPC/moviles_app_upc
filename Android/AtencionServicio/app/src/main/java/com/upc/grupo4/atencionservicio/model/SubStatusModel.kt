package com.upc.grupo4.atencionservicio.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubStatusModel(
    val id: Long,
    val subStatusDescription: String
) : Parcelable