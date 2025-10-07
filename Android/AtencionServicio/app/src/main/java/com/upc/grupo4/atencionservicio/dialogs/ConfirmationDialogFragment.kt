package com.upc.grupo4.atencionservicio.dialogs

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import com.upc.grupo4.atencionservicio.R

class ConfirmationDialogFragment : DialogFragment() {
    private var dialogMessage: String? = null

    private var acceptCallBackFn: (() -> Unit)? = null


    companion object {
        private const val ARG_MESSAGE = "arg_message"

        fun newInstance(
            message: String,
        ): ConfirmationDialogFragment {
            val fragment = ConfirmationDialogFragment()
            val args = Bundle().apply {
                putString(ARG_MESSAGE, message)
            }
            fragment.arguments = args
            return fragment
        }
    }

    fun setOnAcceptClickListener(listener: () -> Unit): ConfirmationDialogFragment {
        this.acceptCallBackFn = listener
        return this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dialogMessage = it.getString(ARG_MESSAGE)
        }
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Remove default dialog title bar
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return inflater.inflate(R.layout.fragment_confirmation_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvMessage: TextView = view.findViewById(R.id.tv_dialog_confirm_message)
        val btnAccept: MaterialButton = view.findViewById(R.id.btn_dialog_accept_confirm)
        val btnClose: ImageButton = view.findViewById(R.id.btn_dialog_close_confirm)
        val btnCancel: MaterialButton = view.findViewById(R.id.btn_dialog_cancel_confirm)

        tvMessage.text = dialogMessage

        btnAccept.setOnClickListener {
            dismiss()
            acceptCallBackFn?.invoke()
        }

        btnClose.setOnClickListener {
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}