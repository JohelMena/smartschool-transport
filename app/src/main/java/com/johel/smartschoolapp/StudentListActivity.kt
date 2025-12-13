package com.johel.smartschoolapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.johel.smartschoolapp.api.ApiClient
import com.johel.smartschoolapp.api.StudentDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentListActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentListAdapter
    private val items = mutableListOf<StudentDto>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)

        // Toolbar con botón back
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        recyclerView = findViewById(R.id.recyclerStudents)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = StudentListAdapter(
            items,
            onClick = { student ->
                Toast.makeText(
                    this,
                    "Selected: ${student.fullName} (${student.grade})",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
        recyclerView.adapter = adapter

        loadStudentsFromApi()
    }

    private fun loadStudentsFromApi() {
        ApiClient.studentService.getStudents()
            .enqueue(object : Callback<List<StudentDto>> {
                override fun onResponse(
                    call: Call<List<StudentDto>>,
                    response: Response<List<StudentDto>>
                ) {
                    if (response.isSuccessful) {
                        val list = response.body() ?: emptyList()
                        items.clear()
                        items.addAll(list)
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@StudentListActivity,
                            "Error loading students (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<StudentDto>>, t: Throwable) {
                    Toast.makeText(
                        this@StudentListActivity,
                        "Failed to connect: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
