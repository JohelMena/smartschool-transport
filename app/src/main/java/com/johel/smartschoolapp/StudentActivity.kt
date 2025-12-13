package com.johel.smartschoolapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import com.google.android.material.appbar.MaterialToolbar
import com.johel.smartschoolapp.api.*
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
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var spinnerGuardian: Spinner
    private lateinit var spinnerRoute: Spinner
    private lateinit var spinnerBus: Spinner

    // --------- Datos en memoria ----------
    private val students = mutableListOf<UiStudent>()
    private val displayStudents = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    private val guardians = mutableListOf<UiGuardian>()
    private lateinit var guardianAdapter: ArrayAdapter<UiGuardian>

    private val routes = mutableListOf<UiRoute>()
    private lateinit var routeAdapter: ArrayAdapter<UiRoute>

    private val buses = mutableListOf<UiBus>()
    private lateinit var busAdapter: ArrayAdapter<UiBus>

    private var selectedStudent: UiStudent? = null
    private var currentPhotoBitmap: Bitmap? = null

    // ================== Clases UI ==================
    data class UiStudent(
        val id: String,
        val fullName: String,
        val grade: String,
        val guardianId: String?,
        val routeId: String?,
        val busId: String?
    )

    data class UiGuardian(
        val id: String,
        val name: String
    ) {
        override fun toString(): String = name
    }

    data class UiRoute(
        val id: String,
        val label: String
    ) {
        override fun toString(): String = label
    }

    data class UiBus(
        val id: String,
        val label: String
    ) {
        override fun toString(): String = label
    }

    // ================== Activity Result Cámara / Galería / Permiso ==================

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
            if (bitmap != null) {
                currentPhotoBitmap = bitmap
                ivPhoto.setImageBitmap(bitmap)
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                @Suppress("DEPRECATION")
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

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // Referencias UI
        ivPhoto = findViewById(R.id.ivStudentPhoto)
        btnSelectPhoto = findViewById(R.id.btnSelectPhoto)
        etName = findViewById(R.id.etStudentName)
        etGrade = findViewById(R.id.etStudentGrade)
        listView = findViewById(R.id.listViewStudents)
        searchView = findViewById(R.id.searchStudent)
        spinnerGuardian = findViewById(R.id.spinnerGuardian)
        spinnerRoute = findViewById(R.id.spinnerRoute)
        spinnerBus = findViewById(R.id.spinnerBus)

        // ListView
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayStudents)
        listView.adapter = adapter

        // Spinners
        guardianAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, guardians)
        guardianAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGuardian.adapter = guardianAdapter

        routeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, routes)
        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRoute.adapter = routeAdapter

        busAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, buses)
        busAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBus.adapter = busAdapter

        // FOTO
        btnSelectPhoto.setOnClickListener { showPhotoSourceDialog() }

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

            val guardianId = (spinnerGuardian.selectedItem as? UiGuardian)?.id?.ifBlank { null }
            val routeId = (spinnerRoute.selectedItem as? UiRoute)?.id?.ifBlank { null }
            val busId = (spinnerBus.selectedItem as? UiBus)?.id?.ifBlank { null }

            val dto = StudentDto(
                id = null,
                fullName = name,
                grade = grade,
                guardianId = guardianId,
                routeId = routeId,
                busId = busId
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

                val guardianId = (spinnerGuardian.selectedItem as? UiGuardian)?.id?.ifBlank { null }
                val routeId = (spinnerRoute.selectedItem as? UiRoute)?.id?.ifBlank { null }
                val busId = (spinnerBus.selectedItem as? UiBus)?.id?.ifBlank { null }

                val dto = StudentDto(
                    id = current.id,
                    fullName = name,
                    grade = grade,
                    guardianId = guardianId,
                    routeId = routeId,
                    busId = busId
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
                etGrade.setText(student.grade)

                // guardian
                if (!student.guardianId.isNullOrBlank()) {
                    val gi = guardians.indexOfFirst { it.id == student.guardianId }
                    spinnerGuardian.setSelection(if (gi >= 0) gi else 0)
                } else spinnerGuardian.setSelection(0)

                // route
                if (!student.routeId.isNullOrBlank()) {
                    val ri = routes.indexOfFirst { it.id == student.routeId }
                    spinnerRoute.setSelection(if (ri >= 0) ri else 0)
                } else spinnerRoute.setSelection(0)

                // bus
                if (!student.busId.isNullOrBlank()) {
                    val bi = buses.indexOfFirst { it.id == student.busId }
                    spinnerBus.setSelection(if (bi >= 0) bi else 0)
                } else spinnerBus.setSelection(0)
            }
        }

        // ================== Búsqueda ==================
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
        loadGuardians()
        loadRoutes()
        loadBuses()
    }

    // =======================================================================
    // FOTO
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

        if (granted) openCamera()
        else requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun openCamera() {
        takePictureLauncher.launch(null)
    }

    private fun openGallery() {
        pickImageLauncher.launch("image/*")
    }

    // =======================================================================
    // API: Students
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
                        students.addAll(
                            dtoList.map { dto ->
                                UiStudent(
                                    id = dto.id ?: "",
                                    fullName = dto.fullName,
                                    grade = dto.grade,
                                    guardianId = dto.guardianId,
                                    routeId = dto.routeId,
                                    busId = dto.busId
                                )
                            }
                        )

                        displayStudents.clear()
                        displayStudents.addAll(
                            students.map { st -> "${st.fullName} - ${st.grade}" }
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

    // =======================================================================
    // API: Guardians
    // =======================================================================

    private fun loadGuardians() {
        ApiClient.guardianService.getGuardians()
            .enqueue(object : Callback<List<GuardianDto>> {
                override fun onResponse(
                    call: Call<List<GuardianDto>>,
                    response: Response<List<GuardianDto>>
                ) {
                    if (response.isSuccessful) {
                        val dtoList = response.body() ?: emptyList()

                        guardians.clear()
                        guardians.add(UiGuardian(id = "", name = "(No guardian)"))
                        guardians.addAll(
                            dtoList.map { dto ->
                                UiGuardian(
                                    id = dto.id ?: "",
                                    name = dto.fullName
                                )
                            }
                        )
                        guardianAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@StudentActivity,
                            "Error loading guardians (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<GuardianDto>>, t: Throwable) {
                    Toast.makeText(
                        this@StudentActivity,
                        "Failed to load guardians: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // =======================================================================
    // API: Routes
    // =======================================================================

    private fun loadRoutes() {
        ApiClient.routeService.getRoutes()
            .enqueue(object : Callback<List<RouteDto>> {
                override fun onResponse(
                    call: Call<List<RouteDto>>,
                    response: Response<List<RouteDto>>
                ) {
                    if (response.isSuccessful) {
                        val dtoList = response.body() ?: emptyList()

                        routes.clear()
                        routes.add(UiRoute(id = "", label = "(No route)"))
                        routes.addAll(
                            dtoList.map { dto ->
                                UiRoute(
                                    id = dto.id ?: "",
                                    // usamos solo el nombre; no dependemos de startPoint/endPoint
                                    label = dto.name
                                )
                            }
                        )
                        routeAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@StudentActivity,
                            "Error loading routes (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<RouteDto>>, t: Throwable) {
                    Toast.makeText(
                        this@StudentActivity,
                        "Failed to load routes: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // =======================================================================
    // API: Buses
    // =======================================================================

    private fun loadBuses() {
        ApiClient.busService.getBuses()
            .enqueue(object : Callback<List<BusDto>> {
                override fun onResponse(
                    call: Call<List<BusDto>>,
                    response: Response<List<BusDto>>
                ) {
                    if (response.isSuccessful) {
                        val dtoList = response.body() ?: emptyList()

                        buses.clear()
                        buses.add(UiBus(id = "", label = "(No bus)"))
                        buses.addAll(
                            dtoList.map { dto ->
                                UiBus(
                                    id = dto.id ?: "",
                                    label = "${dto.plate} (Cap: ${dto.capacity})"
                                )
                            }
                        )
                        busAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@StudentActivity,
                            "Error loading buses (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<BusDto>>, t: Throwable) {
                    Toast.makeText(
                        this@StudentActivity,
                        "Failed to load buses: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // =======================================================================

    private fun clearFields() {
        etName.text.clear()
        etGrade.text.clear()
        spinnerGuardian.setSelection(0)
        spinnerRoute.setSelection(0)
        spinnerBus.setSelection(0)
    }

    private fun findStudentByDisplay(display: String): UiStudent? {
        val parts = display.split(" - ")
        if (parts.size < 2) return null
        val name = parts[0]
        val grade = parts[1]
        return students.find { it.fullName == name && it.grade == grade }
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
