package com.upc.grupo4.atencionservicio.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import android.view.Window
import com.upc.grupo4.atencionservicio.R // Your R file

class InfoDialogFragment : DialogFragment() {

    private var dialogTitle: String? = null
    private var dialogMessage: String? = null
    private var iconResId: Int? = null

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_MESSAGE = "arg_message"
        private const val ARG_ICON_RES_ID = "arg_icon_res_id"

        fun newInstance(
            title: String = "Información",
            message: String,
            iconResId: Int? = R.drawable.ic_warning_themed, // Default icon
        ): InfoDialogFragment {
            val fragment = InfoDialogFragment()
            val args = Bundle().apply {
                putString(ARG_TITLE, title)
                putString(ARG_MESSAGE, message)
                iconResId?.let { putInt(ARG_ICON_RES_ID, it) }
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dialogTitle = it.getString(ARG_TITLE)
            dialogMessage = it.getString(ARG_MESSAGE)
            if (it.containsKey(ARG_ICON_RES_ID)) {
                iconResId = it.getInt(ARG_ICON_RES_ID)
            }
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
        return inflater.inflate(R.layout.fragment_info_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTitle: TextView = view.findViewById(R.id.tv_dialog_title)
        val tvMessage: TextView = view.findViewById(R.id.tv_dialog_message)
        val ivIcon: ImageView = view.findViewById(R.id.iv_dialog_icon)
        val btnAccept: MaterialButton = view.findViewById(R.id.btn_dialog_accept_info)
        val btnClose: ImageButton = view.findViewById(R.id.btn_close_dialog_info)

        tvTitle.text = dialogTitle
        tvMessage.text = dialogMessage

        iconResId?.let {
            ivIcon.setImageResource(it)
        } ?: run {
            ivIcon.visibility = View.GONE // Hide icon if not provided
        }

        btnAccept.setOnClickListener {
            dismiss()
        }

        btnClose.setOnClickListener {
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