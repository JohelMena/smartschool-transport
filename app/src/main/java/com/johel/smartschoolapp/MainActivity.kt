package com.johel.smartschoolapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Students
        findViewById<Button>(R.id.btnStudent).setOnClickListener {
            startActivity(Intent(this, StudentActivity::class.java))
        }

        // Routes
        findViewById<Button>(R.id.btnRoute).setOnClickListener {
            startActivity(Intent(this, RouteActivity::class.java))
        }

        // Attendance
        findViewById<Button>(R.id.btnAttendance).setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }

        // Drivers
        findViewById<Button>(R.id.btnDriver).setOnClickListener {
            startActivity(Intent(this, DriverActivity::class.java))
        }

        // Buses
        findViewById<Button>(R.id.btnBus).setOnClickListener {
            startActivity(Intent(this, BusActivity::class.java))
        }

        // Guardians
        findViewById<Button>(R.id.btnGuardian).setOnClickListener {
            startActivity(Intent(this, GuardianActivity::class.java))
        }

        val btnDriver = findViewById<Button>(R.id.btnDriver)
        btnDriver.setOnClickListener {
            val intent = Intent(this, DriverActivity::class.java)
            startActivity(intent)
        }
    }
}
