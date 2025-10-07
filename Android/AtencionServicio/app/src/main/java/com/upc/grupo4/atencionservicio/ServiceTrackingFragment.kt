package com.upc.grupo4.atencionservicio

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.button.MaterialButton
import com.upc.grupo4.atencionservicio.dialogs.InfoDialogFragment
import com.upc.grupo4.atencionservicio.model.PhotoReference
import com.upc.grupo4.atencionservicio.model.PhotoType
import com.upc.grupo4.atencionservicio.model.ServiceModel
import com.upc.grupo4.atencionservicio.model.StatusModel
import com.upc.grupo4.atencionservicio.model.SubStatusModel
import com.upc.grupo4.atencionservicio.util.Constants
import com.upc.grupo4.atencionservicio.util.LoadingDialog
import org.json.JSONArray
import androidx.core.net.toUri
import com.android.volley.DefaultRetryPolicy
import com.android.volley.TimeoutError
import com.upc.grupo4.atencionservicio.dialogs.ConfirmationDialogFragment
import com.upc.grupo4.atencionservicio.util.SubStatusLoadHelper
import com.upc.grupo4.atencionservicio.util.VolleySingleton

class ServiceTrackingFragment : Fragment() {
    private var service: ServiceModel? = null
    private var statusList: ArrayList<StatusModel>? = null

    private lateinit var spStatus: Spinner
    private lateinit var spSubStatus: Spinner
    private lateinit var btnTakePictures: MaterialButton
    private lateinit var btnEnterRequirements: MaterialButton
    private lateinit var btnFinishService: MaterialButton
    private lateinit var ivServiceIcon: ImageView
    private lateinit var ivPhotoIcon: ImageView
    private lateinit var ivRegisterIcon: ImageView
    private lateinit var tvPhotoHint: TextView
    private lateinit var tvClientInfoHint: TextView

