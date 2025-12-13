package com.johel.smartschoolapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.appcompat.widget.SearchView
import com.google.android.material.appbar.MaterialToolbar
import com.johel.smartschoolapp.api.ApiClient
import com.johel.smartschoolapp.api.StudentDto
import com.johel.smartschoolapp.domain.Student
import com.johel.smartschoolapp.util.Validators
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentActivity : AppCompatActivity() {

    // --------- UI ----------
    private lateinit var ivPhoto: ImageView
    private lateinit var btnSelectPhoto: Button
    private lateinit var etName: EditText
    private lateinit var etGrade: EditText
    private lateinit var spGuardian: Spinner
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView

    // --------- Datos en memoria (solo dentro de esta Activity) ----------
    private val students = mutableListOf<Student>()          // lista real de estudiantes
    private val displayStudents = mutableListOf<String>()    // textos para el ListView
    private lateinit var adapter: ArrayAdapter<String>

    private var selectedStudent: Student? = null
    private var currentPhotoBitmap: Bitmap? = null           // solo local

    // ================== Activity Result: Cámara / Galería / Permiso ==================

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                currentPhotoBitmap = bitmap
                ivPhoto.setImageBitmap(bitmap)
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                // getBitmap está deprecado, pero sirve para este proyecto
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                currentPhotoBitmap = bitmap
                ivPhoto.setImageBitmap(bitmap)
            }
        }

    private val requestCameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    // =======================================================================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student)

        // ---- Toolbar con botón back ----
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // ---- Referencias UI ----
        ivPhoto = findViewById(R.id.ivStudentPhoto)
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto)
        etName = findViewById(R.id.etStudentName)
        etGrade = findViewById(R.id.etStudentGrade)
        spGuardian = findViewById(R.id.spinnerGuardian)
        listView = findViewById(R.id.listViewStudents)
        searchView = findViewById(R.id.searchStudent)

        // ---- Spinner de guardian ----
        val guardianAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.guardian_names,
            android.R.layout.simple_spinner_item
        )
        guardianAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGuardian.adapter = guardianAdapter

        // ---- Adapter del ListView ----
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayStudents)
        listView.adapter = adapter

        // ================== FOTO: botón Select photo ==================
        btnSelectPhoto.setOnClickListener {
            showPhotoSourceDialog()
        }

        // ================== BOTÓN AGREGAR ==================
        findViewById<Button>(R.id.btnAddStudent).setOnClickListener {
            val name = etName.text.toString().trim()
            val grade = etGrade.text.toString().trim()

            if (!Validators.isValidName(name)) {
                Toast.makeText(this, "Nombre inválido (mínimo 3 caracteres)", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (grade.isEmpty()) {
                Toast.makeText(this, "Ingrese el grado del estudiante", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Guardian seleccionado
            val guardianName = spGuardian.selectedItem?.toString() ?: ""
            val guardianId = if (guardianName == "Select guardian") null else guardianName

            // La API genera el id
            val dto = StudentDto(
                id = null,
                fullName = name,
                grade = grade,
                guardianId = guardianId
            )

            ApiClient.studentService.createStudent(dto)
                .enqueue(object : Callback<StudentDto> {
                    override fun onResponse(
                        call: Call<StudentDto>,
                        response: Response<StudentDto>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@StudentActivity,
                                "Student created in API",
                                Toast.LENGTH_SHORT
                            ).show()
                            clearFields()
                            refreshStudentList()
                        } else {
                            Toast.makeText(
                                this@StudentActivity,
                                "Error creating student (${response.code()})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<StudentDto>, t: Throwable) {
                        Toast.makeText(
                            this@StudentActivity,
                            "Failed to connect to API: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        // ================== BOTÓN ACTUALIZAR ==================
        findViewById<Button>(R.id.btnUpdateStudent).setOnClickListener {
            val current = selectedStudent
            if (current == null) {
                Toast.makeText(this, "Seleccione un estudiante primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmationDialog("Update") {
                val name = etName.text.toString().trim()
                val grade = etGrade.text.toString().trim()

                if (!Validators.isValidName(name)) {
                    Toast.makeText(
                        this,
                        "Nombre inválido (mínimo 3 caracteres)",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@showConfirmationDialog
                }

                if (grade.isEmpty()) {
                    Toast.makeText(
                        this,
                        "Ingrese el grado del estudiante",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@showConfirmationDialog
                }

                val guardianName = spGuardian.selectedItem?.toString() ?: ""
                val guardianId = if (guardianName == "Select guardian") null else guardianName

                val dto = StudentDto(
                    id = current.id,
                    fullName = name,
                    grade = grade,
                    guardianId = guardianId
                )

                ApiClient.studentService.updateStudent(current.id, dto)
                    .enqueue(object : Callback<StudentDto> {
                        override fun onResponse(
                            call: Call<StudentDto>,
                            response: Response<StudentDto>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@StudentActivity,
                                    "Student updated in API",
                                    Toast.LENGTH_SHORT
                                ).show()
                                refreshStudentList()
                            } else {
                                Toast.makeText(
                                    this@StudentActivity,
                                    "Error updating student (${response.code()})",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<StudentDto>, t: Throwable) {
                            Toast.makeText(
                                this@StudentActivity,
                                "Failed to connect to API: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }

        // ================== BOTÓN ELIMINAR ==================
        findViewById<Button>(R.id.btnDeleteStudent).setOnClickListener {
            val current = selectedStudent
            if (current == null) {
                Toast.makeText(this, "Seleccione un estudiante primero", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmationDialog("Delete") {
                ApiClient.studentService.deleteStudent(current.id)
                    .enqueue(object : Callback<StudentDto> {
                        override fun onResponse(
                            call: Call<StudentDto>,
                            response: Response<StudentDto>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@StudentActivity,
                                    "Student deleted in API",
                                    Toast.LENGTH_SHORT
                                ).show()
                                selectedStudent = null
                                clearFields()
                                refreshStudentList()
                            } else {
                                Toast.makeText(
                                    this@StudentActivity,
                                    "Error deleting student (${response.code()})",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<StudentDto>, t: Throwable) {
                            Toast.makeText(
                                this@StudentActivity,
                                "Failed to connect to API: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }

        // ================== Click en la lista ==================
        listView.setOnItemClickListener { _, _, position, _ ->
            val displayText = adapter.getItem(position) ?: return@setOnItemClickListener
            val student = findStudentByDisplay(displayText)
            if (student != null) {
                selectedStudent = student
                etName.setText(student.fullName)
                etGrade.setText(student.enrollmentCode)

                // Ajustamos spinner al guardian del estudiante, si coincide con una opción
                val guardian = student.guardianId
                if (guardian.isNotBlank()) {
                    val index = (0 until spGuardian.count).firstOrNull { i ->
                        spGuardian.getItemAtPosition(i).toString() == guardian
                    }
                    if (index != null) {
                        spGuardian.setSelection(index)
                    } else {
                        spGuardian.setSelection(0) // Select guardian
                    }
                } else {
                    spGuardian.setSelection(0)
                }
            }
        }

        // ================== Búsqueda ==================
        searchView.isIconified = false

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        // Carga inicial
        refreshStudentList()
    }

    // =======================================================================
    // FOTO: diálogo y helpers
    // =======================================================================

    private fun showPhotoSourceDialog() {
        val options = arrayOf("Take photo", "Choose from gallery")

        AlertDialog.Builder(this)
            .setTitle("Select photo source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpen()
                    1 -> openGallery()
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndOpen() {
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        if (granted) {
            openCamera()
        } else {
            requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun openCamera() {
        takePictureLauncher.launch(null)
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    // =======================================================================
    // API + lógica de listas
    // =======================================================================

    private fun refreshStudentList() {
        ApiClient.studentService.getStudents()
            .enqueue(object : Callback<List<StudentDto>> {
                override fun onResponse(
                    call: Call<List<StudentDto>>,
                    response: Response<List<StudentDto>>
                ) {
                    if (response.isSuccessful) {
                        val dtoList = response.body() ?: emptyList()

                        students.clear()
                        students.addAll(dtoList.map { dto ->
                            Student(
                                id = dto.id ?: "",
                                fullName = dto.fullName,
                                birthDate = "",
                                enrollmentCode = dto.grade,
                                guardianId = dto.guardianId ?: ""
                            )
                        })

                        displayStudents.clear()
                        displayStudents.addAll(
                            students.map { st -> "${st.fullName} - ${st.enrollmentCode}" }
                        )
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@StudentActivity,
                            "Error loading students (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<StudentDto>>, t: Throwable) {
                    Toast.makeText(
                        this@StudentActivity,
                        "Failed to connect: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun clearFields() {
        etName.text.clear()
        etGrade.text.clear()
        spGuardian.setSelection(0)
        // Si quieres resetear la foto por defecto, descomenta:
        // ivPhoto.setImageResource(R.mipmap.ic_launcher_round)
        // currentPhotoBitmap = null
    }

    private fun findStudentByDisplay(display: String): Student? {
        val parts = display.split(" - ")
        if (parts.size < 2) return null
        val name = parts[0]
        val grade = parts[1]
        return students.find { it.fullName == name && it.enrollmentCode == grade }
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
