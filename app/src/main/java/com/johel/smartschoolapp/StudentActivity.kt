package com.johel.smartschoolapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class StudentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        // Back to Main Menu
        val btnBack = findViewById<Button>(R.id.btnBackToMain)
        btnBack.setOnClickListener {
            finish()
        }
    }
}
