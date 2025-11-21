package com.johel.smartschoolapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class StudentMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_menu)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBarStudentMenu)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        // Go to CRUD screen
        findViewById<Button>(R.id.btnStudentCrud).setOnClickListener {
            startActivity(Intent(this, StudentActivity::class.java))
        }

        // Go to RecyclerView list
        findViewById<Button>(R.id.btnStudentListMenu).setOnClickListener {
            startActivity(Intent(this, StudentListActivity::class.java))
        }
    }
}