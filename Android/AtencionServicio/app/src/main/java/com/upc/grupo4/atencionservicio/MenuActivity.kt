package com.upc.grupo4.atencionservicio

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.io.File
import java.io.FileOutputStream

class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var navigationDrawer: NavigationView
    private lateinit var ivProfilePhoto: ImageView
    private lateinit var btnAdd: FloatingActionButton

    private var photoUri: Uri? = null
    private val CAMERA_PERMISSION_CODE = 100
    private val STORAGE_PERMISSION_CODE = 101

    // Launcher para la cámara
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && photoUri != null) {
            try {
                // Leer la imagen usando el URI
                val inputStream = contentResolver.openInputStream(photoUri!!)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    // Guardar en almacenamiento permanente
                    val permanentFile = File(filesDir, "profile_image.jpg")
                    val outputStream = FileOutputStream(permanentFile)
                    bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, outputStream)
                    outputStream.flush()
                    outputStream.close()

                    // Guardar ruta en SharedPreferences
                    val prefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                    prefs.edit().putString("profile_image_path", permanentFile.absolutePath).apply()

                    // Mostrar imagen en el menú lateral
                    ivProfilePhoto.setImageBitmap(bitmap)
                    Toast.makeText(this, "Foto actualizada correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }

    // Launcher para la galería
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                try {
                    val inputStream = contentResolver.openInputStream(it)
                    if (inputStream != null) {
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream.close()

                        if (bitmap != null) {
                            // Guardar en almacenamiento interno permanente
                            val filename = "profile_image.jpg"
                            val file = File(filesDir, filename)
                            val outputStream = FileOutputStream(file)
                            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 90, outputStream)
                            outputStream.flush()
                            outputStream.close()

                            // Guardar la ruta en SharedPreferences
                            val prefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
                            prefs.edit().putString("profile_image_path", file.absolutePath).apply()

                            // Mostrar la imagen en el menú lateral
                            ivProfilePhoto.setImageBitmap(bitmap)
                            Toast.makeText(this, "Foto actualizada correctamente", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationDrawer = findViewById(R.id.navigation_drawer)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.menu, R.string.my_services
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationDrawer.setNavigationItemSelectedListener(this)

        // Inicializar vistas del header
        val headerView = navigationDrawer.getHeaderView(0)
        ivProfilePhoto = headerView.findViewById(R.id.ivProfilePhoto)
        btnAdd = headerView.findViewById(R.id.btnAdd)

        // Cargar la foto de perfil guardada
        loadProfileImage()

        // Solicitar todos los permisos al iniciar
        requestAllPermissions()

        // Listener para el botón de agregar foto
        btnAdd.setOnClickListener {
            showImagePickerDialog()
        }

        // Set the initial fragment
        replaceFragment(ServiceListFragment())
    }

    override fun onResume() {
        super.onResume()
        // Recargar la foto cada vez que se vuelve a esta actividad
        loadProfileImage()
    }

    private fun loadProfileImage() {
        val prefs = getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE)
        val imagePath = prefs.getString("profile_image_path", null)

        if (imagePath != null) {
            val file = File(imagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                ivProfilePhoto.setImageBitmap(bitmap)
            } else {
                // Si no existe el archivo, usar la imagen por defecto
                ivProfilePhoto.setImageResource(R.drawable.foto_perfil)
            }
        } else {
            // Si no hay foto guardada, usar la imagen por defecto
            ivProfilePhoto.setImageResource(R.drawable.foto_perfil)
        }
    }

    private fun requestAllPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        // Permiso de cámara
        if (!checkCameraPermission()) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }

        // Permiso de almacenamiento
        if (!checkStoragePermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            } else {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                permissionsToRequest.toTypedArray(),
                CAMERA_PERMISSION_CODE
            )
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Tomar foto", "Elegir de galería", "Cancelar")
        val builder = AlertDialog.Builder(this)

        // Crear un TextView personalizado para el título con color azul
        val titleView = TextView(this).apply {
            text = "Selecciona una opción"
            setTextColor(Color.parseColor("#0078D4")) // Color azul
            textSize = 20f
            setPadding(60, 40, 60, 40)
            setTypeface(null, Typeface.BOLD)
        }

        builder.setCustomTitle(titleView)
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    if (checkCameraPermission()) {
                        openCamera()
                    } else {
                        requestCameraPermission()
                    }
                }
                1 -> {
                    if (checkStoragePermission()) {
                        openGallery()
                    } else {
                        requestStoragePermission()
                    }
                }
                2 -> dialog.dismiss()
            }
        }

        val dialog = builder.create()

        // Aplicar esquinas redondeadas al diálogo
        dialog.window?.setBackgroundDrawableResource(R.drawable.dialog_rounded_background)

        dialog.show()
    }

    private fun checkCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_PERMISSION_CODE
        )
    }

    private fun checkStoragePermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                STORAGE_PERMISSION_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_CODE
            )
        }
    }

    private fun openCamera() {
        try {
            // Crear archivo temporal en el directorio de caché de la app
            val photoFile = File(cacheDir, "profile_photo_${System.currentTimeMillis()}.jpg")

            // Obtener URI usando FileProvider
            val uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )

            photoUri = uri

            // Lanzar la cámara con el nuevo método
            takePictureLauncher.launch(uri)

        } catch (e: Exception) {
            Toast.makeText(this, "Error al abrir la cámara", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        pickImageLauncher.launch(intent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
                }
            }
            STORAGE_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGallery()
                } else {
                    Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_more_services -> {
                // Acción para "Obtener más servicios"
                Toast.makeText(this, "Obtener más servicios", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_foto_check -> {
                // Abrir la pantalla de Foto Check
                val intent = Intent(this, FotoCheckActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_logout -> {
                // Cerrar sesión y volver al login
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, fragment)
            .commit()
    }
}