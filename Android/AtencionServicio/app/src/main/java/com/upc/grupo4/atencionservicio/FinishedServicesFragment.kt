package com.upc.grupo4.atencionservicio

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upc.grupo4.atencionservicio.adapter.FinishedServiceAdapter
import com.upc.grupo4.atencionservicio.model.ServiceInformationModel
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.util.Constants
import kotlin.collections.ArrayList

private const val ARG_FINISHED_SERVICES_LIST = "finished_services_list"

/**
 * A simple [Fragment] subclass.
 * Use the [FinishedServicesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
                val serviceInformation = ServiceInformationModel( // Construct as needed
                    service.clientName,
                    "+51943585457", // Example, get real data
                    service.address,
                    "Instalacion 1",
                    "Ducha",
                    "2025-09-30",
                    "",
                    service.shift,
                    "Alt. Cdra 2 Av. La Paz"
                )
                val intent = Intent(requireContext(), StartServiceActivity::class.java)
                intent.putExtra(Constants.SERVICE, service)
                intent.putExtra(Constants.SERVICE_INFORMATION, serviceInformation)
                startActivity(intent)
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