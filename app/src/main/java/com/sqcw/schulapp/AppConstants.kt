package com.sqcw.schulapp

import com.sqcw.schulapp.models.ErledigtModel
import com.sqcw.schulapp.models.FachModel
import com.sqcw.schulapp.models.ToDoModel

var todos = mutableListOf<ToDoModel>()
var erledigt = mutableListOf<ErledigtModel>()
var nichtErledigt = mutableListOf<ErledigtModel>()
var faecher = mutableListOf<FachModel>()
var fachnamen = arrayListOf<String>()