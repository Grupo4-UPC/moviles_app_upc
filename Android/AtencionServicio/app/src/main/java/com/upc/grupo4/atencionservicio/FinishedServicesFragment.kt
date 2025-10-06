package com.upc.grupo4.atencionservicio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.upc.grupo4.atencionservicio.adapter.FinishedServiceAdapter
import com.upc.grupo4.atencionservicio.dialogs.InfoDialogFragment
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.util.Constants
import com.upc.grupo4.atencionservicio.util.LoadingDialog
import com.upc.grupo4.atencionservicio.util.StatusLoadHelper
import com.upc.grupo4.atencionservicio.util.VolleySingleton
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
                loadReviewServiceView(service)
            },
        )
        rvFinishedServices.adapter = finishedServiceAdapter
        rvFinishedServices.layoutManager = LinearLayoutManager(requireContext())
    }

    fun loadReviewServiceView(service: ServiceModel) {
        LoadingDialog.show(requireContext(), "Cargando información...")

        val statusLoadHelper = StatusLoadHelper()

        statusLoadHelper.fetchStatusList(
            context = requireContext(),
            tag = Constants.VOLLEY_TAG,
            onResult = { statusList ->
                Log.d(
                    "FinishedServicesFragment",
                    "Successfully fetched ${statusList.size} statuses."
                )

                LoadingDialog.hide()

                val intent = Intent(requireContext(), StartServiceActivity::class.java)
                intent.putExtra(Constants.SERVICE, service)
                intent.putExtra(
                    Constants.STATUS_LIST,
                    statusList
                )
                startActivity(intent)
            },
            onError = { errorMessage ->
                LoadingDialog.hide()

                Log.e("FinishedServicesFragment", "Failed to fetch statuses: $errorMessage")

                val dialogMessage =
                    "Ocurió un error al intentar iniciar la ruta. Intente de nuevo."
                InfoDialogFragment.newInstance(
                    message = dialogMessage,
                ).show(parentFragmentManager, "InfoDialogFragmentTag")
            }
        )
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