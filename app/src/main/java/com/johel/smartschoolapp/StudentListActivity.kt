package com.johel.smartschoolapp

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.johel.smartschoolapp.adapter.StudentListAdapter
import com.johel.smartschoolapp.data.StudentStorage

class StudentListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBarList)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // RecyclerView
        recyclerView = findViewById(R.id.rvStudents)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val students = StudentStorage.controller.getAll()
        adapter = StudentListAdapter(students)
        recyclerView.adapter = adapter
    }


}
