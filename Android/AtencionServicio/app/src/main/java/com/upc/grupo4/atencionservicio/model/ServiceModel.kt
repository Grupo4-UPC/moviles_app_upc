package com.upc.grupo4.atencionservicio.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceModel(
    val id: String,
    val clientName: String,
    val address: String,
    val shift: String,
    val product: String,
    var status: String? = null,
    var subStatus: String? = null,
    var additionalPhotoUri: String? = null,
    var rightPhotoUri: String? = null,
    var leftPhotoUri: String? = null,
    var frontPhotoUri: String? = null,
    var serviceReceiverName: String? = null,
    var serviceReceiverDocId: String? = null,
    var newObservations: String? = null,
    var additionalInformation: String? = null,
    var isSigned: Boolean? = null,
) : Parcelable