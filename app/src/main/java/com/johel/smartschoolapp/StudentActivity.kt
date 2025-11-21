package com.johel.smartschoolapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.johel.smartschoolapp.data.RouteStorage
import com.johel.smartschoolapp.data.StudentStorage
import com.johel.smartschoolapp.domain.Route
import com.johel.smartschoolapp.domain.Student
import com.johel.smartschoolapp.util.IdGenerator
import com.johel.smartschoolapp.util.Validators

class StudentActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etGrade: EditText
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var ivPhoto: ImageView
    private lateinit var btnSelectPhoto: Button
    private lateinit var spRoute: Spinner

    // List of strings displayed in the ListView
    private val displayStudents = mutableListOf<String>()

    // Currently selected student (if any)
    private var selectedStudent: Student? = null

    // Currently selected photo (camera or gallery)
    private var selectedPhoto: Bitmap? = null

    // Routes loaded from RouteStorage
    private lateinit var routes: List<Route>

    // Launcher to take a picture with the camera (preview bitmap)
    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                selectedPhoto = bitmap
                ivPhoto.setImageBitmap(bitmap)
            } else {
                Toast.makeText(this, "Error taking picture", Toast.LENGTH_SHORT).show()
            }
        }

    // Launcher to pick an image from gallery
    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()

                if (bitmap != null) {
                    selectedPhoto = bitmap
                    ivPhoto.setImageBitmap(bitmap)
                } else {
                    Toast.makeText(this, "Error loading image", Toast.LENGTH_SHORT).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)


        // ---------- Toolbar ----------
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        // ---------- UI ----------
        ivPhoto = findViewById(R.id.ivStudentPhoto)
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto)
        etName = findViewById(R.id.etStudentName)
        etGrade = findViewById(R.id.etStudentGrade)
        listView = findViewById(R.id.listViewStudents)
        spRoute = findViewById(R.id.spRoute)

        btnSelectPhoto.setOnClickListener {
            showPhotoSourceDialog()
        }

        // ---------- Load routes into Spinner ----------
        routes = RouteStorage.getAll()
        val routeNames = routes.map { it.name }
        val routeAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            routeNames
        )
        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spRoute.adapter = routeAdapter

        // ---------- List adapter ----------
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayStudents)
        listView.adapter = adapter

        // ---------- ADD ----------
        findViewById<Button>(R.id.btnAddStudent).setOnClickListener {
            val name = etName.text.toString().trim()
            val grade = etGrade.text.toString().trim()

            if (!Validators.isValidName(name)) {
                Toast.makeText(
                    this,
                    "Invalid name (at least 3 characters)",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (grade.isEmpty()) {
                Toast.makeText(this, "Please enter student grade", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (routes.isEmpty()) {
                Toast.makeText(this, "No routes available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedRouteIndex = spRoute.selectedItemPosition
            val selectedRoute = routes.getOrNull(selectedRouteIndex)

            val newStudent = Student(
                id = IdGenerator.newID(),
                fullName = name,
                birthDate = "",
                enrollmentCode = grade,
                guardianId = "",
                routeId = selectedRoute?.id
            )

            StudentStorage.controller.add(newStudent)

            // Save current photo (if any) for this student
            selectedPhoto?.let { bitmap ->
                StudentStorage.photos[newStudent.id] = bitmap
            }

            clearFields()
            refreshStudentList()
            Toast.makeText(this, "Student added", Toast.LENGTH_SHORT).show()
        }

        // ---------- UPDATE ----------
        findViewById<Button>(R.id.btnUpdateStudent).setOnClickListener {
            val current = selectedStudent
            if (current == null) {
                Toast.makeText(this, "Select a student first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmationDialog("Update") {
                val name = etName.text.toString().trim()
                val grade = etGrade.text.toString().trim()

                if (!Validators.isValidName(name)) {
                    Toast.makeText(
                        this,
                        "Invalid name (at least 3 characters)",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@showConfirmationDialog
                }

                if (grade.isEmpty()) {
                    Toast.makeText(this, "Please enter student grade", Toast.LENGTH_SHORT).show()
                    return@showConfirmationDialog
                }

                val selectedRouteIndex = spRoute.selectedItemPosition
                val selectedRoute = routes.getOrNull(selectedRouteIndex)

                val updatedStudent = Student(
                    id = current.id,
                    fullName = name,
                    birthDate = current.birthDate,
                    enrollmentCode = grade,
                    guardianId = current.guardianId,
                    routeId = selectedRoute?.id
                )

                StudentStorage.controller.update(updatedStudent)

                // Update photo if a new one was selected
                selectedPhoto?.let { bitmap ->
                    StudentStorage.photos[updatedStudent.id] = bitmap
                }

                selectedStudent = updatedStudent
                refreshStudentList()
                Toast.makeText(this, "Student updated", Toast.LENGTH_SHORT).show()
            }
        }

        // ---------- DELETE ----------
        findViewById<Button>(R.id.btnDeleteStudent).setOnClickListener {
            val current = selectedStudent
            if (current == null) {
                Toast.makeText(this, "Select a student first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmationDialog("Delete") {
                StudentStorage.controller.removeById(current.id)
                StudentStorage.photos.remove(current.id)
                selectedStudent = null
                clearFields()
                refreshStudentList()
                Toast.makeText(this, "Student deleted", Toast.LENGTH_SHORT).show()
            }
        }

        // ---------- LIST ITEM CLICK ----------
        listView.setOnItemClickListener { _, _, position, _ ->
            val displayText = adapter.getItem(position) ?: return@setOnItemClickListener
            val student = findStudentByDisplay(displayText)
            if (student != null) {
                selectedStudent = student
                etName.setText(student.fullName)
                etGrade.setText(student.enrollmentCode)

                // Set route in Spinner
                val index = routes.indexOfFirst { it.id == student.routeId }
                if (index >= 0) {
                    spRoute.setSelection(index)
                } else {
                    spRoute.setSelection(0)
                }

                // Show stored photo (if any)
                val photo = StudentStorage.photos[student.id]
                if (photo != null) {
                    ivPhoto.setImageBitmap(photo)
                    selectedPhoto = photo
                } else {
                    ivPhoto.setImageResource(R.mipmap.ic_launcher_round)
                    selectedPhoto = null
                }
            }
        }

        // ---------- SEARCH ----------
        val searchView = findViewById<SearchView>(R.id.searchStudent)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        refreshStudentList()
    }


    // Dialog to choose Camera or Gallery
    private fun showPhotoSourceDialog() {
        val options = arrayOf("Camera", "Gallery")
        AlertDialog.Builder(this)
            .setTitle("Select photo from")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> cameraLauncher.launch(null)
                    1 -> galleryLauncher.launch("image/*")
                }
            }
            .show()
    }

    private fun refreshStudentList() {
        val students = StudentStorage.controller.getAll()
        displayStudents.clear()
        displayStudents.addAll(
            students.map { student ->
                "${student.fullName} - ${student.enrollmentCode}"
            }
        )
        adapter.notifyDataSetChanged()
    }

    private fun clearFields() {
        etName.text.clear()
        etGrade.text.clear()
        ivPhoto.setImageResource(R.mipmap.ic_launcher_round)
        selectedPhoto = null
        if (::routes.isInitialized && routes.isNotEmpty()) {
            spRoute.setSelection(0)
        }
    }

    private fun findStudentByDisplay(display: String): Student? {
        val parts = display.split(" - ")
        if (parts.size < 2) return null
        val name = parts[0]
        val grade = parts[1]
        return StudentStorage.controller.getAll().find {
            it.fullName == name && it.enrollmentCode == grade
        }
    }

    private fun showConfirmationDialog(action: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("$action confirmation")
            .setMessage("Are you sure you want to $action this student?")
            .setPositiveButton("Yes") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
