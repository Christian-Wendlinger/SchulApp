package com.sqcw.schulapp.models

data class ErledigtModel(
    var id: Int,
    var jahr: Int,
    var monat: Int,
    var tag: Int,
    var erledigt: Boolean
)