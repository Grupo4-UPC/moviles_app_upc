package com.upc.grupo4.atencionservicio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.upc.grupo4.atencionservicio.model.PhotoReference
import com.upc.grupo4.atencionservicio.model.PhotoType
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.util.Constants
import com.upc.grupo4.atencionservicio.util.VolleySingleton

class ServiceTrackingViewFragment : Fragment() {
    private var service: ServiceModel? = null
    private lateinit var tvStatus: TextView
    private lateinit var tvSubStatus: TextView
    private lateinit var btnViewPhotos: MaterialButton
    private lateinit var btnViewRequirements: MaterialButton
    private var receivedPhotoReferences: List<PhotoReference>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si los argumentos llegaron correctamente
        arguments?.let {
            service = it.getParcelable(Constants.SERVICE)
            receivedPhotoReferences = it.getParcelableArrayList(Constants.PHOTO_REFERENCES)
        }

        // Verificar que las fotos han llegado correctamente
        if (receivedPhotoReferences.isNullOrEmpty()) {
            Log.e("ServiceTrackingViewFragment", "No photo references found ")
        } else {
            receivedPhotoReferences?.forEachIndexed { index, photoReference ->
                Log.d("ServiceTrackingViewFragment", "Photo $index: ${photoReference.uri}")
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service_tracking_view, container, false)

        tvStatus = view.findViewById(R.id.tv_status_view)
        tvSubStatus = view.findViewById(R.id.tv_sub_status_view)
        btnViewPhotos = view.findViewById(R.id.btn_view_pictures)
        btnViewRequirements = view.findViewById(R.id.btn_view_requirements)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvStatus.text = service?.status
        tvSubStatus.text = service?.subStatus

        // Verificar si las fotos llegaron correctamente antes de continuar
        if (receivedPhotoReferences.isNullOrEmpty()) {
            Log.e("ServiceTrackingViewFragment", "No photo references to show")
        } else {
            // Mostrar las fotos si están disponibles
            btnViewPhotos.setOnClickListener {
                launchViewPhotosActivity(service?.status!!, service?.subStatus!!)
            }
        }

        btnViewRequirements.setOnClickListener {
            val intent = Intent(requireContext(), ViewRequirementsActivity::class.java)
            intent.putExtra(Constants.SERVICE, service)
            startActivity(intent)
        }
    }

    private fun launchViewPhotosActivity(statusValueStr: String, subStatusValueStr: String) {
        val intent = Intent(requireContext(), ViewPhotosActivity::class.java)
        intent.putExtra(Constants.STATUS, statusValueStr)
        intent.putExtra(Constants.SUB_STATUS, subStatusValueStr)
        intent.putExtra(Constants.SERVICE_DESCRIPTION, service?.serviceDescription)

        // Verificar si las fotos están disponibles antes de pasarlas
        if (receivedPhotoReferences != null && receivedPhotoReferences!!.isNotEmpty()) {
            intent.putParcelableArrayListExtra(Constants.PHOTO_REFERENCES, ArrayList(receivedPhotoReferences!!))
        } else {
            Log.e("ServiceTrackingViewFragment", "No photos to pass to ViewPhotosActivity")
        }

        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        VolleySingleton.getInstance(requireContext()).requestQueue.cancelAll(Constants.VOLLEY_TAG)
    }

    companion object {
        @JvmStatic
        fun newInstance(service: ServiceModel?) =
            ServiceTrackingViewFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.SERVICE, service)
                    // Si las fotos no están vacías, las paso en los argumentos
                    putParcelableArrayList(Constants.PHOTO_REFERENCES, ArrayList())
                }
            }
    }
}