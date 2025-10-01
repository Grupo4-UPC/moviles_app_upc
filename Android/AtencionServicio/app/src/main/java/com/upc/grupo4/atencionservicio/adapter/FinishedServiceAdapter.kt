package com.upc.grupo4.atencionservicio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.upc.grupo4.atencionservicio.R
import com.upc.grupo4.atencionservicio.model.ServiceModel

class FinishedServiceAdapter(
    private var services: MutableList<ServiceModel>,
    private val onReviewServiceClick: (ServiceModel) -> Unit,
) : RecyclerView.Adapter<FinishedServiceAdapter.FinishedServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinishedServiceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.finished_item_service, parent, false)
        return FinishedServiceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FinishedServiceViewHolder, position: Int) {
        val currentService = services[position]
        holder.bind(currentService)
    }

    override fun getItemCount() = services.size

    fun updateData(newServices: List<ServiceModel>) {
        services.clear()
        services.addAll(newServices)
        notifyDataSetChanged() // Basic update, consider DiffUtil for better performance
    }

    inner class FinishedServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvServiceID: TextView = itemView.findViewById(R.id.tv_service_id_finished)
        private val tvAddress: TextView = itemView.findViewById(R.id.tv_address_finished)
        private val tvProduct: TextView = itemView.findViewById(R.id.tv_product_finished)
        private val tvShift: TextView = itemView.findViewById(R.id.tv_shift_finished)

        private val btnReviewFinishedService: Button = itemView.findViewById(R.id.btn_review_finish_service)
        fun bind(service: ServiceModel) {
            tvServiceID.text = "OS - ${service.id}" // Assuming 'id' is what you used before
            tvAddress.text = service.address
            tvProduct.text = service.product
            tvShift.text = service.shift


            btnReviewFinishedService.setOnClickListener {
                onReviewServiceClick(service)
            }
        }
    }
}