package com.upc.grupo4.atencionservicio

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.graphics.drawable.toDrawable
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.util.Constants
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.upc.grupo4.atencionservicio.adapter.PendingServiceAdapter
import com.upc.grupo4.atencionservicio.dialogs.ConfirmationDialogFragment
import com.upc.grupo4.atencionservicio.dialogs.InfoDialogFragment
import com.upc.grupo4.atencionservicio.model.StatusModel
import com.upc.grupo4.atencionservicio.util.LoadingDialog
import com.upc.grupo4.atencionservicio.util.StatusLoadHelper
import com.upc.grupo4.atencionservicio.util.VolleySingleton
import org.json.JSONArray

private const val ARG_PENDING_SERVICES_LIST = "pending_services_list"

class PendingServicesFragment : Fragment() {
    private lateinit var rvPendingServices: RecyclerView
    private lateinit var serviceAdapter: PendingServiceAdapter

    private lateinit var startServiceLauncher: ActivityResultLauncher<Intent>

    private var pendingServicesList: ArrayList<ServiceModel> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pendingServicesList = it.getParcelableArrayList("pending_services_list") ?: ArrayList()
        }

        startServiceLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("PendingServicesFragment", "Finish service was successful.")
                val data: Intent? = result.data

                val updatedServiceModel = data?.getParcelableExtra<ServiceModel>(
                    Constants.SERVICE
                )
                if (updatedServiceModel != null) {
                    setFragmentResult(
                        Constants.SERVICE_STARTED_REQUEST_KEY,
                        bundleOf(Constants.SERVICE_MODEL_BUNDLE_KEY to updatedServiceModel)
                    )
                } else {
                    Log.d("PendingServicesFragment", "No service information returned.")
                }

            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                Log.d("PendingServicesFragment", "Finish service was cancelled.")
                // Handle cancellation if needed
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pending_services, container, false)
        rvPendingServices = view.findViewById(R.id.rv_pending_services)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        // Load initial data into adapter if it came from arguments
        if (pendingServicesList.isNotEmpty()) {
            serviceAdapter.updateData(pendingServicesList)
        }
    }

    override fun onDestroyView() {
        LoadingDialog.hide()
        super.onDestroyView()
    }

    private fun setupRecyclerView() {
        serviceAdapter = PendingServiceAdapter(
            ArrayList(pendingServicesList), // Pass a mutable copy initially
            onServiceInfoClick = { service ->
                val intent = Intent(requireContext(), ServiceInformationActivity::class.java)
                intent.putExtra(Constants.SERVICE, service)
                startActivity(intent)
            },
            onStartServiceClick = { service ->
                showConfirmStartServiceDialog(service)
            }
        )
        rvPendingServices.adapter = serviceAdapter
        rvPendingServices.layoutManager = LinearLayoutManager(requireContext())
    }

    fun updateServices(newPendingServices: List<ServiceModel>) {
        pendingServicesList.clear()
        pendingServicesList.addAll(newPendingServices)
        if (::serviceAdapter.isInitialized) { // Ensure adapter is initialized
            serviceAdapter.updateData(newPendingServices)
        }
    }

    private fun showConfirmStartServiceDialog(
        service: ServiceModel,
    ) {
        ConfirmationDialogFragment.newInstance(message = getString(R.string.dialog_start_service))
            .setOnAcceptClickListener { startingService(service) }
            .show(parentFragmentManager, "ConfirmationDialogFragmentTag")
    }

    private fun startingService(service: ServiceModel) {
        LoadingDialog.show(requireContext(), "Iniciando ruta...")

        val statusLoadHelper = StatusLoadHelper()

        statusLoadHelper.fetchStatusList(
            context = requireContext(),
            tag = Constants.VOLLEY_TAG,
            onResult = { statusList ->
                if (!isAdded) return@fetchStatusList
                // Success! You have your ArrayList<StatusModel> here.
                // You can now use this list to populate your spinner or any other UI component.
                Log.d(
                    "PendingServicesFragment",
                    "Successfully fetched ${statusList.size} statuses."
                )

                LoadingDialog.hide()

                launchStartActualService(service, statusList)
            },
            onError = { errorMessage ->
                if (!isAdded) return@fetchStatusList

                LoadingDialog.hide()

                Log.e("PendingServicesFragment", "Failed to fetch statuses: $errorMessage")

                val dialogMessage =
                    "Ocurió un error al intentar ver el servicio. Intente de nuevo."
                InfoDialogFragment.newInstance(
                    message = dialogMessage,
                ).show(parentFragmentManager, "InfoDialogFragmentTag")
            }
        )
    }

    private fun launchStartActualService(
        service: ServiceModel,
        statusList: ArrayList<StatusModel>
    ) {
        val intent = Intent(requireContext(), StartServiceActivity::class.java)
        intent.putExtra(
            Constants.SERVICE,
            service
        )
        intent.putExtra(
            Constants.STATUS_LIST,
            statusList
        )
        Log.d("PendingServicesFragment", "Lanzando StartServiceActivity con service:")

        startServiceLauncher.launch(intent)
    }

    override fun onStop() {
        super.onStop()
        VolleySingleton.getInstance(requireContext()).requestQueue.cancelAll(Constants.VOLLEY_TAG)
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