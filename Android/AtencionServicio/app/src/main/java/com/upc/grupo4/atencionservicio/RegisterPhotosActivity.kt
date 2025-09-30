package com.upc.grupo4.atencionservicio

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.load
import com.upc.grupo4.atencionservicio.model.PhotoReference
import com.upc.grupo4.atencionservicio.model.PhotoType
import com.upc.grupo4.atencionservicio.util.Constants
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegisterPhotosActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var additionalPhotoView: View
    private lateinit var rightPhotoView: View
    private lateinit var leftPhotoViewView: View
    private lateinit var frontPhotoView: View

    private lateinit var btnDeleteAllPhotos: TextView
    private lateinit var btnSavePhotos: Button

    private val photoViewHolders = mutableMapOf<PhotoType, PhotoViewHolder>()

    private val photoReferences = mutableListOf(
        PhotoReference(PhotoType.ADDITIONAL),
        PhotoReference(PhotoType.RIGHT),
        PhotoReference(PhotoType.LEFT),
        PhotoReference(PhotoType.FRONT)
    )
    private var currentPhotoFile: File? = null
    private var currentPhotoType: PhotoType? = null

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success) {
                currentPhotoFile?.let { file ->
                    val photoUri = Uri.fromFile(file) // Get URI from the saved file
                    val photoPath = file.absolutePath // Get absolute path to the file
                    Log.d("PhotoPath", "Path: $photoPath")
                    currentPhotoType?.let { type ->
                        updatePhotoReference(type, photoUri, photoPath)
                        displayPhoto(type, photoUri)
                        updateSavePhotosButtonState()
                    }
                }
            } else {
                // Optionally delete the temp file if capture was cancelled and file was created
                currentPhotoFile?.delete()
            }
            currentPhotoFile = null // Reset
            currentPhotoType = null // Reset
        }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                currentPhotoType?.let { launchCamera(it) }
            } else {
                Toast.makeText(this, "Permiso de cámara denegado.", Toast.LENGTH_SHORT).show()
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register_photos)

        toolbar = findViewById(R.id.toolbar_register_photos)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        val status: String? =
            intent.getStringExtra(Constants.STATUS)

        val subStatus: String? =
            intent.getStringExtra(Constants.SUB_STATUS)

        val tvStatusValue: TextView = findViewById(R.id.tv_status)
        tvStatusValue.text = status

        val tvSubStatusValue: TextView = findViewById(R.id.tv_sub_status)
        tvSubStatusValue.text = subStatus

        btnDeleteAllPhotos = findViewById(R.id.btn_delete_all_photos)
        btnSavePhotos = findViewById(R.id.btn_save_photos)

        additionalPhotoView = findViewById(R.id.photo_item_adicional)
        rightPhotoView = findViewById(R.id.photo_item_lateral_derecho)
        leftPhotoViewView = findViewById(R.id.photo_item_lateral_izquierdo)
        frontPhotoView = findViewById(R.id.photo_item_parte_frontal)

        setupPhotoItem(additionalPhotoView, "Adicional", PhotoType.ADDITIONAL)
        setupPhotoItem(rightPhotoView, "Lateral derecho", PhotoType.RIGHT)
        setupPhotoItem(leftPhotoViewView, "Lateral izq.", PhotoType.LEFT)
        setupPhotoItem(frontPhotoView, "Parte frontal", PhotoType.FRONT)

        btnDeleteAllPhotos.setOnClickListener {
            deleteAllPhotos()
        }

        btnSavePhotos.setOnClickListener {
            // TODO: Implement logic to save/upload the photoReferences
            // For example, iterate through photoReferences and upload URIs
            var allPhotosTaken = true
            val completedPhotoReferences = mutableListOf<PhotoReference>()

            photoReferences.forEach { ref ->
                if (ref.uri == null) {
                    allPhotosTaken = false
                } else {
                    completedPhotoReferences.add(ref)
                }
            }
            if (allPhotosTaken) {
                val resultIntent = Intent()
                // Put the ArrayList of Parcelable objects
                resultIntent.putParcelableArrayListExtra(
                    Constants.PHOTO_REFERENCES,
                    ArrayList(completedPhotoReferences) // Pass only the valid references
                )
                setResult(Activity.RESULT_OK, resultIntent)
                finish() // Close RegisterPhotosActivity
            }
        }

    }

    private fun setupPhotoItem(itemView: View, label: String, type: PhotoType) {
        val layoutButtonCapture: LinearLayout = itemView.findViewById(R.id.layout_button_capture)
        val tvCaptureLabel: TextView = itemView.findViewById(R.id.tv_capture_label)
        val layoutImagePreview: FrameLayout = itemView.findViewById(R.id.layout_image_preview)
        val ivPhotoPreview: ImageView = itemView.findViewById(R.id.iv_photo_preview)
        val btnDeletePhotoItem: ImageButton = itemView.findViewById(R.id.btn_delete_photo_item)

        tvCaptureLabel.text = label

        val viewHolder = PhotoViewHolder(
            layoutButtonCapture,
            layoutImagePreview,
            ivPhotoPreview,
            btnDeletePhotoItem
        )
        photoViewHolders[type] = viewHolder

        layoutButtonCapture.setOnClickListener {
            currentPhotoType = type // Set before checking permission
            checkCameraPermissionAndLaunch()
        }

        btnDeletePhotoItem.setOnClickListener {
            removePhoto(type)
        }

        // Check if there's an existing URI for this type
        val existingUri = photoReferences.find { it.type == type }?.uri
        if (existingUri != null) {
            displayPhoto(type, existingUri)
        } else {
            showCaptureButton(type)
        }
    }

    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED -> {
                currentPhotoType?.let { launchCamera(it) }
            }

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // Show an explanation to the user *asynchronously*
                // For simplicity, just requesting again. In a real app, show a dialog.
                Toast.makeText(
                    this,
                    "Se necesita permiso de cámara para tomar fotos.",
                    Toast.LENGTH_LONG
                ).show()
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }

            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }


    private fun launchCamera(type: PhotoType) {
        this.currentPhotoType = type // Ensure this is set
        try {
            val photoFile: File = createImageFile()
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.fileprovider", // Authority must match AndroidManifest
                photoFile
            )
            takePictureLauncher.launch(photoURI)
        } catch (ex: IOException) {
            // Error occurred while creating the File
            Toast.makeText(this, "Error al preparar la cámara.", Toast.LENGTH_SHORT).show()
            ex.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())

        val storageDir =
            File(getExternalFilesDir(null), "Pictures/images") // App-specific directory
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoFile = this
        }
    }

    private fun updatePhotoReference(type: PhotoType, uri: Uri, filePath: String) {
        photoReferences.find { it.type == type }?.let { ref ->
            ref.uri = uri
            ref.filePath = filePath
        }
    }

    private fun displayPhoto(type: PhotoType, uri: Uri) {
        photoViewHolders[type]?.let { holder ->
            holder.layoutButtonCapture.visibility = View.GONE
            holder.layoutImagePreview.visibility = View.VISIBLE

            holder.ivPhotoPreview.load(uri) {
                size(300, 300)
                crossfade(true) // Optional: Enable crossfade animation
                placeholder(R.drawable.ic_placeholder_image) // Optional: Show a placeholder while loading
            }
        }
    }

    private fun showCaptureButton(type: PhotoType) {
        photoViewHolders[type]?.let { holder ->
            holder.layoutButtonCapture.visibility = View.VISIBLE
            holder.layoutImagePreview.visibility = View.GONE
            holder.ivPhotoPreview.setImageDrawable(null) // Clear previous image
        }
    }

    private fun removePhoto(type: PhotoType) {
        photoReferences.find { it.type == type }?.let { ref ->
            // Delete the file if the path exists
            ref.filePath?.let { path ->
                val fileToDelete = File(path)
                if (fileToDelete.exists()) {
                    if (fileToDelete.delete()) {
                        Toast.makeText(this, "Foto eliminada", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(
                            this,
                            "No se pudo eliminar el archivo.",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
            // Clear the references
            ref.uri = null
            ref.filePath = null
        }
        showCaptureButton(type)
        updateSavePhotosButtonState()
    }

    private fun deleteAllPhotos() {
        var allFilesDeletedSuccessfully = true
        photoReferences.forEach { ref ->
            ref.filePath?.let { path ->
                val fileToDelete = File(path)
                if (fileToDelete.exists()) {
                    if (!fileToDelete.delete()) {
                        allFilesDeletedSuccessfully = false
                        // Log or handle individual file deletion failure if necessary
                    }
                }
            }
            // Clear references
            ref.uri = null
            ref.filePath = null
            showCaptureButton(ref.type)
        }

        if (allFilesDeletedSuccessfully) {
            updateSavePhotosButtonState()
        } else {
            Toast.makeText(
                this,
                "Algunas fotos no pudieron ser eliminadas del almacenamiento.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun updateSavePhotosButtonState() {
        val allPhotosTaken = photoReferences.all { it.uri != null }

        btnSavePhotos.isEnabled = allPhotosTaken
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    data class PhotoViewHolder(
        val layoutButtonCapture: LinearLayout,
        val layoutImagePreview: FrameLayout,
        val ivPhotoPreview: ImageView,
        val btnDeletePhotoItem: ImageButton
    )
}