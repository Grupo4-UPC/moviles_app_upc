package com.upc.grupo4.atencionservicio.util // Or any other appropriate package

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.widget.TextView
import com.upc.grupo4.atencionservicio.R
import androidx.core.graphics.drawable.toDrawable

object LoadingDialog {

    private var dialog: Dialog? = null

    fun show(context: Context,  message: String? = null) {
        // If a dialog is already showing, do nothing
        if (dialog?.isShowing == true) {
            return
        }

        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_loading, null)

        val tvMessage: TextView = dialogView.findViewById(R.id.tv_loading_message)

        // Set the message dynamically. Use a default if null.
        tvMessage.text = message ?: context.getString(R.string.loading_default_message)

        dialog = Dialog(context).apply {
            setContentView(dialogView)
            setCancelable(false) // Prevent user from dismissing by tapping outside
            window?.setBackgroundDrawable(Color.TRANSPARENT.toDrawable()) // Make the dialog window background transparent
        }

        dialog?.show()
    }

    fun hide() {
        dialog?.dismiss()
        dialog = null // Clean up reference
    }
}
