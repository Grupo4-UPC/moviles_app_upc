package com.upc.grupo4.atencionservicio.model

import android.os.Parcel
import android.os.Parcelable
data class ServiceModel(
    val id: String,
    val clientName: String,
    val address: String,
    val shift: String,
    val sku: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(clientName)
        parcel.writeString(address)
        parcel.writeString(shift)
        parcel.writeString(sku)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ServiceModel> {
        override fun createFromParcel(parcel: Parcel): ServiceModel {
            return ServiceModel(parcel)
        }

        override fun newArray(size: Int): Array<ServiceModel?> {
            return arrayOfNulls(size)
        }
    }
}