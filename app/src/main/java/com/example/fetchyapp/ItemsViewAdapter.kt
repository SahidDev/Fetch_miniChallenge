package com.example.fetchyapp

import ItemsViewHolder
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ItemsViewAdapter(context: Context, items: List<ItemCollection>): RecyclerView.Adapter<ItemsViewHolder>() {
    val context = context
    val items = items
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_collection_view, parent, false)
        return ItemsViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        holder.idTextView.setText(items.get(position).getIdText())
        for (item in items.get(position).getItems()) {
            val itemHolderView: View = LayoutInflater.from(context).inflate(R.layout.item_card, null)
            itemHolderView.findViewById<TextView>(R.id.ItemText).setText(item.getNameText())
            holder.itemsList.addView(itemHolderView)
        }
    }

    override fun getItemCount(): Int {
        return items.size;
    }
}
