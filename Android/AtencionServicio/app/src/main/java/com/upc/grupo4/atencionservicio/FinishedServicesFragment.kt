package com.upc.grupo4.atencionservicio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.upc.grupo4.atencionservicio.model.ServiceModel

private const val ARG_FINISHED_SERVICES_LIST = "finished_services_list"

/**
 * A simple [Fragment] subclass.
 * Use the [FinishedServicesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FinishedServicesFragment : Fragment() {
    lateinit var finishedServiceListContainer: LinearLayout

    private var finishedServicesList: List<ServiceModel> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            finishedServicesList =
                it.getParcelableArrayList<ServiceModel>(ARG_FINISHED_SERVICES_LIST)?.toList()
                    ?: emptyList()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finished_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        finishedServiceListContainer = view.findViewById(R.id.finishedServiceListContainer)

        finishedServicesList.forEach { service ->
            val itemView = layoutInflater.inflate(
                R.layout.activity_item_service,
                finishedServiceListContainer,
                false
            )

            val tvServiceID: TextView = itemView.findViewById(R.id.tvServiceID)
            val tvClientName: TextView = itemView.findViewById(R.id.tvClientName)
            val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
            val tvShift: TextView = itemView.findViewById(R.id.tvShift)
            val tvSKU: TextView = itemView.findViewById(R.id.tvSKU)
            val btnServiceInfo: Button = itemView.findViewById(R.id.btnServiceInfo)
            val btnStartService: Button = itemView.findViewById(R.id.btnStartService)

            tvServiceID.text = "OS - ${service.id}"
            tvClientName.text = "Cliente: ${service.clientName}"
            tvAddress.text = "Dirección: ${service.address}"

            tvShift.visibility = View.GONE
            tvSKU.visibility = View.GONE
            btnServiceInfo.visibility = View.GONE
            btnStartService.visibility = View.GONE

            finishedServiceListContainer.addView(itemView)
        }
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