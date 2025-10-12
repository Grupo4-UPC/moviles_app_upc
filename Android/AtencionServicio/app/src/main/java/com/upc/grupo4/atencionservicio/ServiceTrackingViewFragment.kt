package com.upc.grupo4.atencionservicio

import android.content.Intent
import android.os.Bundle
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
        arguments?.let {
            service = it.getParcelable(Constants.SERVICE)
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

        if (service?.additionalPhotoUri != null) {
            receivedPhotoReferences = listOf(
                PhotoReference(
                    type = PhotoType.ADDITIONAL,
                    uri = service?.additionalPhotoUri!!.toUri()
                ),
                PhotoReference(
                    type = PhotoType.RIGHT,
                    uri = service?.rightPhotoUri!!.toUri()
                ),
                PhotoReference(
                    type = PhotoType.LEFT,
                    uri = service?.leftPhotoUri!!.toUri()
                ),
                PhotoReference(
                    type = PhotoType.FRONT,
                    uri = service?.frontPhotoUri!!.toUri()
                )
            )
        }

        btnViewPhotos.setOnClickListener {
            launchViewPhotosActivity(service?.status!!, service?.subStatus!!)
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

        if (receivedPhotoReferences != null) {
            val completedPhotoReferences = mutableListOf<PhotoReference>()
            (receivedPhotoReferences as Iterable<Any?>).forEach {
                completedPhotoReferences.add(it as PhotoReference)
            }

            intent.putParcelableArrayListExtra(
                Constants.PHOTO_REFERENCES,
                ArrayList(completedPhotoReferences)
            )
        }

        startActivity(intent)
    }

    override fun onStop() {
        super.onStop()
        VolleySingleton.getInstance(requireContext()).requestQueue.cancelAll(Constants.VOLLEY_TAG)
    }

    companion object {
        @JvmStatic
        fun newInstance(service: ServiceModel) =
            ServiceTrackingViewFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.SERVICE, service)
                }
            }
    }
}