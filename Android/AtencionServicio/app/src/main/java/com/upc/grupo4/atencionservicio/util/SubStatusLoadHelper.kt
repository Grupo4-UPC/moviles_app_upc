package com.upc.grupo4.atencionservicio.util

import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.upc.grupo4.atencionservicio.model.SubStatusModel
import org.json.JSONArray

class SubStatusLoadHelper {

    fun fetchSubStatusList(
        context: Context,
        statusId: Long?,
        tag: String,
        onResult: (ArrayList<SubStatusModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url =
            "http://10.0.2.2:3000/rutas/estados/${statusId}/subestados" //TODO: Change this with final URL
        val queue = Volley.newRequestQueue(context)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // The API call was successful, now parse the response
                    val statusList = parseStatusResponse(response)
                    onResult(statusList) // Pass the parsed list to the success callback
                } catch (e: Exception) {
                    Log.e("SubStatusParser", "Error parsing JSON", e)
                    onError("Error parsing response.")
                }
            },
            { error ->
                // The API call failed
                val errorMessage = if (error is TimeoutError) {
                    "El servidor tardó demasiado en responder. Intente de nuevo."
                } else {
                    "Error de red. Verifique su conexión."
                }
                Log.e("SubStatusApi", "Volley error: ${error.message}", error)
                onError(errorMessage)
            }
        )

        jsonArrayRequest.retryPolicy = DefaultRetryPolicy(
            10000, // 10 seconds timeout
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Number of retries
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT  // Backoff multiplier
        )

        jsonArrayRequest.tag = tag

        // Add the request to the RequestQueue.
        VolleySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest)
    }

    private fun parseStatusResponse(response: JSONArray): ArrayList<SubStatusModel> {
        val subStatusList = ArrayList<SubStatusModel>()

        for (i in 0 until response.length()) {
            val statusObject = response.getJSONObject(i)

            // Create a SubStatusModel object from the JSONObject
            val subStatusModel = SubStatusModel(
                id = statusObject.getLong("idSubestado"),
                subStatusDescription = statusObject.getString("subestadoDesc")
            )

            // Add the new object to our list
            subStatusList.add(subStatusModel)
        }

        return subStatusList
    }
}