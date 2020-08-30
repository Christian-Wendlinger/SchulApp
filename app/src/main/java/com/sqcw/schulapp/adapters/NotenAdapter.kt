package com.sqcw.schulapp.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sqcw.schulapp.*
import com.sqcw.schulapp.activities.Noten
import com.sqcw.schulapp.models.NoteModel
import kotlinx.android.synthetic.main.note_layout.view.*

class NotenAdapter(private var notenInternal: MutableList<NoteModel>, activity: Noten) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val activity = activity

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.note_layout, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return notenInternal.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.datum.text = notenInternal[position].datum
        holder.itemView.note.text = notenInternal[position].punkte.toString()

        // note löschen
        val db = DatabaseHelper(holder.itemView.context)
        holder.itemView.delete.setOnClickListener {
            initializeDeleteDialog(db, holder, notenInternal[position].id)
        }
    }

    // open Dialog to change Halbjahr
    @SuppressLint("SetTextI18n")
    private fun initializeDeleteDialog(
        db: DatabaseHelper,
        holder: RecyclerView.ViewHolder,
        id: Int
    ) {
        val dialog = AlertDialog.Builder(holder.itemView.context)
        val dialogView = LayoutInflater.from(holder.itemView.context)
            .inflate(R.layout.delete_note_dialog, null)

        dialog.setView(dialogView)
        dialog.setCancelable(false)

        //set buttons
        dialog.setPositiveButton("Löschen") { dialogInterface: DialogInterface, i: Int -> }
        dialog.setNegativeButton("Abbrechen") { dialogInterface: DialogInterface, i: Int -> }
        val customDialog = dialog.create()

        // Text anpassen
        dialogView.findViewById<TextView>(R.id.deleteText).text =
            "Bist du sicher, dass du die ${holder.itemView.note.text} Punkte vom ${holder.itemView.datum.text} im Fach ${notenInternal[0].fach} löschen willst?"

        // show and listener
        customDialog.show()
        customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            db.deleteNote(id)
            customDialog.dismiss()

            // update the view
            db.readNoten(notenInternal[0].fach)
            val art = notenInternal[0].art
            val fach = notenInternal[0].fach
            notenInternal.clear()
            when (art) {
                "Klausur" -> notenInternal.addAll(klausurNoten)
                "Test" -> notenInternal.addAll(testNoten)
                "Abfrage" -> notenInternal.addAll(abfrageNoten)
                else -> notenInternal.addAll(vortragsNoten)
            }

            // Schnitte neu ausrechnen
            // Fachschnitt
            val fachschnitt = berechneFachschnitt(fach)
            db.updateSchnitt(halbjahr, fach, fachschnitt)

            // Halbjahresschnitt
            val fachSchnitte = mutableListOf<Int>()
            for (fach in db.readFaecher()) {
                val schnitt = db.readRoundedSchnitte(fach.name)
                if (schnitt > -1) fachSchnitte.add(schnitt)
            }
            // Halbjahresschnitt in der Datenbank aktualisieren
            if (fachSchnitte.isNotEmpty()) {
                db.updateSchnitt(
                    halbjahr,
                    "Halbjahr",
                    fachSchnitte.sum().toFloat() / fachSchnitte.size
                )
            } else db.updateSchnitt(halbjahr, "Halbjahr", (-1).toFloat())


            // Abischnitt

            // Refresh View
            activity.loadFaecher()
            activity.setHalbjahrSchnitt()
        }
    }
}