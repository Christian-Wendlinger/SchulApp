package com.sqcw.schulapp.models

data class NoteModel(
    var id: Int,
    var halbjahr: Int,
    var datum: String,
    var fach: String,
    var art: String,
    var note: Float
)