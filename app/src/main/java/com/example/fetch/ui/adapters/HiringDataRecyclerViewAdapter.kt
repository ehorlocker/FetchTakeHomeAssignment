package com.example.fetch.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.fetch.R
import com.example.fetch.data.models.HiringResponseItem

class HiringDataRecyclerViewAdapter(private val hiringResponseList: List<HiringResponseItem>) :
    RecyclerView.Adapter<HiringDataRecyclerViewAdapter.HiringResponseViewHolder>() {

    class HiringResponseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idTextView: TextView = view.findViewById(R.id.json_entry_id)
        val listIdTextView: TextView = view.findViewById(R.id.json_entry_list_id)
        val nameTextView: TextView = view.findViewById(R.id.json_entry_name)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HiringResponseViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.json_entry_recycler_view_item, viewGroup, false)

        return HiringResponseViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: HiringResponseViewHolder, position: Int) {
        val currentItem = hiringResponseList[position]
        viewHolder.idTextView.text = "${currentItem.id}"
        viewHolder.listIdTextView.text = "${currentItem.listId}"
        viewHolder.nameTextView.text = currentItem.name
    }

    override fun getItemCount() = hiringResponseList.size

}