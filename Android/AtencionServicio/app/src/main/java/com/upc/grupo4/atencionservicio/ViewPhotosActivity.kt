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

        toolbar = findViewById(R.id.toolbar_view_photos)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)

        val status: String? =
            intent.getStringExtra(Constants.STATUS)

        val subStatus: String? =
            intent.getStringExtra(Constants.SUB_STATUS)

        val serviceDescription: String? =
            intent.getStringExtra(Constants.SERVICE_DESCRIPTION)

        val tvStatusValue: TextView = findViewById(R.id.tv_status_photo_view)
        tvStatusValue.text = status

        val tvSubStatusValue: TextView = findViewById(R.id.tv_sub_status_photo_view)
        tvSubStatusValue.text = subStatus

        val tvServiceDesc: TextView = findViewById(R.id.tv_service_view)
        tvServiceDesc.text = serviceDescription

        additionalPhotoView = findViewById(R.id.photo_item_adicional_view)
        rightPhotoView = findViewById(R.id.photo_item_lateral_derecho_view)
        leftPhotoViewView = findViewById(R.id.photo_item_lateral_izquierdo_view)
        frontPhotoView = findViewById(R.id.photo_item_parte_frontal_view)

        val photoReferencesFromIntent: ArrayList<PhotoReference>? =
            intent.getParcelableArrayListExtra(
                Constants.PHOTO_REFERENCES
            )

        if (photoReferencesFromIntent != null) {
            photoReferences.clear()
            photoReferences.addAll(photoReferencesFromIntent)
        }

        setupPhotoItem(additionalPhotoView, PhotoType.ADDITIONAL)
        setupPhotoItem(rightPhotoView, PhotoType.RIGHT)
        setupPhotoItem(leftPhotoViewView, PhotoType.LEFT)
        setupPhotoItem(frontPhotoView, PhotoType.FRONT)
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