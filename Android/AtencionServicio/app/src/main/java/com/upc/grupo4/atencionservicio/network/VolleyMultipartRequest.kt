package com.upc.grupo4.atencionservicio.network

import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import java.io.*

open class VolleyMultipartRequest(
    method: Int,
    url: String,
    private val mListener: Response.Listener<NetworkResponse>,
    private val mErrorListener: Response.ErrorListener
) : Request<NetworkResponse>(method, url, mErrorListener) {

    private var headers: MutableMap<String, String> = HashMap()

    fun setCustomHeaders(headers: MutableMap<String, String>) {
        this.headers = headers
    }

    override fun getHeaders(): MutableMap<String, String> = headers

    override fun getBodyContentType(): String = "multipart/form-data;boundary=$boundary"

    private val boundary = "apiclient-${System.currentTimeMillis()}"
    private val twoHyphens = "--"
    private val lineEnd = "\r\n"

    protected open fun getParamsData(): MutableMap<String, String> = HashMap()
    protected open fun getByteData(): MutableMap<String, DataPart> = HashMap()

    override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        return try {
            Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            Response.error(null)
        }
    }

    override fun deliverResponse(response: NetworkResponse) {
        mListener.onResponse(response)
    }

    override fun getBody(): ByteArray {
        val bos = ByteArrayOutputStream()
        val dos = DataOutputStream(bos)

        try {

            for ((key, value) in getParamsData()) {
                buildTextPart(dos, key, value)
            }

            // Enviar archivos
            for ((key, dataPart) in getByteData()) {
                buildFilePart(dos, dataPart, key)
            }

            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return bos.toByteArray()
    }

    private fun buildTextPart(dataOutputStream: DataOutputStream, parameterName: String, parameterValue: String) {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"$parameterName\"$lineEnd")
        dataOutputStream.writeBytes(lineEnd)
        dataOutputStream.writeBytes(parameterValue + lineEnd)
    }

    private fun buildFilePart(dataOutputStream: DataOutputStream, dataFile: DataPart, inputName: String) {
        dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd)
        dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"$inputName\"; filename=\"${dataFile.fileName}\"$lineEnd")
        dataOutputStream.writeBytes("Content-Type: ${dataFile.type}$lineEnd")
        dataOutputStream.writeBytes(lineEnd)

        val fileInputStream = ByteArrayInputStream(dataFile.content)
        val buffer = ByteArray(1024)
        var bytesRead: Int
        while (fileInputStream.read(buffer).also { bytesRead = it } != -1) {
            dataOutputStream.write(buffer, 0, bytesRead)
        }
        dataOutputStream.writeBytes(lineEnd)
    }

    data class DataPart(val fileName: String, val content: ByteArray, val type: String)
}