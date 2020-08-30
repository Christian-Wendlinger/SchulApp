package com.sqcw.schulapp

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.sqcw.schulapp.models.ErledigtModel
import com.sqcw.schulapp.models.FachModel
import com.sqcw.schulapp.models.NoteModel
import com.sqcw.schulapp.models.ToDoModel
import java.util.*
import kotlin.math.roundToInt

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

        // fächer einfügen
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Chemie', '#c4c4c4')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Informatik', '#000000')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Gesch. / Gem.', '#a16f32')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Deutsch', '#ad0e00')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('VWL / BWL', '#ff7119')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Englisch', '#c7c702')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Biologie', '#56cf00')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Sport', '#11850f')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Physik', '#6ac1cc')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Mathe', '#00277a')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Religion', '#660980')")
        db?.execSQL("INSERT INTO fach(name, farbcode) VALUES ('Spanisch', '#eb13e0')")

        //tabelle für aktuelles Halbjahr
        db?.execSQL("CREATE TABLE halbjahr(id INTEGER PRIMARY KEY)")
        db?.execSQL("INSERT INTO halbjahr VALUES (1)")

        //tabelle für noten
        db?.execSQL(
            "CREATE TABLE noten (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "halbjahr INTEGER," +
                    "datum TEXT," +
                    "fach TEXT," +
                    "art TEXT," +
                    "punkte INTEGER)"
        )

        //tabellte für Schnitte
        db?.execSQL(
            "CREATE TABLE schnitte (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "halbjahr INTEGER," +
                    "fach TEXT," +
                    "genau REAL," +
                    "gerundet INTEGER)"
        )

        // Schnitte der einzelnen Fächer pro Halbjahr
        for (fach in mutableListOf(
            "Chemie",
            "Informatik",
            "Gesch. / Gem.",
            "Deutsch",
            "VWL / BWL",
            "Englisch",
            "Biologie",
            "Sport",
            "Physik",
            "Mathe",
            "Religion",
            "Spanisch"
        )) {
            for (i in 1 until 7) {
                db?.execSQL(
                    "INSERT INTO schnitte(halbjahr, fach, genau, gerundet) VALUES (?, ?, -1, -1)",
                    arrayOf(i, fach)
                )
            }
        }

        // Abischnitt
        db?.execSQL("INSERT INTO schnitte(halbjahr, fach, genau, gerundet) VALUES (7, 'Abitur', -1, -1)")

        // Halbahresgesamtschnitte
        for (i in 1 until 7) {
            db?.execSQL(
                "INSERT INTO schnitte(halbjahr, fach, genau, gerundet) VALUES (?, 'Halbjahr', -1, -1)",
                arrayOf(i)
            )
        }
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL("DROP TABLE IF EXISTS todos")
        db?.execSQL("DROP TABLE IF EXISTS todosErledigt")
        db?.execSQL("DROP TABLE IF EXISTS fach")
        db?.execSQL("DROP TABLE IF EXISTS halbjahr")
        db?.execSQL("DROP TABLE IF EXISTS noten")
        db?.execSQL("DROP TABLE IF EXISTS schnitte")
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


    // neuen erledigt eintrag einfügen
    fun insertNote(note: NoteModel) {
        val db = writableDatabase

        val contentValues = ContentValues()
        contentValues.put("halbjahr", note.halbjahr)
        contentValues.put("datum", note.datum)
        contentValues.put("fach", note.fach)
        contentValues.put("art", note.art)
        contentValues.put("punkte", note.punkte)

        db.insert("noten", null, contentValues)
    }

    // alle Noten für ein Fach auslesen
    fun readNoten(fach: String) {
        klausurNoten = readKlausurNoten(fach)
        testNoten = readTestNoten(fach)
        abfrageNoten = readAbfrageNoten(fach)
        vortragsNoten = readVortragsNoten(fach)
    }

    // alle Klausurnoten auslesen
    fun readKlausurNoten(fach: String): MutableList<NoteModel> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM noten WHERE halbjahr = ? AND fach = ? AND art = ?", arrayOf(
                halbjahr.toString(), fach, "Klausur"
            )
        )

        val tmp = mutableListOf<NoteModel>()
        while (cursor.moveToNext()) {
            tmp.add(
                NoteModel(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5)
                )
            )
        }
        cursor.close()
        return tmp
    }

    // alle Testnoten auslesen
    fun readTestNoten(fach: String): MutableList<NoteModel> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM noten WHERE halbjahr = ? AND fach = ? AND art = ?", arrayOf(
                halbjahr.toString(), fach, "Test"
            )
        )

        val tmp = mutableListOf<NoteModel>()
        while (cursor.moveToNext()) {
            tmp.add(
                NoteModel(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5)
                )
            )
        }
        cursor.close()
        return tmp
    }

    // alle Abfragenoten auslesen
    fun readAbfrageNoten(fach: String): MutableList<NoteModel> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM noten WHERE halbjahr = ? AND fach = ? AND art = ?", arrayOf(
                halbjahr.toString(), fach, "Abfrage"
            )
        )

        val tmp = mutableListOf<NoteModel>()
        while (cursor.moveToNext()) {
            tmp.add(
                NoteModel(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5)
                )
            )
        }
        cursor.close()
        return tmp
    }

    // alle Vortragsnoten auslesen
    fun readVortragsNoten(fach: String): MutableList<NoteModel> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM noten WHERE halbjahr = ? AND fach = ? AND art = ?", arrayOf(
                halbjahr.toString(), fach, "Vortrag/Mündlich"
            )
        )

        val tmp = mutableListOf<NoteModel>()
        while (cursor.moveToNext()) {
            tmp.add(
                NoteModel(
                    cursor.getInt(0),
                    cursor.getInt(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getInt(5)
                )
            )
        }
        cursor.close()
        return tmp
    }

    // note löschen
    fun deleteNote(id: Int) {
        val db = writableDatabase
        db.delete("noten", "id = ?", arrayOf(id.toString()))
    }

    // Schnitt eines Faches lesen (genau)
    fun readSchnitte(fach: String): Float {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT genau FROM schnitte WHERE halbjahr = ? AND fach = ? ",
            arrayOf(halbjahr.toString(), fach)
        )

        cursor.moveToFirst()

        return cursor.getFloat(0)
    }

    // Schnitt eines Faches lesen (gerundet)
    fun readRoundedSchnitte(fach: String): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT gerundet FROM schnitte WHERE halbjahr = ? AND fach = ? ",
            arrayOf(halbjahr.toString(), fach)
        )

        cursor.moveToFirst()

        return cursor.getInt(0)
    }

    // Schnitt von einem Fach / Halbjahr / Abitur setzen
    fun updateSchnitt(halbjahr: Int, fach: String, value: Float) {
        val db = writableDatabase

        val contentValues = ContentValues()
        contentValues.put("genau", value)
        contentValues.put("gerundet", value.roundToInt())

        db.update(
            "schnitte",
            contentValues,
            "halbjahr = ? AND fach = ?",
            arrayOf(halbjahr.toString(), fach)
        )
    }
}