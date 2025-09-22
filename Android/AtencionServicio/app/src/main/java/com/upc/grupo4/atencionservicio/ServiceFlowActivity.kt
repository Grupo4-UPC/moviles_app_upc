package com.upc.grupo4.atencionservicio

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ServiceFlowActivity : AppCompatActivity() {

    private lateinit var spinnerEstado: Spinner
    private lateinit var spinnerSubestado: Spinner
    private lateinit var btnTomarFoto: Button
    private lateinit var btnIngresarCumplimiento: Button

    private val estados = arrayOf("Seleccionar Estado", "Activo", "Inactivo", "Pendiente")
    private val subestados = arrayOf("Seleccionar Subestado", "En proceso", "Completado", "Cancelado")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service)

        initViews()
        setupSpinners()
        setupButtons()
    }

    private fun initViews() {
        spinnerEstado = findViewById(R.id.spinner_estado)
        spinnerSubestado = findViewById(R.id.spinner_subestado)
        btnTomarFoto = findViewById(R.id.btn_tomar_foto)
        btnIngresarCumplimiento = findViewById(R.id.btn_ingresar_cumplimiento)
    }

    private fun setupSpinners() {
        // Configurar Spinner de Estado
        val estadoAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            estados
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerEstado.adapter = estadoAdapter

        // Configurar Spinner de Subestado
        val subestadoAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            subestados
        ).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinnerSubestado.adapter = subestadoAdapter

        // Listener para cambios en el spinner de estado
        spinnerEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0) {
                    Toast.makeText(this@ServiceFlowActivity, "Estado seleccionado: ${estados[position]}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupButtons() {
        btnTomarFoto.setOnClickListener {
            openCamera()
        }

        btnIngresarCumplimiento.setOnClickListener {
            openDataRegistration()
        }
    }

    private fun openCamera() {
        // Verificar permisos de cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
        } else {
            startCameraActivity()
        }
    }

    private fun startCameraActivity() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(this, "No se encontró aplicación de cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openDataRegistration() {
        // TODO: Crear DataRegistrationActivity (PENDIENTE)
        Toast.makeText(this, "Funcionalidad pendiente", Toast.LENGTH_SHORT).show()
        // val intent = Intent(this, DataRegistrationActivity::class.java)
        // startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCameraActivity()
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            Toast.makeText(this, "Foto capturada exitosamente", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 100
    }
}