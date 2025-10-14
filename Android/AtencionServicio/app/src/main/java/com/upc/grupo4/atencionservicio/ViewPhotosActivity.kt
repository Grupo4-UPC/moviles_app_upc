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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import coil.load
import com.upc.grupo4.atencionservicio.model.PhotoReference
import com.upc.grupo4.atencionservicio.model.PhotoType
import com.upc.grupo4.atencionservicio.util.Constants
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ViewPhotosActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var additionalPhotoView: View
    private lateinit var rightPhotoView: View
    private lateinit var leftPhotoViewView: View
    private lateinit var frontPhotoView: View

    private val photoViewHolders = mutableMapOf<PhotoType, PhotoViewHolder>()

    private val photoReferences = mutableListOf(
        PhotoReference(PhotoType.ADDITIONAL),
        PhotoReference(PhotoType.RIGHT),
        PhotoReference(PhotoType.LEFT),
        PhotoReference(PhotoType.FRONT)
    )

      override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_view_photos)

        val toolbar: Toolbar = findViewById(R.id.toolbar_view_photos)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        // Estado, Subestado y descripción
        findViewById<TextView>(R.id.tv_status_photo_view).text =
            intent.getStringExtra(Constants.STATUS)
        findViewById<TextView>(R.id.tv_sub_status_photo_view).text =
            intent.getStringExtra(Constants.SUB_STATUS)
        findViewById<TextView>(R.id.tv_service_view).text =
            intent.getStringExtra(Constants.SERVICE_DESCRIPTION)

        // Obtener lista de fotos del intent
        val photoReferences: ArrayList<PhotoReference>? =
            intent.getParcelableArrayListExtra(Constants.PHOTO_REFERENCES)
        Log.d("ViewPhotosActivity", "Photo references: $photoReferences")
        // Referencias a los 4 ImageViews
        val iv1: ImageView = findViewById(R.id.iv_photo_view_1)
        val iv2: ImageView = findViewById(R.id.iv_photo_view_2)
        val iv3: ImageView = findViewById(R.id.iv_photo_view_3)
        val iv4: ImageView = findViewById(R.id.iv_photo_view_4)

        
        iv1.setImageResource(R.drawable.ic_placeholder_image)
        iv2.setImageResource(R.drawable.ic_placeholder_image)
        iv3.setImageResource(R.drawable.ic_placeholder_image)
        iv4.setImageResource(R.drawable.ic_placeholder_image)

        // Cargar fotos según índice
        photoReferences?.let { photos ->
            if (photos.isNotEmpty()) iv1.load(photos.getOrNull(0)?.uri)
            if (photos.size > 1) iv2.load(photos.getOrNull(1)?.uri)
            if (photos.size > 2) iv3.load(photos.getOrNull(2)?.uri)
            if (photos.size > 3) iv4.load(photos.getOrNull(3)?.uri)
        }
    }

    private fun setupPhotoItem(itemView: View, type: PhotoType) {
        val layoutImagePreview: FrameLayout = itemView.findViewById(R.id.layout_image_preview_view)
        val ivPhotoPreview: ImageView = itemView.findViewById(R.id.iv_photo_view)

        val viewHolder = PhotoViewHolder(
            layoutImagePreview,
            ivPhotoPreview,
        )
        photoViewHolders[type] = viewHolder

        // Check if there's an existing URI for this type
        val existingUri = photoReferences.find { it.type == type }?.uri
        if (existingUri != null) {
            displayPhoto(type, existingUri)
        }
    }

    private fun displayPhoto(type: PhotoType, uri: Uri) {
        photoViewHolders[type]?.let { holder ->
            holder.ivPhotoPreview.load(uri) {
                size(300, 300)
                crossfade(true) // Optional: Enable crossfade animation
                placeholder(R.drawable.ic_placeholder_image) // Optional: Show a placeholder while loading
            }
        }
    }

     override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    data class PhotoViewHolder(
        val layoutImagePreview: FrameLayout,
        val ivPhotoPreview: ImageView,
    )
}