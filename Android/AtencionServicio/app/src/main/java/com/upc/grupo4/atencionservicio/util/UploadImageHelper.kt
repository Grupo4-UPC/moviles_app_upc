package com.upc.grupo4.atencionservicio.util

import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.VolleyError
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.network.VolleyMultipartRequest
import java.io.File

class UploadImageHelper {

    fun uploadImage(
        context: Context, tag: String, rootId: Long?, type: String, filePath: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "http://10.0.2.2:3000/rutas/subir-firma/$rootId" // Cambia la IP si usas físico

        val volleyRequest = object : VolleyMultipartRequest(
            Method.POST, url,
            Response.Listener<NetworkResponse> { response ->
                Log.d("VolleyUpload", "Éxito: ${response.statusCode}")
                onSuccess(response.statusCode.toString())
            },
            Response.ErrorListener { error: VolleyError ->
                Log.e("VolleyUpload", "Error: ${error.message}")
                onError(error.message.toString())
            }) {

            override fun getParamsData(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["tipo"] = type // firma o evidencia
                return params
            }

            override fun getByteData(): MutableMap<String, DataPart> {
                val params = HashMap<String, DataPart>()
                val file = File(filePath)
                val bytes = file.readBytes()
                params["file"] = DataPart(file.name, bytes, "image/jpeg")
                return params
            }
        }

        volleyRequest.tag = tag

        volleyRequest.retryPolicy = DefaultRetryPolicy(
            30000, // 30 seconds timeout
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        VolleySingleton.getInstance(context).addToRequestQueue(volleyRequest)
    }
}