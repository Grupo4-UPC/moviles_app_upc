package com.upc.grupo4.atencionservicio

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.upc.grupo4.atencionservicio.adapter.FinishedServiceAdapter
import com.upc.grupo4.atencionservicio.dialogs.InfoDialogFragment
import com.upc.grupo4.atencionservicio.model.PhotoReference
import com.upc.grupo4.atencionservicio.model.PhotoType
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.model.SignatureClient
import com.upc.grupo4.atencionservicio.util.Constants
import com.upc.grupo4.atencionservicio.util.LoadingDialog
import com.upc.grupo4.atencionservicio.util.StatusLoadHelper
import com.upc.grupo4.atencionservicio.util.VolleySingleton
import kotlin.collections.ArrayList

private const val ARG_FINISHED_SERVICES_LIST = "finished_services_list"

class FinishedServicesFragment : Fragment() {
    private lateinit var rvFinishedServices: RecyclerView
    private lateinit var finishedServiceAdapter: FinishedServiceAdapter

    private var finishedServicesList: ArrayList<ServiceModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            finishedServicesList =
                it.getParcelableArrayList<ServiceModel>(ARG_FINISHED_SERVICES_LIST)
                    ?: ArrayList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_finished_services, container, false)
        rvFinishedServices = view.findViewById(R.id.rv_finished_services)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        // Load initial data into adapter if it came from arguments
        if (finishedServicesList.isNotEmpty()) {
            finishedServiceAdapter.updateData(finishedServicesList)
        }

    }

    private fun setupRecyclerView() {
        finishedServiceAdapter = FinishedServiceAdapter(
            ArrayList(finishedServicesList), // Pass a mutable copy initially
            onReviewServiceClick = { service ->
                loadReviewServiceView(service)
            },
        )
        rvFinishedServices.adapter = finishedServiceAdapter
        rvFinishedServices.layoutManager = LinearLayoutManager(requireContext())
    }

    fun updateServices(newPendingServices: List<ServiceModel>) {
        finishedServicesList.clear()
        finishedServicesList.addAll(newPendingServices)
        if (::finishedServiceAdapter.isInitialized) { // Ensure adapter is initialized
            finishedServiceAdapter.updateData(newPendingServices)
        }
    }

    override fun onStop() {
        super.onStop()
        VolleySingleton.getInstance(requireContext()).requestQueue.cancelAll(Constants.VOLLEY_TAG)
    }

    fun loadReviewServiceView(service: ServiceModel) {
        LoadingDialog.show(requireContext(), "Cargando información...")

        val url = "http://10.0.2.2:3000/rutas/detalle/${service.serviceId}" //

        val jsonRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                LoadingDialog.hide()

                Log.d("FinishedServicesFragment", "Detalle de ruta: $response")

                // Extraer el array de fotos del JSON
                val fotosArray = response.optJSONArray("fotos")
                val signatureArray = response.optJSONArray("firmas")
                val photoReferences = mutableListOf<PhotoReference>()
                val signatureList = mutableListOf<SignatureClient>()

                fotosArray?.let { array ->
                    for (i in 0 until array.length()) {
                        val url = array.getString(i)
                        Log.d("FinishedServicesFragment", "Foto URL: $url")
                        photoReferences.add(
                            PhotoReference(
                                type = PhotoType.ADDITIONAL,
                                uri = url.toUri()
                            )
                        ) // Guardar las fotos en la lista
                    }
                }

                signatureArray?.let { array ->
                    for (i in 0 until array.length()) {
                        val url = array.getString(i)
                        Log.d("FinishedServicesFragment firma", "Foto URL: $url")
                        signatureList.add(
                            SignatureClient(
                                uri = url.toUri()
                            )
                        ) // Guardar las firmas en la lista
                    }
                }

                val intent = Intent(requireContext(), StartServiceActivity::class.java)
                intent.putExtra(Constants.SERVICE, service)
                // Verificar si las fotos están en la lista antes de pasarlas al siguiente Intent
                if (photoReferences.isNotEmpty()) {
                    intent.putParcelableArrayListExtra(
                        Constants.PHOTO_REFERENCES,
                        ArrayList(photoReferences)
                    )
                    intent.putParcelableArrayListExtra(
                        Constants.SIGNATURE_CLIENT,
                        ArrayList(signatureList)
                    )
                    intent.putExtra(
                        Constants.STATUS,
                        service.status
                    )  // Pasa el estado del servicio
                    intent.putExtra(Constants.SUB_STATUS, service.subStatus)  // Pasa el subestado
                    intent.putExtra(
                        Constants.SERVICE_DESCRIPTION,
                        service.serviceDescription
                    )  // Pasa la descripción del servicio
                    startActivity(intent)  // Inicia la actividad de vista de fotos
                }
            },
            { error ->
                LoadingDialog.hide()

                Log.e("FinishedServicesFragment", "Error al obtener detalle: ${error.message}")
                InfoDialogFragment.newInstance(
                    message = "Ocurrió un error al cargar la información. Intente de nuevo."
                ).show(parentFragmentManager, "InfoDialogFragmentTag")
            }
        )

        com.android.volley.toolbox.Volley.newRequestQueue(requireContext()).add(jsonRequest)
    }

    companion object {
        fun newInstance(finishedServicesList: List<ServiceModel>) =
            PendingServicesFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(
                        ARG_FINISHED_SERVICES_LIST,
                        ArrayList(finishedServicesList)
                    )
                }
            }
    }
}