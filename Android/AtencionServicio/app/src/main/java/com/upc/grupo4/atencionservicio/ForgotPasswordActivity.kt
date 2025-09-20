package com.upc.grupo4.atencionservicio

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.upc.grupo4.atencionservicio.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var forgotPasswordBinding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(forgotPasswordBinding.root)

        forgotPasswordBinding.btnSend.setOnClickListener {
            val email = forgotPasswordBinding.etForgotEmail.text?.toString()?.trim().orEmpty()
            if (email.isEmpty()) {
                Toast.makeText(this, "Ingrese su correo", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Correo de recuperación enviado", Toast.LENGTH_SHORT).show()
            }
        }

        forgotPasswordBinding.btnBack.setOnClickListener { finish() }
    }
}