    private lateinit var tvStep1RequiredLabel: TextView
    private lateinit var tvStep2RequiredLabel: TextView
    private lateinit var tvStep3RequiredLabel: TextView
    private lateinit var registerPhotosLauncher: ActivityResultLauncher<Intent>
    private lateinit var registerRequirementsLauncher: ActivityResultLauncher<Intent>
    private var receivedPhotoReferences: List<PhotoReference>? = null
    private var statusIdValue: Long? = 0L
    private var statusValueStr: String? = ""
    private var subStatusIdValue: Long? = 0L
    private var subStatusValueStr: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            service = it.getParcelable(Constants.SERVICE)
            statusList = it.getParcelableArrayList(Constants.STATUS_LIST)
        }

        registerPhotosLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ServiceTrackingFragment", "Photo registration was successful.")
                val data: Intent? = result.data
                // Get the ArrayList of Parcelable objects
                val photoRefs = data?.getParcelableArrayListExtra<PhotoReference>(
                    Constants.PHOTO_REFERENCES
                )
                if (photoRefs != null && photoRefs.isNotEmpty()) {
                    receivedPhotoReferences = photoRefs
                    updatePhotoIconColor(R.color.blue_400)
                    updateTakePhotoButtonColor(R.color.blue_400)
                } else {
                    Log.d(
                        "ServiceTrackingFragment",
                        "No photo references received or list is empty."
                    )
                }
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                Log.d("ServiceTrackingFragment", "Photo registration was cancelled.")
            }
        }

        registerRequirementsLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.d("ServiceTrackingFragment", "Requirements registration was successful.")
                val data: Intent? = result.data

                val returnedInfo = data?.getParcelableExtra<ServiceModel>(
                    Constants.SERVICE // Key used by EnterRequirementsFragment
                )
                if (returnedInfo != null) {
                    service = returnedInfo
                    updateRegisterIconColor(R.color.blue_400)
                    updateRegisterButtonColor(R.color.blue_400)
                    btnFinishService.isEnabled = true
                } else {
                    Log.d("ServiceTrackingFragment", "No service information returned.")
                }

            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                Log.d("ServiceTrackingFragment", "Requirements registration was cancelled.")
                // Handle cancellation if needed
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_service_tracking, container, false)

        spStatus = view.findViewById(R.id.sp_status)
        spSubStatus = view.findViewById(R.id.sp_sub_status)
        btnTakePictures = view.findViewById(R.id.btn_take_pictures)
        btnEnterRequirements = view.findViewById(R.id.btn_enter_requirements)
        btnFinishService = view.findViewById(R.id.btn_finish_service)
        ivServiceIcon = view.findViewById(R.id.iv_service_icon)
        ivPhotoIcon = view.findViewById(R.id.iv_photo_icon)
        ivRegisterIcon = view.findViewById(R.id.iv_register_icon)
        tvPhotoHint = view.findViewById(R.id.tv_photo_hint)
        tvClientInfoHint = view.findViewById(R.id.tv_client_info_hint)
        tvStep1RequiredLabel = view.findViewById(R.id.tv_step_1_required_label)
        tvStep2RequiredLabel = view.findViewById(R.id.tv_step_2_required_label)
        tvStep3RequiredLabel = view.findViewById(R.id.tv_step_3_required_label)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

            loadStatusSpinner()
            loadSubStatusSpinner()

            btnTakePictures.setOnClickListener {
                launchRegisterPhotosActivity()
            }

            btnEnterRequirements.setOnClickListener {
                launchEnterRequirementsActivity()
            }

            btnFinishService.setOnClickListener {
                showConfirmSaveServiceDialog()
            }
    }

    private fun loadStatusSpinner(serviceStatus: String? = null, subStatusValue: String? = null) {
        val actualStatusOptions: ArrayList<String> = ArrayList();
        actualStatusOptions.add(getString(R.string.sp_status_default_value))

        statusList?.forEach { status ->
            actualStatusOptions.add(status.statusDescription)
        }

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_custom, // Setting custom spinner item layout
            actualStatusOptions
        )

        // Setting custom dropdown layout
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)

        // 3. Apply the adapter to the spinner
        spStatus.adapter = adapter

        if (serviceStatus != null) {
            val position = adapter.getPosition(serviceStatus)

            // 2. Set the selection only if the status was found in the adapter (position > -1)
            if (position >= 0) {
                spStatus.setSelection(position, false)
                val selectedTextView = spStatus.selectedView as? TextView

                val selectedStatusObj: StatusModel? =
                    statusList?.find { status -> status.statusDescription == serviceStatus }

                statusIdValue = selectedStatusObj?.id
                statusValueStr = serviceStatus
                loadSubStatusSpinner(
                    subStatusValue,
                    statusIdValue
                ) // Ensure sub-status spinner updates based on this pre-selected status

                updateSpinnerWithSelectedStyles(selectedTextView)

                spStatus.isEnabled = false
            }
        }

        spStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedStatus = parent?.getItemAtPosition(position).toString()
                val selectedStatusObj: StatusModel? =
                    statusList?.find { status -> status.statusDescription == selectedStatus }

                val selectedTextView = view as? TextView

                when (position) {
                    0 -> {
                        updateSpinnerWithDefaultStyles(selectedTextView)
                        statusIdValue = 0
                        statusValueStr = ""
                        loadSubStatusSpinner()
                    }

                    else -> {
                        updateSpinnerWithSelectedStyles(selectedTextView)
                        statusIdValue = selectedStatusObj?.id
                        statusValueStr = selectedStatus
                        loadSubStatusSpinner("", statusIdValue)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Another interface callback
            }
        }

        if (serviceStatus == null) {
            // Set spinner to show the hint initially
            spStatus.setSelection(0, false)
        }
    }


    private fun loadSubStatusSpinner(serviceSubStatus: String? = null, statusIdValue: Long? = 0L) {
        if (statusIdValue == 0L) {
            setupSubStatusSpinnerWithDefault()
            return
        }

        val loadingAdapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_custom,
            listOf(getString(R.string.sp_loading_options))
        )
        spSubStatus.adapter = loadingAdapter

        val subStatusLoadHelper = SubStatusLoadHelper()

        subStatusLoadHelper.fetchSubStatusList(
            context = requireContext(),
            statusId = statusIdValue,
            tag = Constants.VOLLEY_TAG,
            onResult = { subStatusListReturned ->
                Log.i("ServiceTrackingFragment", "subStatusListReturned: $subStatusListReturned")
                setupSubStatusSpinner(subStatusListReturned, serviceSubStatus)
            },
            onError = { errorMessage ->
                LoadingDialog.hide()

                Log.e("ServiceTrackingFragment", "Failed to fetch statuses: $errorMessage")

                val dialogMessage =
                    "Ocurió un error al intentar iniciar la ruta. Intente de nuevo."
                InfoDialogFragment.newInstance(
                    message = dialogMessage,
                ).show(parentFragmentManager, "InfoDialogFragmentTag")
            }
        )
    }

    private fun setupSubStatusSpinner(
        subStatusList: List<SubStatusModel>,
        serviceSubStatus: String?
    ) {
        // Create the list of strings for the adapter, including the default hint.
        val subStatusOptions = ArrayList<String>()
        subStatusOptions.add(getString(R.string.sp_sub_status_default_value))
        subStatusList.forEach { subStatus ->
            subStatusOptions.add(subStatus.subStatusDescription)
        }

        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_custom,
            subStatusOptions
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)
        spSubStatus.adapter = adapter

        // Now that the adapter has the full data, try to set the pre-selected value.
        if (serviceSubStatus != null) {
            val position = adapter.getPosition(serviceSubStatus)
            if (position >= 0) {
                subStatusValueStr = serviceSubStatus
                spSubStatus.setSelection(position, false)
                updateSpinnerWithSelectedStyles(spSubStatus.selectedView as? TextView)
                updateServiceIconColor(R.color.blue_400)
                spSubStatus.isEnabled = false
            }
        }

        // Set the listener to handle user interactions.
        spSubStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedTextView = view as? TextView
                if (position == 0) {
                    // Hint "Seleccione sub-estado" is selected
                    updateSpinnerWithDefaultStyles(selectedTextView)
                    subStatusIdValue = null
                    subStatusValueStr = ""
                    updateServiceIconColor(R.color.button_disabled_background_grey)
                } else {
                    updateSpinnerWithSelectedStyles(selectedTextView)
                    subStatusValueStr = parent?.getItemAtPosition(position).toString()
                    subStatusIdValue =
                        subStatusList.find { it.subStatusDescription == subStatusValueStr }?.id
                    updateServiceIconColor(R.color.blue_400)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupSubStatusSpinnerWithDefault() {
        val adapter = ArrayAdapter(
            requireContext(),
            R.layout.spinner_item_custom,
            resources.getStringArray(R.array.sub_status_initial)
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_custom)
        spSubStatus.adapter = adapter
        spSubStatus.setSelection(0, false)
    }

    private fun launchRegisterPhotosActivity() {
        if (statusValueStr != "" && subStatusValueStr != "") {
            val intent = Intent(requireContext(), RegisterPhotosActivity::class.java)
            intent.putExtra(Constants.STATUS, statusValueStr)
            intent.putExtra(Constants.SUB_STATUS, subStatusValueStr)
            intent.putExtra(Constants.SERVICE_DESCRIPTION, service?.serviceDescription)

            if (receivedPhotoReferences != null) {
                val completedPhotoReferences = mutableListOf<PhotoReference>()
                (receivedPhotoReferences as Iterable<Any?>).forEach {
                    completedPhotoReferences.add(it as PhotoReference)
                }

                intent.putParcelableArrayListExtra(
                    Constants.PHOTO_REFERENCES,
                    ArrayList(completedPhotoReferences)
                )
            }

            registerPhotosLauncher.launch(intent)
        } else {
            val dialogMessage =
                "Para poder continuar con la captura de fotos debes seleccionar un estado y subestado."
            InfoDialogFragment.newInstance(
                message = dialogMessage,
            ).show(parentFragmentManager, "InfoDialogFragmentTag")
        }
    }

    private fun launchEnterRequirementsActivity() {
        if (statusValueStr != "" && subStatusValueStr != "" && receivedPhotoReferences != null) {
            val intent = Intent(requireContext(), EnterRequirementsActivity::class.java)
            intent.putExtra(Constants.SERVICE, service)
            registerRequirementsLauncher.launch(intent)
        } else {
            val dialogMessage =
                "Para poder continuar con el registro, debes completar los pasos previos."
            InfoDialogFragment.newInstance(
                message = dialogMessage,
            ).show(parentFragmentManager, "InfoDialogFragmentTag")
        }
    }

    private fun showConfirmSaveServiceDialog() {
        ConfirmationDialogFragment.newInstance(message = getString(R.string.dialog_save_service))
            .setOnAcceptClickListener { saveService() }
            .show(parentFragmentManager, "ConfirmationDialogFragmentTag")
    }

    private fun saveService() {
        LoadingDialog.show(requireContext(), "Guardando información")

        //TODO: Call API to save service
        gatheringServiceData()

        Handler(Looper.getMainLooper()).postDelayed({
            // Once the task is complete, hide the dialog
            LoadingDialog.hide()

            val dialogMessage =
                "Se ha guardado el servicio satisfactoriamente"

            InfoDialogFragment.newInstance(
                title = "OS - ${service?.rootId} - ${service?.serviceId}",
                message = dialogMessage,
                iconResId = R.drawable.ic_check_circle
            ).setOnAcceptClickListener {
                returnToPendingServices()
            }.show(parentFragmentManager, "InfoDialogFragmentTag")

        }, 2000)
    }

    private fun gatheringServiceData() {
        service?.status = statusValueStr
        service?.subStatus = subStatusValueStr

        receivedPhotoReferences?.forEach { ref ->
            when (ref.type) {
                PhotoType.ADDITIONAL -> service?.additionalPhotoUri = ref.filePath
                PhotoType.RIGHT -> service?.rightPhotoUri = ref.filePath
                PhotoType.LEFT -> service?.leftPhotoUri = ref.filePath
                PhotoType.FRONT -> service?.frontPhotoUri = ref.filePath
            }
        }
    }

    private fun returnToPendingServices() {
        val resultIntent = Intent()
        resultIntent.putExtra(
            Constants.SERVICE,
            service
        )
        requireActivity().setResult(Activity.RESULT_OK, resultIntent)
        requireActivity().finish()
    }

    private fun updateSpinnerWithDefaultStyles(selectedTextView: TextView?) {
        selectedTextView?.setBackgroundResource(R.drawable.spinner_custom_background)
        selectedTextView?.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.spinner_hint_text_color
            )
        )
    }

    private fun updateSpinnerWithSelectedStyles(selectedTextView: TextView?) {
        selectedTextView?.setBackgroundResource(R.drawable.spinner_background_blue)
        selectedTextView?.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
    }

    private fun updateServiceIconColor(colorResId: Int) {
        val color = ContextCompat.getColor(requireContext(), colorResId)
        ivServiceIcon.setColorFilter(color)
    }

    private fun updatePhotoIconColor(colorResId: Int) {
        val color = ContextCompat.getColor(requireContext(), colorResId)
        ivPhotoIcon.setColorFilter(color)
    }

    private fun updateTakePhotoButtonColor(colorResId: Int) {
        val color = ContextCompat.getColor(requireContext(), colorResId)
        btnTakePictures.setBackgroundColor(color)
    }

    private fun updateRegisterIconColor(colorResId: Int) {
        val color = ContextCompat.getColor(requireContext(), colorResId)
        ivRegisterIcon.setColorFilter(color)
    }

    private fun updateRegisterButtonColor(colorResId: Int) {
        val color = ContextCompat.getColor(requireContext(), colorResId)
        btnEnterRequirements.setBackgroundColor(color)
    }

    override fun onStop() {
        super.onStop()
        VolleySingleton.getInstance(requireContext()).requestQueue.cancelAll(Constants.VOLLEY_TAG)
    }

    companion object {
        @JvmStatic
        fun newInstance(service: ServiceModel) =
            ServiceTrackingFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(Constants.SERVICE, service)
                }
            }
    }
}