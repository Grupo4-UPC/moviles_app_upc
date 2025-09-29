package com.upc.grupo4.atencionservicio

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.drawable.toDrawable
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

            btnStartService.setOnClickListener {
                showConfirmStartServiceDialog(service.id, serviceInformation)
            }

            pendingServiceListContainer.addView(itemView)
        }
    }

    private fun showConfirmStartServiceDialog(
        orderId: String,
        serviceInformation: ServiceInformationModel?
    ) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE) // Remove default title bar
        dialog.setCancelable(false) // To avoid dismissing by tapping outside or back button
        dialog.setContentView(R.layout.dialog_confirm_start_service)

        // Make the dialog background transparent to show our rounded background
        dialog.window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable())

        val tvMessage: TextView = dialog.findViewById(R.id.tv_dialog_message)
        // Setting text into dialog.
        tvMessage.text = getString(R.string.dialog_start_service)

        val btnClose: ImageView = dialog.findViewById(R.id.btn_dialog_close)
        val btnCancel: Button = dialog.findViewById(R.id.btn_dialog_cancel)
        val btnAccept: Button = dialog.findViewById(R.id.btn_dialog_accept)


        btnClose.setOnClickListener {
            dialog.dismiss() // Close the dialog
        }

        btnCancel.setOnClickListener {
            dialog.dismiss() // Close the dialog
        }

        btnAccept.setOnClickListener {
            dialog.dismiss() // Close the dialog
            //TODO: Add a loading animation for 6 seconds and start the service
            startActualServiceProcedure(orderId, serviceInformation)
        }

        dialog.show()

        // Optional: Adjust dialog width if needed (e.g., to 80% of screen width)
        val window = dialog.window
        window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun startActualServiceProcedure(
        orderId: String,
        serviceInformation: ServiceInformationModel?
    ) {
        val intent = Intent(requireContext(), StartServiceActivity::class.java)
        intent.putExtra(
            "order_id",
            orderId
        )
        intent.putExtra(
            "service_information",
            serviceInformation
        )
        startActivity(intent)
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