package com.upc.grupo4.atencionservicio.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StatusModel(
    val id: Long,
    val statusDescription: String
) : Parcelable