package com.sqcw.schulapp.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sqcw.schulapp.DatabaseHelper
import com.sqcw.schulapp.R
import com.sqcw.schulapp.adapters.FaecherAdapter
import com.sqcw.schulapp.fachnamen
import com.sqcw.schulapp.faecher
import kotlinx.android.synthetic.main.activity_noten.*


class Noten : AppCompatActivity() {
    private val db = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noten)

        // initialize Button to add noten
        addNoteButton.setOnClickListener {
            initializeDialog()
        }


        faecher = db.readFaecher()
        fachListe.layoutManager = LinearLayoutManager(this)
        fachListe.adapter = FaecherAdapter(faecher)
    }


    // create the Code for the dialog to add a playlist
    private fun initializeDialog() {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.add_note_dialog, null)

        dialog.setView(dialogView)
        dialog.setCancelable(false)

        //set buttons
        dialog.setPositiveButton("Hinzufügen") { dialogInterface: DialogInterface, i: Int -> }
        dialog.setNegativeButton("Abbrechen") { dialogInterface: DialogInterface, i: Int -> }
        val customDialog = dialog.create()

        // initialize Spinner
        fachnamen = db.readFaecherNames()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fachnamen)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogView.findViewById<Spinner>(R.id.fachAuswahl).adapter = adapter

        // initialize second spinner
        val notenArten = arrayListOf<String>()
        notenArten.add("Klausur")
        notenArten.add("Test")
        notenArten.add("Abfrage")
        notenArten.add("Vortrag/Mündlich")

        val adapter1 = ArrayAdapter(this, android.R.layout.simple_spinner_item, notenArten)
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dialogView.findViewById<Spinner>(R.id.notenArt).adapter = adapter1

        customDialog.show()
        customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
        }
    }
}