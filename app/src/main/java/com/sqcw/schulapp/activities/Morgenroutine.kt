package com.sqcw.schulapp.activities

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sqcw.schulapp.*
import com.sqcw.schulapp.adapters.TodoListAdapter
import com.sqcw.schulapp.models.ErledigtModel
import kotlinx.android.synthetic.main.activity_morgenroutine.*
import sun.bob.mcalendarview.MarkStyle
import java.util.*

class Morgenroutine : AppCompatActivity() {
    private val db = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_morgenroutine)

        initializeTodos()

        // read ToDos Data
        erledigt = db.readErledigt()
        nichtErledigt = db.readNichtErledigt()


        // mark current day
        val isCurrentDay = db.isCurrentDay()
        val calendar = Calendar.getInstance()
        kalender.setMarkedStyle(MarkStyle.DOT, Color.parseColor("#EF26AC"))
        kalender.markDate(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DATE)
        )


        // mark previous days
        kalender.setMarkedStyle(MarkStyle.DOT, Color.parseColor("#8ce68a"))
        // erfolgreiche Tage
        for (erledigtDay in erledigt) {
            kalender.markDate(erledigtDay.jahr, erledigtDay.monat, erledigtDay.tag)
        }

        kalender.setMarkedStyle(MarkStyle.DOT, Color.parseColor("#e68a8a"))
        // nicht erfolgreiche Tage
        for (erledigtDay in nichtErledigt) {
            kalender.markDate(erledigtDay.jahr, erledigtDay.monat, erledigtDay.tag)
        }

        // wenn ein neuer Tag ist
        if (!isCurrentDay) {
            db.insertErledigt(
                ErledigtModel(
                    0,
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH) + 1,
                    calendar.get(Calendar.DATE),
                    false
                )
            )
            db.resetToDos()

            // update view
            initializeTodos()
        }

    }

    // fill recyclerview
    fun initializeTodos() {
        todos = db.readToDos()
        todoListe.layoutManager = LinearLayoutManager(this)
        todoListe.adapter = TodoListAdapter(todos)
    }
}