package com.johel.smartschoolapp

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class BusActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus)

        val btnAddBus = findViewById<Button>(R.id.btnAddBus)
        val btnViewBuses = findViewById<Button>(R.id.btnViewBuses)
        val btnBackToMain = findViewById<Button>(R.id.btnBackToMain)

        btnAddBus.setOnClickListener {
            Toast.makeText(this, "Add Bus clicked", Toast.LENGTH_SHORT).show()
        }

        btnViewBuses.setOnClickListener {
            Toast.makeText(this, "View Bus List clicked", Toast.LENGTH_SHORT).show()
        }

        btnBackToMain.setOnClickListener {
            finish()
        }
    }
}
