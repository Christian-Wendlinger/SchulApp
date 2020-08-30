package com.sqcw.schulapp

import com.sqcw.schulapp.models.ErledigtModel
import com.sqcw.schulapp.models.FachModel
import com.sqcw.schulapp.models.NoteModel
import com.sqcw.schulapp.models.ToDoModel

var todos = mutableListOf<ToDoModel>()
var erledigt = mutableListOf<ErledigtModel>()
var nichtErledigt = mutableListOf<ErledigtModel>()
var faecher = mutableListOf<FachModel>()
var fachnamen = arrayListOf<String>()
var halbjahr = 0
var klausurNoten = mutableListOf<NoteModel>()
var testNoten = mutableListOf<NoteModel>()
var abfrageNoten = mutableListOf<NoteModel>()
var vortragsNoten = mutableListOf<NoteModel>()


fun berechneFachschnitt(fach: String): Float {
    // Fall 1 - mindestens eine Note vorhanden
    if (klausurNoten.isNotEmpty() || testNoten.isNotEmpty() || abfrageNoten.isNotEmpty() || vortragsNoten.isNotEmpty()) {
        // nur klausurnoten
        if (klausurNoten.isNotEmpty() && testNoten.isEmpty() && abfrageNoten.isEmpty() && vortragsNoten.isEmpty()) {
            return klausurNoten.sumBy { note -> note.punkte }.toFloat() / klausurNoten.size
        }
        // keine Klausurnoten
        else if (klausurNoten.isEmpty() && (testNoten.isNotEmpty() || abfrageNoten.isNotEmpty() || vortragsNoten.isNotEmpty())) {
            return calculateNotenOhneKlausuren()
        } else {
            return (klausurNoten.sumBy { note -> note.punkte }
                .toFloat() / klausurNoten.size + calculateNotenOhneKlausuren()) / 2
        }
    } else {
        return (-1).toFloat()
    }
}

// Schnitt ohne Klausuren ausrechnen
fun calculateNotenOhneKlausuren(): Float {
    var punkte = 0
    var anzahl = 0

    // Testnoten
    if (testNoten.isNotEmpty()) {
        punkte += testNoten.sumBy { note -> note.punkte }
        anzahl += testNoten.size
    }

    // Abfragenoten
    if (abfrageNoten.isNotEmpty()) {
        punkte += abfrageNoten.sumBy { note -> note.punkte }
        anzahl += abfrageNoten.size
    }

    // Vorträge
    if (vortragsNoten.isNotEmpty()) {
        punkte += vortragsNoten.sumBy { note -> note.punkte }
        anzahl += vortragsNoten.size
    }

    return punkte.toFloat() / anzahl
}