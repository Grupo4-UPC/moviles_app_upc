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
                R.layout.finished_item_service,
                finishedServiceListContainer,
                false
            )

            val tvServiceID: TextView = itemView.findViewById(R.id.tv_service_id_finished)
            val tvAddress: TextView = itemView.findViewById(R.id.tv_address_finished)
            val tvShift: TextView = itemView.findViewById(R.id.tv_shift_finished)
            val tvProduct: TextView = itemView.findViewById(R.id.tv_product_finished)
            val btnServiceInfo: Button = itemView.findViewById(R.id.btn_check_service)

            tvServiceID.text = "OS - ${service.id}"
            tvAddress.text = service.address
            tvShift.text = service.shift
            tvProduct.text = service.product

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