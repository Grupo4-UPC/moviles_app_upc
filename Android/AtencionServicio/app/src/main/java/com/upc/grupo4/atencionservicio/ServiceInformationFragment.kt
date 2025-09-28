package com.upc.grupo4.atencionservicio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.upc.grupo4.atencionservicio.model.ServiceInformationModel

private const val ARG_SERVICE_INFORMATION = "service_information"

/**
 * A simple [Fragment] subclass.
 * Use the [ServiceInformationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ServiceInformationFragment : Fragment() {
    private var serviceInformation: ServiceInformationModel? = null

    private lateinit var txtClient: TextView
    private lateinit var txtCellphone: TextView
    private lateinit var txtAddress: TextView
    private lateinit var txtTypeService: TextView
    private lateinit var txtProduct: TextView
    private lateinit var txtDateService: TextView
    private lateinit var txtObservation: TextView
    private lateinit var txtServiceShift: TextView
    private lateinit var txtAddressReference: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceInformation = it.getParcelable(ARG_SERVICE_INFORMATION)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtClient = view.findViewById(R.id.txt_client)
        txtCellphone = view.findViewById(R.id.txt_cellphone)
        txtAddress = view.findViewById(R.id.txt_address)
        txtTypeService = view.findViewById(R.id.txt_type_service)
        txtProduct = view.findViewById(R.id.txt_product)
        txtDateService = view.findViewById(R.id.txt_date_service)
        txtObservation = view.findViewById(R.id.txt_observation)
        txtServiceShift = view.findViewById(R.id.txt_service_shift)
        txtAddressReference = view.findViewById(R.id.txt_address_reference)

        txtClient.text = serviceInformation?.clientName
        txtCellphone.text = serviceInformation?.cellphone
        txtAddress.text = serviceInformation?.address
        txtTypeService.text = serviceInformation?.typeService
        txtProduct.text = serviceInformation?.product
        txtDateService.text = serviceInformation?.dateService
        txtObservation.text = serviceInformation?.observation
        txtServiceShift.text = serviceInformation?.serviceShift
        txtAddressReference.text = serviceInformation?.addressReference
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param serviceInformation Parameter 1.
         * @return A new instance of fragment ServiceInformationFragment.
         */
        @JvmStatic
        fun newInstance(serviceInformation: ServiceInformationModel) =
            ServiceInformationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SERVICE_INFORMATION, serviceInformation.toString())
                }
            }
    }
}