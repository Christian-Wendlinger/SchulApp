package com.sqcw.schulapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.sqcw.schulapp.models.ErledigtModel
import com.sqcw.schulapp.models.FachModel
import com.sqcw.schulapp.models.ToDoModel
import java.util.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "SchulApp.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase?) {
        // create table for ToDos (id, name, checked)
        db?.execSQL(
            "CREATE TABLE todos (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "checked INTEGER)"
        )

        // Morgenroutine data
        db?.execSQL("INSERT INTO todos(name, checked) values ('Aufwachen 5:30', 0)")
        db?.execSQL("INSERT INTO todos(name, checked) values ('10 Minuten Yoga', 0)")
        db?.execSQL("INSERT INTO todos(name, checked) values ('Bett machen', 0)")
        db?.execSQL("INSERT INTO todos(name, checked) values ('Fertig machen', 0)")
        db?.execSQL("INSERT INTO todos(name, checked) values ('Frühstücken', 0)")
        db?.execSQL("INSERT INTO todos(name, checked) values ('Essen für die Schule zubereiten', 0)")
        db?.execSQL("INSERT INTO todos(name, checked) values ('Schulsachen packen', 0)")


        // create table to save done ToDos (id, name, checked)
        db?.execSQL(
            "CREATE TABLE todosErledigt (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "jahr INTEGER," +
                    "monat INTEGER," +
                    "tag INTEGER," +
                    "erledigt INTEGER)"
        )

        // create table for individual courses
        db?.execSQL(
            "CREATE TABLE fach (" +
                    "name TEXT PRIMARY KEY," +
                    "farbcode TEXT)"
        )

        // tabelle für schnitte
        db?.execSQL(
            "CREATE TABLE schnitt (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "halbjahr INTEGER," +
                    "name TEXT," +
                    "durchschnitt REAL," +
                    "gerundet INTEGER)"
        )

        // fächer einfügen
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Chemie', '#c4c4c4')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Informatik', '#000000')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Gesch. / Gem.', '#a16f32')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Deutsch', '#ad0e00')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('VWL / BWL', '#ff7119')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Englisch', '#f5e400')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Biologie', '#3ced39')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Sport', '#11850f')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Physik', '#6ac1cc')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Mathe', '#00277a')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Religion', '#660980')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Spanisch', '#eb13e0')")

        //tabelle für aktuelles Halbjahr
        db?.execSQL("CREATE TABLE halbjahr(id INTEGER PRIMARY KEY)")
        db?.execSQL("INSERT INTO halbjahr VALUES (1)")

        //tabelle für noten
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS todos")
        db?.execSQL("DROP TABLE IF EXISTS todosErledigt")
        db?.execSQL("DROP TABLE IF EXISTS fach")
        db?.execSQL("DROP TABLE IF EXISTS halbjahr")
        db?.execSQL("DROP TABLE IF EXISTS schnitt")
        db?.execSQL("DROP TABLE IF EXISTS noten")
        onCreate(db)
    }

    //read the ToDos from Database and insert into global variable
    fun readToDos(): MutableList<ToDoModel> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM todos", null)

        val tmp = mutableListOf<ToDoModel>()

        while (cursor.moveToNext()) {
            val checked = cursor.getInt(2) != 0
            tmp.add(ToDoModel(cursor.getInt(0), cursor.getString(1), checked))
        }
        cursor.close()

        return tmp
    }

    // save status of ToDos
    fun updateToDoStatus(id: Int, status: Boolean) {
        val db = writableDatabase

        val checked = if (status) 1 else 0
        val contentValues = ContentValues()
        contentValues.put("checked", checked)

        db.update("todos", contentValues, "id = ?", arrayOf(id.toString()))
    }

    // reset ToDos
    fun resetToDos() {
        val db = writableDatabase
        db.execSQL("UPDATE todos SET checked = 0")
    }

    // neuen erledigt eintrag einfügen
    fun insertErledigt(eintrag: ErledigtModel) {
        val db = writableDatabase

        val erledigt = if (eintrag.erledigt) 1 else 0
        val contentValues = ContentValues()
        contentValues.put("jahr", eintrag.jahr)
        contentValues.put("monat", eintrag.monat)
        contentValues.put("tag", eintrag.tag)
        contentValues.put("erledigt", erledigt)

        db.insert("todosErledigt", null, contentValues)
    }

    // erledigt eintrag updaten
    fun updateErledigt(id: Int, status: Boolean) {
        val db = writableDatabase

        val erledigt = if (status) 1 else 0
        val contentValues = ContentValues()
        contentValues.put("erledigt", erledigt)

        db.update("todosErledigt", contentValues, "id = ?", arrayOf(id.toString()))
    }

    // todos tage auslesen, an denen alles erledigt wurde
    fun readErledigt(): MutableList<ErledigtModel> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM todosErledigt WHERE erledigt = 1", null)

        val tmp = mutableListOf<ErledigtModel>()

        while (cursor.moveToNext()) {
            tmp.add(
                ErledigtModel(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    true
                )
            )
        }
        cursor.close()

        return tmp
    }

    // todos tage auslesen, an denen *NICHT* alles erledigt wurde
    fun readNichtErledigt(): MutableList<ErledigtModel> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM todosErledigt WHERE erledigt = 0", null)

        val tmp = mutableListOf<ErledigtModel>()

        while (cursor.moveToNext()) {
            tmp.add(
                ErledigtModel(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getInt(2),
                    cursor.getInt(3),
                    false
                )
            )
        }
        cursor.close()

        return tmp
    }

    // check if the current day is in the database already
    fun isCurrentDay(): Boolean {
        val db = readableDatabase
        val c = Calendar.getInstance()
        val cursor = db.rawQuery(
            "SELECT * FROM todosErledigt WHERE jahr = ? AND monat = ? AND tag = ?",
            arrayOf(
                c.get(Calendar.YEAR).toString(),
                (c.get(Calendar.MONTH) + 1).toString(),
                c.get(Calendar.DATE).toString()
            )
        )

        return cursor.count == 1
    }

    // check if the current day is in the database already
    fun readCurrentDay(): ErledigtModel {
        val db = readableDatabase
        val c = Calendar.getInstance()
        val cursor = db.rawQuery(
            "SELECT * FROM todosErledigt WHERE jahr = ? AND monat = ? AND tag = ?",
            arrayOf(
                c.get(Calendar.YEAR).toString(),
                (c.get(Calendar.MONTH) + 1).toString(),
                c.get(Calendar.DATE).toString()
            )
        )

        cursor.moveToFirst()
        val checked = cursor.getInt(4) != 0
        return ErledigtModel(
            cursor.getInt(0),
            cursor.getInt(1),
            cursor.getInt(2),
            cursor.getInt(3),
            checked
        )
    }


    // read all faecher info
    fun readFaecher(): MutableList<FachModel> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM fach", null)

        val tmp = mutableListOf<FachModel>()

        while (cursor.moveToNext()) {
            tmp.add(FachModel(cursor.getString(0), cursor.getString(1)))
        }
        cursor.close()

        return tmp
    }

    //read only faecher name
    fun readFaecherNames(): ArrayList<String> {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM fach", null)

        val tmp = arrayListOf<String>()
        while (cursor.moveToNext()) {
            tmp.add(cursor.getString(0))
        }
        cursor.close()

        return tmp
    }

    // aktuelles halbjahr lesen
    fun readHalbjahr(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM halbjahr", null)
        cursor.moveToFirst()
        return cursor.getInt(0)
    }

    // aktuelles halbjahr überschreiben
    fun updateHalbjahr(value: Int) {
        val db = writableDatabase
        db.execSQL("UPDATE halbjahr SET id = ?", arrayOf(value))
    }
}