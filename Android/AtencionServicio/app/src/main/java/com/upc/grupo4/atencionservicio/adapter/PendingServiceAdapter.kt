package com.upc.grupo4.atencionservicio.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.upc.grupo4.atencionservicio.R
import com.upc.grupo4.atencionservicio.model.ServiceModel

class PendingServiceAdapter(
    private var services: MutableList<ServiceModel>,
    private val onServiceInfoClick: (ServiceModel) -> Unit,       // Lambda for Info button click
    private val onStartServiceClick: (ServiceModel) -> Unit     // Lambda for Start button click
) : RecyclerView.Adapter<PendingServiceAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val currentService = services[position]
        holder.bind(currentService)
    }

    override fun getItemCount() = services.size

    fun updateData(newServices: List<ServiceModel>) {
        services.clear()
        services.addAll(newServices)
        notifyDataSetChanged() // Basic update, consider DiffUtil for better performance
    }

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvServiceID: TextView = itemView.findViewById(R.id.tvServiceID)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val tvShift: TextView = itemView.findViewById(R.id.tvShift)
        private val tvProduct: TextView = itemView.findViewById(R.id.tvProduct)
        private val btnServiceInfo: Button = itemView.findViewById(R.id.btnServiceInfo)
        private val btnStartService: Button = itemView.findViewById(R.id.btnStartService)

        fun bind(service: ServiceModel) {
            tvServiceID.text = "OS - ${service.id}" // Assuming 'id' is what you used before
            tvAddress.text = service.address
            tvShift.text = service.shift
            tvProduct.text = service.product

            btnServiceInfo.setOnClickListener {
                onServiceInfoClick(service)
            }

            btnStartService.setOnClickListener {
                onStartServiceClick(service)
            }
        }
    }
}