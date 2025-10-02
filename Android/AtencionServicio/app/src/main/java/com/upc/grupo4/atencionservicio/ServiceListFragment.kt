package com.upc.grupo4.atencionservicio

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.util.Constants

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

        initializeServiceLists()

        // Set up the listener for results from PendingServicesFragment
        setFragmentResultListener(Constants.SERVICE_STARTED_REQUEST_KEY) { requestKey, bundle ->
            if (requestKey == Constants.SERVICE_STARTED_REQUEST_KEY) {
                val updatedServiceModel: ServiceModel? =
                    bundle.getParcelable(Constants.SERVICE_MODEL_BUNDLE_KEY)
                if (updatedServiceModel != null) {
                    Log.d(
                        "ServiceListFragment",
                        "Received result from pending: ${updatedServiceModel.id}"
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
        // Set initial counts
        updateButtonText(
            btnToStart,
            getString(R.string.services_to_start),
            pendingServicesList.size
        )
        updateButtonText(btnEnded, getString(R.string.services_finished), finishedServicesList.size)

        actualFragmentManager = requireActivity().supportFragmentManager
        loadPendingServices()

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

    private fun initializeServiceLists() {
        //TODO: Replace this with a real call to the server
        allServicesList = mutableListOf(
            ServiceModel(
                "354140-1", "Luisa Pérez", "Av. Siempre Viva 147", "Mañana", "Ropero",
                "Realizado", "Todo Conforme", "uri1", "uri2", "uri3", "uri4",
                "Luisa Pérez", "48545121", "-", "", true
            ),
            ServiceModel(
                "354140-2",
                "Juan Pérez",
                "Av Surco 659, Santiago de Surco 15049",
                "Mañana",
                "Ducha",
            ),
            ServiceModel(
                "354140-3",
                "Maria Gómez",
                "Calle Ficticia 456",
                "Tarde",
                "Terma",
            ),
            ServiceModel(
                "354140-4",
                "Carlos López2",
                "Av. La Paz 789",
                "Noche",
                "Armario",
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
        val indexInAll = allServicesList.indexOfFirst { it.id == updatedService.id }
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