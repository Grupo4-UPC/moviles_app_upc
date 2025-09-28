package com.upc.grupo4.atencionservicio

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.upc.grupo4.atencionservicio.databinding.ActivityLoginBinding
import androidx.appcompat.widget.Toolbar
import android.view.Menu
import android.view.MenuItem


class LoginActivity : AppCompatActivity() {
    lateinit var loginBinding: ActivityLoginBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        loginBinding = ActivityLoginBinding .inflate(layoutInflater)
        setContentView(loginBinding.root)

        //Toolbar
        val toolbar: Toolbar = loginBinding.loginToolbar
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Atencion de Servicio"


        loginBinding.btnLogin.setOnClickListener {
            val email = loginBinding.etEmail.text?.toString()?.trim().orEmpty()
            val pass = loginBinding.etPassword.text?.toString()?.trim().orEmpty()
            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Ingrese usuario y contraseña", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
            }
        }

        loginBinding.btnForgot.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        loginBinding.btnLogin.setOnClickListener {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }
    }

    //Icono
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_faq, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_faq -> {
                startActivity(Intent(this, FAQActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}

