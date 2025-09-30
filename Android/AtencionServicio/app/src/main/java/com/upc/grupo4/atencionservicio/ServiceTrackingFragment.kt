package com.upc.grupo4.atencionservicio

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ORDER_ID = "order_id"

/**
 * A simple [Fragment] subclass.
 * Use the [ServiceTrackingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ServiceTrackingFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var spStatus: Spinner
    private lateinit var spSubStatus: Spinner
    private lateinit var btnTakePictures: Button
    private var statusValue: Int? = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service_tracking, container, false)

        spStatus = view.findViewById(R.id.sp_status)
        spSubStatus = view.findViewById(R.id.sp_sub_status)
        btnTakePictures = view.findViewById(R.id.btn_take_pictures)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupStatusSpinner()
        loadSubStatusSpinner()

        btnTakePictures.setOnClickListener {
            val intent = Intent(requireContext(), RegisterPhotosActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupStatusSpinner() {
        val actualStatusOptions = resources.getStringArray(R.array.status_options)

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_custom, // Setting custom spinner item layout
            actualStatusOptions
        )

        // Setting custom dropdown layout
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)

        // 3. Apply the adapter to the spinner
        spStatus.adapter = adapter

        spStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedStatus = parent?.getItemAtPosition(position).toString()

                val selectedTextView = view as? TextView

                when (position) {
                    0 -> {
                        updateSpinnerWithDefaultStyles(selectedTextView)
                        statusValue = 0
                        loadSubStatusSpinner()
                    }

                    1 -> {
                        updateSpinnerWithSelectedStyles(selectedTextView)
                        statusValue = 1
                        loadSubStatusSpinner()
                    }

                    2 -> {
                        updateSpinnerWithSelectedStyles(selectedTextView)
                        statusValue = 2
                        loadSubStatusSpinner()
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Another interface callback
            }
        }

        // Set spinner to show the hint initially
        spStatus.setSelection(0, false)
    }


    private fun loadSubStatusSpinner() {
        val subStatusOptionsResource = when (statusValue) {
            1 -> R.array.sub_status_for_done
            2 -> R.array.sub_status_for_not_done
            else -> R.array.sub_status_initial
        }

        val subStatusOptions = resources.getStringArray(subStatusOptionsResource)

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_custom, // Setting custom spinner item layout
            subStatusOptions
        )

        // Setting custom dropdown layout
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)

        spSubStatus.adapter = adapter

        spSubStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedStatus = parent?.getItemAtPosition(position).toString()

                val selectedTextView = view as? TextView

                when (position) {
                    0 -> {
                        updateSpinnerWithDefaultStyles(selectedTextView)
                    }

                    else -> {
                        updateSpinnerWithSelectedStyles(selectedTextView)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Another interface callback
            }
        }
    }

    private fun updateSpinnerWithDefaultStyles(selectedTextView: TextView?) {
        selectedTextView?.setBackgroundResource(R.drawable.spinner_custom_background)
        selectedTextView?.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.spinner_hint_text_color
            )
        )
    }

    private fun updateSpinnerWithSelectedStyles(selectedTextView: TextView?) {
        selectedTextView?.setBackgroundResource(R.drawable.spinner_background_blue)
        selectedTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ServiceTrackingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ServiceTrackingFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}