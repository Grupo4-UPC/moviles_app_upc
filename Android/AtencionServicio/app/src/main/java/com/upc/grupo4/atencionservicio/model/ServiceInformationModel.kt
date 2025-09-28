package com.upc.grupo4.atencionservicio.model

import android.os.Parcel
import android.os.Parcelable

data class ServiceInformationModel(
    val clientName: String,
    val cellphone: String,
    val address: String,
    val typeService: String,
    val product: String,
    val dateService: String,
    val observation: String,
    val serviceShift: String,
    val addressReference: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(clientName)
        parcel.writeString(cellphone)
        parcel.writeString(address)
        parcel.writeString(typeService)
        parcel.writeString(product)
        parcel.writeString(dateService)
        parcel.writeString(observation)
        parcel.writeString(product)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ServiceInformationModel> {
        override fun createFromParcel(parcel: Parcel): ServiceInformationModel {
            return ServiceInformationModel(parcel)
        }

        override fun newArray(size: Int): Array<ServiceInformationModel?> {
            return arrayOfNulls(size)
        }
    }
}
