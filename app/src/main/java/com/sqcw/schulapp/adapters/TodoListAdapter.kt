package com.sqcw.schulapp.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sqcw.schulapp.DatabaseHelper
import com.sqcw.schulapp.R
import com.sqcw.schulapp.models.ToDoModel
import com.sqcw.schulapp.todos
import kotlinx.android.synthetic.main.todo_item.view.*

class TodoListAdapter(private var todoInternal: MutableList<ToDoModel>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val db = DatabaseHelper(itemView.context)

        init {
            itemView.setOnClickListener {
                itemView.erledigt.isChecked = !itemView.erledigt.isChecked

                // update checked
                val position = itemView.nummer.text.toString().split(".")[0].toInt()
                todos[position - 1].checked = itemView.erledigt.isChecked
                db.updateToDoStatus(todos[position - 1].id, todos[position - 1].checked)

                // check if day is complete
                var complete = true
                for (todo in todos) {
                    if (!todo.checked) {
                        complete = false
                        break
                    }
                }
                val today = db.readCurrentDay()
                if (complete) db.updateErledigt(today.id, true)
                else db.updateErledigt(today.id, false)
            }

            itemView.erledigt.setOnClickListener {
                // update checked
                val position = itemView.nummer.text.toString().split(".")[0].toInt()
                todos[position - 1].checked = itemView.erledigt.isChecked
                db.updateToDoStatus(todos[position - 1].id, todos[position - 1].checked)

                // check if day is complete
                var complete = true
                for (todo in todos) {
                    if (!todo.checked) {
                        complete = false
                        break
                    }
                }
                val today = db.readCurrentDay()
                if (complete) db.updateErledigt(today.id, true)
                else db.updateErledigt(today.id, false)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return todoInternal.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.nummer.text = "${todos[position].id}."
        holder.itemView.aufgabenText.text = todos[position].todo
        holder.itemView.erledigt.isChecked = todos[position].checked
    }
}