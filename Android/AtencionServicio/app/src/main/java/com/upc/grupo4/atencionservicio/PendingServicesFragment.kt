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
import com.upc.grupo4.atencionservicio.model.StatusModel
import com.upc.grupo4.atencionservicio.util.LoadingDialog
import com.upc.grupo4.atencionservicio.util.StatusLoadHelper
import org.json.JSONArray

private const val ARG_PENDING_SERVICES_LIST = "pending_services_list"

/**
 * A simple [Fragment] subclass.
 * Use the [PendingServicesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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
        } else {
            // If adapter isn't initialized yet (e.g. view not created),
            // setupRecyclerView will use the updated currentPendingServicesList.
            // Or, if view is already created, you might need to call setupRecyclerView here
            // if it wasn't called for some reason or the data wasn't ready.
        }
    }

    private fun showConfirmStartServiceDialog(
        service: ServiceModel,
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

            LoadingDialog.show(requireContext(), "Iniciando ruta...")

            val statusLoadHelper = StatusLoadHelper()

            statusLoadHelper.fetchStatusList(
                context = requireContext(),
                onResult = { statusList ->
                    // Success! You have your ArrayList<StatusModel> here.
                    // You can now use this list to populate your spinner or any other UI component.
                    Log.d("PendingServicesFragment", "Successfully fetched ${statusList.size} statuses.")

                    LoadingDialog.hide()

                    launchStartActualService(service, statusList)
                },
                onError = { errorMessage ->
                    Log.e("PendingServicesFragment", "Failed to fetch statuses: $errorMessage")
                    // Toast.makeText(requireContext(), "Error: $errorMessage", Toast.LENGTH_SHORT).show()
                }
            )
        }

        dialog.show()

        // Optional: Adjust dialog width if needed (e.g., to 80% of screen width)
        val window = dialog.window
        window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
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

        startServiceLauncher.launch(intent)
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