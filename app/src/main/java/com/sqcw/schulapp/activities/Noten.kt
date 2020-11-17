package com.sqcw.schulapp.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sqcw.schulapp.*
import com.sqcw.schulapp.adapters.FaecherAdapter
import com.sqcw.schulapp.models.NoteModel
import kotlinx.android.synthetic.main.activity_noten.*


class Noten : AppCompatActivity() {
    private val db = DatabaseHelper(this)

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_noten)

        // initialize Button to add noten
        addNoteButton.setOnClickListener {
            initializeNotenDialog()
        }

        //aktuelles Halbjahr auslesen
        halbjahr = db.readHalbjahr()


        // facher auslesen
        faecher = db.readFaecher()
        loadFaecher()

        // set halbjahrButton
        setHalbjahrButtonText()
        halbjahrWechselnButton.setOnClickListener {
            initializeHalbjahrDialog()
        }

        // Halbjahrsschnitt anzeigen
        setHalbjahrSchnitt()
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
        val fachspinner = dialogView.findViewById<Spinner>(R.id.fachAuswahl)
        fachspinner.adapter = adapter

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

            val datum = dialogView.findViewById<EditText>(R.id.noteDatum).text.toString()
            val notenString = dialogView.findViewById<EditText>(R.id.note).text.toString()
            val note = if (notenString != "") notenString.toFloat() else (-1).toFloat()
            val fach = fachspinner.selectedItem.toString()

            // Eingaben auf Gültigkeit prüfen
            if (datum != "" && note > -1 && note <= 15.0) {
                // Note in die Datenbank einfügen
                db.insertNote(
                    NoteModel(
                        0,
                        halbjahr,
                        datum,
                        fach,
                        dialogView.findViewById<Spinner>(R.id.notenArt).selectedItem.toString(),
                        note
                    )
                )
                customDialog.dismiss()
                loadFaecher()

                // Notenschnitt des Fachs anpassen
                db.readNoten(fach)
                var fachschnitt = (-1.0).toFloat()
                if (halbjahr == 11) fachschnitt = berechneFachschnittKlasse11(fach)

                // neuen Schnitt in DB eintragen
                db.updateSchnitt(halbjahr, fach, fachschnitt)

                // Halbjahresschnitt anpassen
                val fachSchnitte = mutableListOf<Int>()
                for (f in db.readFaecher()) {
                    val schnitt = db.readRoundedSchnitte(f.name)
                    if (schnitt > -1) fachSchnitte.add(schnitt)
                }
                // Halbjahresschnitt in der Datenbank aktualisieren
                if (fachSchnitte.isNotEmpty()) {
                    db.updateSchnitt(
                        halbjahr,
                        "Halbjahr",
                        fachSchnitte.sum().toFloat() / fachSchnitte.size
                    )
                }
                setHalbjahrSchnitt()
                //Abischnitt anpassen
            }
            // eingaben falsch
            else {
                when {
                    datum == "" -> Toast.makeText(
                        this, "Datum darf nicht leer sein",
                        Toast.LENGTH_SHORT
                    ).show()
                    note == (-1).toFloat() -> Toast.makeText(
                        this,
                        "Note darf nicht leer sein",
                        Toast.LENGTH_SHORT
                    ).show()
                    else -> Toast.makeText(
                        this,
                        "Note muss zwischen 1,0 und 15 liegen",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            }
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
            dialogView.findViewById(R.id.radioButton11),
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

            // add other scenarios later
            if (radioButtons[0].isChecked) db.updateHalbjahr(11)

            customDialog.dismiss()

            // load new data and show
            halbjahr = db.readHalbjahr()
            setHalbjahrButtonText()

            faecher = db.readFaecher()
            loadFaecher()

            setHalbjahrSchnitt()
        }
    }

    // set button of the Text
    private fun setHalbjahrButtonText() {
        when (halbjahr) {
            11 -> halbjahrWechselnButton.text = halbjahr.toString()
        }
    }

    // Halbjahrschnitt festlegen
    @SuppressLint("SetTextI18n")
    fun setHalbjahrSchnitt() {
        val schnitt = db.readSchnitte("Halbjahr")
        if (schnitt > -1) halbjahrSchnittText.text = "${"%.2f".format(schnitt)}"
        else halbjahrSchnittText.text = "N/A"
    }

    fun loadFaecher() {
        fachListe.layoutManager = LinearLayoutManager(this)
        fachListe.adapter = FaecherAdapter(faecher, this)
    }
}