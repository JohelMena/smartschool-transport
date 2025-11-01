package com.johel.smartschoolapp

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast

class DriverActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)

        val btnAddDriver = findViewById<Button>(R.id.btnAddDriver)
        val btnViewDrivers = findViewById<Button>(R.id.btnViewDrivers)
        val btnBackToMain = findViewById<Button>(R.id.btnBackToMain)

        btnAddDriver.setOnClickListener {
            Toast.makeText(this, "Add Driver clicked", Toast.LENGTH_SHORT).show()
        }

        btnViewDrivers.setOnClickListener {
            Toast.makeText(this, "View Drivers clicked", Toast.LENGTH_SHORT).show()
        }

        btnBackToMain.setOnClickListener {
            finish()
        }
    }
}
