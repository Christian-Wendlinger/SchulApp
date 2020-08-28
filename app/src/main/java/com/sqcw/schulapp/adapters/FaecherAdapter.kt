package com.sqcw.schulapp.adapters

import android.graphics.Color.parseColor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sqcw.schulapp.R
import com.sqcw.schulapp.faecher
import com.sqcw.schulapp.models.FachModel
import kotlinx.android.synthetic.main.noten_list_item.view.*

class FaecherAdapter(private var faecherInternal: MutableList<FachModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            itemView.setOnClickListener {
                if (itemView.expandable.visibility == View.GONE) {
                    itemView.expandable.visibility = View.VISIBLE
                } else {
                    itemView.expandable.visibility = View.GONE
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.noten_list_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return faecherInternal.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.fachtext.text = faecher[position].name
        holder.itemView.card.setCardBackgroundColor(parseColor(faecher[position].farbcode))
    }
}