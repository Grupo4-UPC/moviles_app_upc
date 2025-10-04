package com.upc.grupo4.atencionservicio.util

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.upc.grupo4.atencionservicio.model.StatusModel
import org.json.JSONArray

class StatusLoadHelper {

    fun fetchStatusList(
        context: Context,
        onResult: (ArrayList<StatusModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url = "http://10.0.2.2:3000/rutas/estados" //TODO: Change this with final URL
        val queue = Volley.newRequestQueue(context)

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // The API call was successful, now parse the response
                    val statusList = parseStatusResponse(response)
                    onResult(statusList) // Pass the parsed list to the success callback
                } catch (e: Exception) {
                    Log.e("StatusParser", "Error parsing JSON", e)
                    onError("Error parsing response.")
                }
            },
            { error ->
                // The API call failed
                Log.e("StatusApi", "Volley error: ${error.message}", error)
                onError(error.message ?: "Unknown Volley error")
            }
        )

        // Add the request to the RequestQueue.
        queue.add(jsonArrayRequest)
    }

    private fun parseStatusResponse(response: JSONArray): ArrayList<StatusModel> {
        val statusList = ArrayList<StatusModel>()

        // Loop through each JSON object in the JSON array
        for (i in 0 until response.length()) {
            val statusObject = response.getJSONObject(i)

            // Create a StatusModel object from the JSONObject
            val statusModel = StatusModel(
                id = statusObject.getLong("idEstado"),
                statusDescription = statusObject.getString("estadoDesc")
            )

            // Add the new object to our list
            statusList.add(statusModel)
        }

        return statusList
    }
}