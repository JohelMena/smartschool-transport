package com.johel.smartschoolapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class StudentActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etGrade: EditText
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private val students = mutableListOf<String>()
    private var selectedIndex = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        etName = findViewById(R.id.etStudentName)
        etGrade = findViewById(R.id.etStudentGrade)
        listView = findViewById(R.id.listViewStudents)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, students)
        listView.adapter = adapter

        findViewById<Button>(R.id.btnAddStudent).setOnClickListener {
            val name = etName.text.toString()
            val grade = etGrade.text.toString()
            if (name.isNotEmpty() && grade.isNotEmpty()) {
                students.add("$name - $grade")
                adapter.notifyDataSetChanged()
                etName.text.clear()
                etGrade.text.clear()
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnUpdateStudent).setOnClickListener {
            if (selectedIndex != -1) {
                showConfirmationDialog("Update") {
                    val name = etName.text.toString()
                    val grade = etGrade.text.toString()
                    students[selectedIndex] = "$name - $grade"
                    adapter.notifyDataSetChanged()
                    Toast.makeText(this, "Student updated", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Select a student first", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnDeleteStudent).setOnClickListener {
            if (selectedIndex != -1) {
                showConfirmationDialog("Delete") {
                    students.removeAt(selectedIndex)
                    adapter.notifyDataSetChanged()
                    etName.text.clear()
                    etGrade.text.clear()
                    selectedIndex = -1
                    Toast.makeText(this, "Student deleted", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Select a student first", Toast.LENGTH_SHORT).show()
            }
        }

        listView.setOnItemClickListener { _, _, position, _ ->
            selectedIndex = position
            val parts = students[position].split(" - ")
            etName.setText(parts[0])
            etGrade.setText(parts[1])
        }

        val searchView = findViewById<SearchView>(R.id.searchStudent)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })
    }

    private fun showConfirmationDialog(action: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("$action Confirmation")
            .setMessage("Are you sure you want to $action this student?")
            .setPositiveButton("Yes") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
