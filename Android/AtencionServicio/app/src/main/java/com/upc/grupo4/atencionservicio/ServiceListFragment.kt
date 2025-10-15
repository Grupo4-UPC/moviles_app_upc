package com.upc.grupo4.atencionservicio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.upc.grupo4.atencionservicio.dialogs.InfoDialogFragment
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.util.Constants
import com.upc.grupo4.atencionservicio.util.LoadingDialog
import com.upc.grupo4.atencionservicio.util.ServiceLoaderHelper
import com.upc.grupo4.atencionservicio.util.VolleySingleton
import java.time.LocalDate

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ServiceListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ServiceListFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var toggleButtonGroup: MaterialButtonToggleGroup
    lateinit var btnToStart: MaterialButton
    lateinit var btnEnded: MaterialButton
    lateinit var fragmentServiceListContainer: FrameLayout
    lateinit var actualFragmentManager: FragmentManager
    lateinit var allServicesList: MutableList<ServiceModel>
    lateinit var pendingServicesList: MutableList<ServiceModel>
    lateinit var finishedServicesList: MutableList<ServiceModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        // Set up the listener for results from PendingServicesFragment
        setFragmentResultListener(Constants.SERVICE_STARTED_REQUEST_KEY) { requestKey, bundle ->
            if (requestKey == Constants.SERVICE_STARTED_REQUEST_KEY) {
                val updatedServiceModel: ServiceModel? =
                    bundle.getParcelable(Constants.SERVICE_MODEL_BUNDLE_KEY)
                if (updatedServiceModel != null) {
                    Log.d(
                        "ServiceListFragment",
                        "Received result from pending: ${updatedServiceModel.serviceId}"
                    )
                    handleServiceUpdate(updatedServiceModel)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service_list, container, false)

        toggleButtonGroup = view.findViewById(R.id.toggleButtonGroup)
        btnToStart = view.findViewById(R.id.btnToStart)
        btnEnded = view.findViewById(R.id.btnEnded)
        fragmentServiceListContainer = view.findViewById(R.id.fragment_service_list_container)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LoadingDialog.show(requireContext())

        actualFragmentManager = requireActivity().supportFragmentManager

        /*        Handler(Looper.getMainLooper()).postDelayed({
        //            LoadingDialog.hide()
        //
        //            initializeServiceLists()
        //
        //            // Set initial counts
        //            updateButtonText(
        //                btnToStart,
        //                getString(R.string.services_to_start),
        //                pendingServicesList.size
        //            )
        //            updateButtonText(
        //                btnEnded,
        //                getString(R.string.services_finished),
        //                finishedServicesList.size
        //            )
        //
        //            loadPendingServices()
        //
        //            toggleButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
        //                if (isChecked) { // Only react to the button that is being checked
        //                    when (checkedId) {
        //                        R.id.btnToStart -> {
        //                            loadPendingServices()
        //                        }
        //
        //                        R.id.btnEnded -> {
        //                            loadFinishedServices()
        //                        }
        //                    }
        //                }
        //            }
        //        }, 2000)*/

        fetchServicesFromServer()

        toggleButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) { // Only react to the button that is being checked
                when (checkedId) {
                    R.id.btnToStart -> {
                        loadPendingServices()
                    }

                    R.id.btnEnded -> {
                        loadFinishedServices()
                    }
                }
            }
        }
    }

    private fun fetchServicesFromServer() {
        // Show a loading indicator
        LoadingDialog.show(requireContext())

        val serviceLoadHelper = ServiceLoaderHelper() // Your new helper
        val fechaActual = LocalDate.now().toString()
        Log.d("DEBUG_FECHA", "La fecha actual es: $fechaActual")


        val prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
        val tecnicoId = prefs.getInt("id", -1)

        if (tecnicoId == -1) {
            Log.e("ServiceListFragment", "No se encontró el ID del técnico")
            return
        }

        serviceLoadHelper.fetchAllServices(
            context = requireContext(),
            tag = Constants.VOLLEY_TAG,
            userId = tecnicoId.toString(),
            date = fechaActual,
            onResult = { services ->
                // Hide the loading indicator
                LoadingDialog.hide()

                // Save the fetched list
                allServicesList = services

                filterServiceLists()

                updateButtonText(
                    btnToStart,
                    getString(R.string.services_to_start),
                    pendingServicesList.size
                )
                updateButtonText(
                    btnEnded,
                    getString(R.string.services_finished),
                    finishedServicesList.size
                )

                loadPendingServices()
            },
            onError = { errorMessage ->
                // Hide the loading indicator
                LoadingDialog.hide()

                // Show an error message to the user
                Log.e("ServiceListFragment", "Failed to fetch services: $errorMessage")
                // You could show an error dialog or a "retry" button here
                val dialog = InfoDialogFragment.newInstance(message = errorMessage)
                dialog.show(parentFragmentManager, "ErrorDialog")
            }
        )
    }

    // TODO: Remove this
    private fun initializeServiceLists() {
        allServicesList = mutableListOf(
            ServiceModel(
                354140, 1, "Luisa Pérez", "Calle Berlín 519", "Mañana", "Ropero",
                "2025-10-01", 1, "Realizado", 1, "Todo Conforme", "48642154", "Miraflores", "15074",
                "+51946878542", "-", "Instalacion", "",
                "Luisa Pérez", "48545121", "-", "", true,
                "https://www.consumer.es/app/uploads/fly-images/110784/img_firma-3-1200x550-cc.jpg",
                null,
                "https://comofuncionaexplicado.com/wp-content/uploads/2024/02/como-funciona-una-ducha-electrica.jpg",
                "https://comofuncionaexplicado.com/wp-content/uploads/2024/02/como-funciona-una-ducha-electrica.jpg",
                "https://comofuncionaexplicado.com/wp-content/uploads/2024/02/como-funciona-una-ducha-electrica.jpg",
                "https://comofuncionaexplicado.com/wp-content/uploads/2024/02/como-funciona-una-ducha-electrica.jpg",
            ),
            ServiceModel(
                354140, 2,
                "Juan Pérez",
                "Av Surco 659",
                "Mañana",
                "Ducha",
                "2025-10-01", 0, "", 0, "", "45878484", "Santiago de Surco", "15049",
                "+51946878543"
            ),
            ServiceModel(
                354140, 3,
                "Maria Gómez",
                "Calle Ficticia 456",
                "Tarde",
                "Terma",
                "2025-10-01", 0, "", 0, "", "", "Santiago de Surco", "15049",
                "+51946878543"
            )
        )
        // Applying filter to the list
        filterServiceLists()
    }

    private fun filterServiceLists() {
        pendingServicesList = allServicesList.filter { it.status != "Realizado" }.toMutableList()
        finishedServicesList = allServicesList.filter { it.status == "Realizado" }.toMutableList()
    }

    fun updateButtonText(button: MaterialButton, prefix: String, count: Int) {
        "$prefix: $count".also { button.text = it }
    }

    fun loadPendingServices() {
        val fragmentTransaction = actualFragmentManager.beginTransaction()

        if (pendingServicesList.isNotEmpty()) {
            val pendingServicesFragment = PendingServicesFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList("pending_services_list", ArrayList(pendingServicesList))
                }
            }

            fragmentTransaction.replace(
                R.id.fragment_service_list_container,
                pendingServicesFragment
            )
            fragmentTransaction.commit()
        } else {
            val noServiceFragment = NoServiceFragment.newInstance(true)

            fragmentTransaction.replace(R.id.fragment_service_list_container, noServiceFragment)
            fragmentTransaction.commit()
        }
    }

    fun loadFinishedServices() {
        val fragmentTransaction = actualFragmentManager.beginTransaction()

        if (finishedServicesList.isNotEmpty()) {
            val finishedServicesFragment = FinishedServicesFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(
                        "finished_services_list",
                        ArrayList(finishedServicesList)
                    )
                }
            }
            fragmentTransaction.replace(
                R.id.fragment_service_list_container,
                finishedServicesFragment
            )
            fragmentTransaction.commit()
        } else {
            val noServiceFragment = NoServiceFragment.newInstance(false)
            fragmentTransaction.replace(R.id.fragment_service_list_container, noServiceFragment)
            fragmentTransaction.commit()
        }
    }


    private fun handleServiceUpdate(updatedService: ServiceModel) {
        // 1. Update the master list
        val indexInAll = allServicesList.indexOfFirst { it.serviceId == updatedService.serviceId }
        if (indexInAll != -1) {
            allServicesList[indexInAll] = updatedService
        }

        // Re-filter your pending and finished lists
        filterServiceLists()

        // Update button counts
        updateButtonText(
            btnToStart,
            getString(R.string.services_to_start),
            pendingServicesList.size
        )
        updateButtonText(
            btnEnded,
            getString(R.string.services_finished),
            finishedServicesList.size
        )

        // 4. Refresh the currently visible child fragment
        // Check which button in toggleButtonGroup is checked to know which fragment is visible
        when (toggleButtonGroup.checkedButtonId) {
            R.id.btnToStart -> {
                if (pendingServicesList.isEmpty()) {
                    val fragmentTransaction = actualFragmentManager.beginTransaction()
                    val noServiceFragment = NoServiceFragment.newInstance(true)
                    fragmentTransaction.replace(
                        R.id.fragment_service_list_container,
                        noServiceFragment
                    )
                    fragmentTransaction.commit()
                } else {
                    // If PendingServicesFragment is visible, update it or reload it
                    val pendingFragment =
                        actualFragmentManager.findFragmentById(R.id.fragment_service_list_container) as? PendingServicesFragment
                    pendingFragment?.updateServices(pendingServicesList)
                }
            }

            R.id.btnEnded -> {
                // If FinishedServicesFragment is visible, update it or reload it
                val finishedFragment =
                    actualFragmentManager.findFragmentById(R.id.fragment_service_list_container) as? FinishedServicesFragment
                finishedFragment?.updateServices(finishedServicesList) // Assuming FinishedServicesFragment has a similar updateServices method
            }
        }
    }

    override fun onStop() {
        super.onStop()
        VolleySingleton.getInstance(requireContext()).requestQueue.cancelAll(Constants.VOLLEY_TAG)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ServiceListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ServiceListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}