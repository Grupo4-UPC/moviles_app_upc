package com.upc.grupo4.atencionservicio

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.upc.grupo4.atencionservicio.model.ServiceInformationModel
import com.upc.grupo4.atencionservicio.model.ServiceModel

private const val ARG_PENDING_SERVICES_LIST = "pending_services_list"

/**
 * A simple [Fragment] subclass.
 * Use the [PendingServicesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PendingServicesFragment : Fragment() {
    lateinit var pendingServiceListContainer: LinearLayout

    private var pendingServicesList: List<ServiceModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pendingServicesList =
                it.getParcelableArrayList<ServiceModel>(ARG_PENDING_SERVICES_LIST)?.toList()
                    ?: emptyList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pending_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pendingServiceListContainer = view.findViewById(R.id.pendingServiceListContainer)

        pendingServicesList.forEach { service ->
            val itemView = layoutInflater.inflate(
                R.layout.item_service,
                pendingServiceListContainer,
                false
            )

            val tvServiceID: TextView = itemView.findViewById(R.id.tvServiceID)
            val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
            val tvShift: TextView = itemView.findViewById(R.id.tvShift)
            val tvProduct: TextView = itemView.findViewById(R.id.tvProduct)
            val btnServiceInfo: Button = itemView.findViewById(R.id.btnServiceInfo)
            val btnStartService: Button = itemView.findViewById(R.id.btnStartService)

            tvServiceID.text = "OS - ${service.id}"
            tvAddress.text = service.address
            tvShift.text = service.shift
            tvProduct.text = service.product

            val serviceInformation = ServiceInformationModel(
                service.clientName,
                "+51943585457",
                service.address,
                "Instalacion 1",
                "Ducha",
                "2025-09-30",
                "",
                service.shift,
                "Alt. Cdra 2 Av. La Paz"
            )

            btnServiceInfo.setOnClickListener {
                val intent = Intent(requireContext(), ServiceInformationActivity::class.java)
                intent.putExtra(
                    "service_information",
                    serviceInformation
                )
                startActivity(intent)
            }

            pendingServiceListContainer.addView(itemView)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(pendingServicesList: List<ServiceModel>) =
            PendingServicesFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(
                        ARG_PENDING_SERVICES_LIST,
                        ArrayList(pendingServicesList)
                    )
                }
            }
    }
}