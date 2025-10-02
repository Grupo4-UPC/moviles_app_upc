package com.upc.grupo4.atencionservicio

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.upc.grupo4.atencionservicio.util.Constants

class NoServiceFragment : Fragment() {
    private var isPendingServicesEmpty: Boolean = false

    private lateinit var tvNoService: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isPendingServicesEmpty = it.getBoolean(Constants.IS_PENDING_SERVICES_EMPTY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_no_service, container, false)
        tvNoService = view.findViewById(R.id.tv_no_service)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val message = if (isPendingServicesEmpty) {
            getString(R.string.tv_no_service_to_start)
        } else {
            getString(R.string.tv_no_service_to_finish)
        }

        tvNoService.text = message
    }

    companion object {
        @JvmStatic
        fun newInstance(isPendingServicesEmpty: Boolean) =
            NoServiceFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(Constants.IS_PENDING_SERVICES_EMPTY, isPendingServicesEmpty)
                }
            }
    }
}