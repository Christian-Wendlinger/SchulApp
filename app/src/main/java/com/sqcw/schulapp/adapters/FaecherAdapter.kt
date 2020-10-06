package com.sqcw.schulapp.adapters

import android.annotation.SuppressLint
import android.graphics.Color.parseColor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sqcw.schulapp.*
import com.sqcw.schulapp.activities.Noten
import com.sqcw.schulapp.models.FachModel
import kotlinx.android.synthetic.main.noten_list_item.view.*

class FaecherAdapter(private var faecherInternal: MutableList<FachModel>, activity: Noten) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val activity = activity

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.fachtext.text = faecher[position].name
        holder.itemView.card.setCardBackgroundColor(parseColor(faecher[position].farbcode))

        // open database and read values for noten
        val db = DatabaseHelper(holder.itemView.context)

        // Fachschnitt auslesen
        val schnitt = db.readSchnitte(holder.itemView.fachtext.text.toString())
        if (schnitt > -1) holder.itemView.fachschnitt.text =
            "${"%.2f".format(schnitt)} - ${"%.2f".format((17 - schnitt) / 3)}"
        else holder.itemView.fachschnitt.text = "N/A"

        db.readNoten(holder.itemView.fachtext.text.toString())

        // show all noten
        holder.itemView.klausurnotenListe.layoutManager =
            LinearLayoutManager(holder.itemView.context)
        holder.itemView.klausurnotenListe.adapter = NotenAdapter(klausurNoten, activity)
        if (klausurNoten.isEmpty()) holder.itemView.findViewById<TextView>(R.id.klausurenText).visibility =
            View.GONE

        holder.itemView.testnotenListe.layoutManager = LinearLayoutManager(holder.itemView.context)
        holder.itemView.testnotenListe.adapter = NotenAdapter(testNoten, activity)
        if (testNoten.isEmpty()) holder.itemView.findViewById<TextView>(R.id.testsText).visibility =
            View.GONE

        holder.itemView.abfragenotenListe.layoutManager =
            LinearLayoutManager(holder.itemView.context)
        holder.itemView.abfragenotenListe.adapter = NotenAdapter(abfrageNoten, activity)
        if (abfrageNoten.isEmpty()) holder.itemView.findViewById<TextView>(R.id.abfragenText).visibility =
            View.GONE

        holder.itemView.vortragnotenListe.layoutManager =
            LinearLayoutManager(holder.itemView.context)
        holder.itemView.vortragnotenListe.adapter = NotenAdapter(vortragsNoten, activity)
        if (vortragsNoten.isEmpty()) holder.itemView.findViewById<TextView>(R.id.vortragText).visibility =
            View.GONE
    }
}