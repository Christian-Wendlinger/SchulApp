package com.sqcw.schulapp.activities

import android.app.Activity
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.sqcw.schulapp.DatabaseHelper
import com.sqcw.schulapp.R
import com.sqcw.schulapp.stundenplanSrc
import kotlinx.android.synthetic.main.activity_stundenplan.*

class Stundenplan : AppCompatActivity() {
    private val db = DatabaseHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stundenplan)

        stundenplanSrc = db.readStundenplanSrc()
        stundenplan.setImageURI(Uri.parse(stundenplanSrc))

        stundenplan.setOnLongClickListener {
            ImagePicker.with(this).galleryOnly().start { resultCode, data ->
                if (resultCode == Activity.RESULT_OK) {
                    val fileUri = data?.data
                    stundenplan.setImageURI(fileUri)
                    db.updateStundenplanSrc(fileUri.toString())
                }
            }
            true
        }
    }
}