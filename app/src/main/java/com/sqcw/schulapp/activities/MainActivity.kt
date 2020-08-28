package com.sqcw.schulapp.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sqcw.schulapp.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        morgenroutineButton.setOnClickListener {
            val intent = Intent(this, Morgenroutine::class.java)
            startActivity(intent)
        }

        stundenplanButton.setOnClickListener {
            val intent = Intent(this, Stundenplan::class.java)
            startActivity(intent)
        }

        notenButton.setOnClickListener {
            val intent = Intent(this, Noten::class.java)
            startActivity(intent)
        }
    }
}