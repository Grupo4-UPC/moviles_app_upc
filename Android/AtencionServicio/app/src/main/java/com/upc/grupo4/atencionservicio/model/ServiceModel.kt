package com.upc.grupo4.atencionservicio.model

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceModel(
    val rootId: Long,
    val serviceId: Long,
    val clientName: String,
    val address: String,
    val shift: String,
    val product: String,
    var serviceDate: String,
    var statusId: Long? = null,
    var status: String? = null,
    var subStatusId: Long? = null,
    var subStatus: String? = null,
    var clientDocId: String? = null,
    var district: String? = null,
    var postalCode: String? = null,
    var cellphone: String? = null,
    var addressReference: String? = null,
    var serviceDescription: String? = null,
    var observation: String? = null,
    //TODO: Temp fields for images
    var additionalPhotoUri: String? = null,
    var rightPhotoUri: String? = null,
    var leftPhotoUri: String? = null,
    var frontPhotoUri: String? = null,
    var serviceReceiverName: String? = null,
    var serviceReceiverDocId: String? = null,
    var newObservations: String? = null,
    var additionalInformation: String? = null,
    var isSigned: Boolean? = null,
    var signImg: String? = null
) : Parcelable