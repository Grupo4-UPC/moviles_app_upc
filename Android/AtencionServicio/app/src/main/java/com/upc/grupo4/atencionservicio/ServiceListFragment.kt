package com.upc.grupo4.atencionservicio

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentManager
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.upc.grupo4.atencionservicio.model.ServiceModel

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
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var pendingServicesList: List<ServiceModel>
    lateinit var finishedServicesList: List<ServiceModel>
    lateinit var toggleButtonGroup: MaterialButtonToggleGroup
    lateinit var btnToStart: MaterialButton
    lateinit var btnEnded: MaterialButton
    lateinit var fragmentServiceListContainer: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        pendingServicesList = listOf(
            ServiceModel("354140-2", "Juan Pérez", "Av. Siempre Viva 123", "Mañana", "Ducha"),
            ServiceModel("354140-3", "Maria Gómez", "Calle Ficticia 456", "Tarde", "Terma"),
            ServiceModel("354140-4", "Carlos López2", "Av. La Paz 789", "Noche", "Armario")
        )

        finishedServicesList = listOf(
            ServiceModel("354140-1", "Luisa Pérez", "Av. Siempre Viva 147", "Mañana", "Ropero"),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toggleButtonGroup = view.findViewById(R.id.toggleButtonGroup)
        btnToStart = view.findViewById(R.id.btnToStart)
        btnEnded = view.findViewById(R.id.btnEnded)
        fragmentServiceListContainer = view.findViewById(R.id.fragment_service_list_container)

        // Set initial counts (replace with your actual data)
        updateButtonText(
            btnToStart,
            getString(R.string.services_to_start),
            pendingServicesList.size
        )
        updateButtonText(btnEnded, getString(R.string.services_finished), 1)

        val fragmentManager = requireActivity().supportFragmentManager
        loadPendingServices(fragmentManager)

        toggleButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) { // Only react to the button that is being checked
                when (checkedId) {
                    R.id.btnToStart -> {
                        loadPendingServices(fragmentManager)
                    }

                    R.id.btnEnded -> {
                        loadFinishedServices(fragmentManager)
                    }
                }
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

    fun updateButtonText(button: MaterialButton, prefix: String, count: Int) {
        "$prefix: $count".also { button.text = it }
    }

    fun loadPendingServices(fragmentManager: FragmentManager) {
        val pendingServicesFragment = PendingServicesFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList("pending_services_list", ArrayList(pendingServicesList))
            }
        }

        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_service_list_container, pendingServicesFragment)
        fragmentTransaction.commit()
    }

    fun loadFinishedServices(fragmentManager: FragmentManager) {
        val finishedServicesFragment = FinishedServicesFragment().apply {
            arguments = Bundle().apply {
                putParcelableArrayList("finished_services_list", ArrayList(finishedServicesList))
            }
        }
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_service_list_container, finishedServicesFragment)
        fragmentTransaction.commit()
    }
}