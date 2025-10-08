package com.upc.grupo4.atencionservicio.util

import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.TimeoutError
import com.android.volley.toolbox.JsonArrayRequest
import com.upc.grupo4.atencionservicio.model.ServiceModel
import org.json.JSONArray

class ServiceLoaderHelper {

    fun fetchAllServices(
        context: Context,
        tag: String,
        userId: String,
        date: String,
        onResult: (ArrayList<ServiceModel>) -> Unit,
        onError: (String) -> Unit
    ) {
        val url =
            "http://10.0.2.2:3000/rutas/tecnico?usuario=${userId}&fecha=${date}" //TODO: Change this with final URL

        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    // The API call was successful, now parse the response
                    val serviceList = parseStatusResponse(response)
                    onResult(serviceList)
                } catch (e: Exception) {
                    Log.e("StatusParser", "Error parsing JSON", e)
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
                Log.e("StatusApi", "Volley error: ${error.message}", error)
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

    private fun parseStatusResponse(response: JSONArray): ArrayList<ServiceModel> {
        val serviceList = ArrayList<ServiceModel>()

        // Loop through each JSON object in the JSON array
        for (i in 0 until response.length()) {
            val statusObject = response.getJSONObject(i)

            // Getting fields that might be "NULL"
            val referenceText = statusObject.optString("referencia")
            val observationText = statusObject.optString("observacion_servicio")
            val serviceReceiverNameText = statusObject.optString("nom_persona_atendio")
            val serviceReceiverDocIdText = statusObject.optString("num_doc_persona_atendio")
            val newObservationsText = statusObject.optString("nueva_observacion")
            val additionalInfoText = statusObject.optString("info_adicional")

            // Create a ServiceModel object from the JSONObject
            val serviceModel = ServiceModel(
                rootId = statusObject.getLong("id_ruta"),
                serviceId = statusObject.getLong("id_pedido"),
                clientName = statusObject.getString("nombre_cliente"),
                address = statusObject.getString("direccion"),
                shift = statusObject.getString("turno"),
                product = statusObject.getString("sku_producto_desc"),
                serviceDate = statusObject.getString("fecha"),
                statusId = statusObject.optLong("estado_servicio_id"),
                status = statusObject.getString("estado_servicio_desc"),
                subStatusId = statusObject.optLong("subestado_servicio_id"),
                subStatus = statusObject.getString("subestado_servicio_desc"),
                clientDocId = statusObject.getString("num_doc_cliente"),
                district = statusObject.getString("distrito"),
                postalCode = statusObject.getString("codigo_postal"),
                cellphone = statusObject.getString("telefono"),
                addressReference = if (referenceText == "null") "-" else referenceText,
                serviceDescription = statusObject.getString("sku_servicio_desc"),
                observation = if (observationText == "null") "-" else observationText,
                serviceReceiverName = if (serviceReceiverNameText == "null") "" else serviceReceiverNameText,
                serviceReceiverDocId = if (serviceReceiverDocIdText == "null") "" else serviceReceiverDocIdText,
                newObservations = if (newObservationsText == "null") "" else newObservationsText,
                additionalInformation = if (additionalInfoText == "null") "" else additionalInfoText,
                isSigned = statusObject.getBoolean("firmado"),
            )

            // Add the new object to our list
            serviceList.add(serviceModel)
        }

        return serviceList
    }
}