package com.sqcw.schulapp.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sqcw.schulapp.*
import com.sqcw.schulapp.adapters.FaecherAdapter
import kotlinx.android.synthetic.main.activity_noten.*


class Noten : AppCompatActivity() {
    private val db = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noten)

        // initialize Button to add noten
        addNoteButton.setOnClickListener {
            initializeNotenDialog()
        }


        faecher = db.readFaecher()
        fachListe.layoutManager = LinearLayoutManager(this)
        fachListe.adapter = FaecherAdapter(faecher)

        // set halbjahrButton
        halbjahr = db.readHalbjahr()
        setHalbjahrButtonText()
        halbjahrWechselnButton.setOnClickListener {
            initializeHalbjahrDialog()
        }
    }


    // create the Code for the dialog to add a playlist
    private fun initializeNotenDialog() {
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

    // open Dialog to change Halbjahr
    private fun initializeHalbjahrDialog() {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.change_halbjahr_dialog, null)

        dialog.setView(dialogView)
        dialog.setCancelable(false)

        //set buttons
        dialog.setPositiveButton("Wechseln") { dialogInterface: DialogInterface, i: Int -> }
        dialog.setNegativeButton("Abbrechen") { dialogInterface: DialogInterface, i: Int -> }
        val customDialog = dialog.create()

        val radioButtons = mutableListOf<RadioButton>(
            dialogView.findViewById(R.id.radioButton111),
            dialogView.findViewById(R.id.radioButton112),
            dialogView.findViewById(R.id.radioButton121),
            dialogView.findViewById(R.id.radioButton122),
            dialogView.findViewById(R.id.radioButton131),
            dialogView.findViewById(R.id.radioButton132)
        )

        for (radioButton in radioButtons) {
            radioButton.setOnClickListener {
                for (radio in radioButtons) {
                    radio.isChecked = false
                }
                radioButton.isChecked = true
            }
        }

        // show and listener
        customDialog.show()
        customDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            for (i in 0 until radioButtons.size) {
                if (radioButtons[i].isChecked) {
                    db.updateHalbjahr(i + 1)
                    customDialog.dismiss()

                    // read new value to change the button text
                    halbjahr = db.readHalbjahr()
                    setHalbjahrButtonText()
                    break
                }
            }
        }
    }

    // setr button of the Text
    private fun setHalbjahrButtonText() {
        when (halbjahr) {
            1 -> halbjahrWechselnButton.text = "11/1"
            2 -> halbjahrWechselnButton.text = "11/2"
            3 -> halbjahrWechselnButton.text = "12/1"
            4 -> halbjahrWechselnButton.text = "12/2"
            5 -> halbjahrWechselnButton.text = "13/1"
            6 -> halbjahrWechselnButton.text = "13/2"
        }
    }
}