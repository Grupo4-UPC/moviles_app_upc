package com.upc.grupo4.atencionservicio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FAQAdapter(private var items: List<Pair<String, String>>) :
    RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    class FAQViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val question: TextView = view.findViewById(R.id.tvQuestion)
        val answer: TextView = view.findViewById(R.id.tvAnswer)
    }


    private val expandedPositions = mutableSetOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_faq, parent, false)
        return FAQViewHolder(view)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val (q, a) = items[position]
        holder.question.text = q
        holder.answer.text = a


        holder.answer.visibility = if (expandedPositions.contains(position)) View.VISIBLE else View.GONE


        holder.question.setOnClickListener {
            if (expandedPositions.contains(position)) {
                expandedPositions.remove(position)
            } else {
                expandedPositions.add(position)
            }
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int = items.size


    fun updateList(newItems: List<Pair<String, String>>) {
        items = newItems
        expandedPositions.clear() // resetear expansiones al filtrar
        notifyDataSetChanged()
    }
}
