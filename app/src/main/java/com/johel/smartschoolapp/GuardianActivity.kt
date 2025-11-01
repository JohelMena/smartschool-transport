package com.johel.smartschoolapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GuardianActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardian)

        val btnAddGuardian = findViewById<Button>(R.id.btnAddGuardian)
        val btnViewGuardians = findViewById<Button>(R.id.btnViewGuardians)
        val btnBackToMain = findViewById<Button>(R.id.btnBackToMain)

        btnAddGuardian.setOnClickListener {
            Toast.makeText(this, "Add Guardian clicked", Toast.LENGTH_SHORT).show()
        }

        btnViewGuardians.setOnClickListener {
            Toast.makeText(this, "View Guardian List clicked", Toast.LENGTH_SHORT).show()
        }

        btnBackToMain.setOnClickListener {
            finish()
        }
    }
}
