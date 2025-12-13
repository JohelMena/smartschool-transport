package com.johel.smartschoolapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.johel.smartschoolapp.api.ApiClient
import com.johel.smartschoolapp.api.BusDto
import com.johel.smartschoolapp.api.RouteDto
import com.johel.smartschoolapp.api.StudentDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AtendanceActivity : AppCompatActivity() {

    // UI
    private lateinit var spinnerStudent: Spinner
    private lateinit var spinnerRoute: Spinner
    private lateinit var spinnerBus: Spinner
    private lateinit var spinnerStatus: Spinner
    private lateinit var etDate: EditText

    private lateinit var btnAdd: Button
    private lateinit var btnViewList: Button
    private lateinit var btnBack: Button

    // Datos para spinners
    private val students = mutableListOf<UiStudent>()
    private lateinit var studentAdapter: ArrayAdapter<UiStudent>

    private val routes = mutableListOf<UiRoute>()
    private lateinit var routeAdapter: ArrayAdapter<UiRoute>

    private val buses = mutableListOf<UiBus>()
    private lateinit var busAdapter: ArrayAdapter<UiBus>

    // Attendance solo en memoria
    private val attendances = mutableListOf<UiAttendance>()

    // ======== Clases UI ========

    data class UiStudent(
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

    data class UiAttendance(
        val id: String,
        val student: UiStudent,
        val route: UiRoute?,
        val bus: UiBus?,
        val date: String,
        val status: String
    ) {
        override fun toString(): String {
            val routeText = route?.label ?: "(no route)"
            val busText = bus?.label ?: "(no bus)"
            return "${student.name} - $date - $status - $routeText - $busText"
        }
    }

    // ===================== onCreate =====================

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_atendence)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBarAttendance)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // Referencias UI
        spinnerStudent = findViewById(R.id.spinnerStudent)
        spinnerRoute = findViewById(R.id.spinnerRoute)
        spinnerBus = findViewById(R.id.spinnerBus)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        etDate = findViewById(R.id.etDate)

        btnAdd = findViewById(R.id.btnAddAttendance)
        btnViewList = findViewById(R.id.btnViewAttendanceList)
        btnBack = findViewById(R.id.btnBackToMain)

        // Adapters
        studentAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, students)
        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStudent.adapter = studentAdapter

        routeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, routes)
        routeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRoute.adapter = routeAdapter

        busAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, buses)
        busAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerBus.adapter = busAdapter

        // Status (Present / Absent)
        val statusOptions = listOf("Present", "Absent")
        val statusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, statusOptions)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatus.adapter = statusAdapter

        // Botones
        btnAdd.setOnClickListener { onAddAttendance() }
        btnViewList.setOnClickListener { showAttendanceListDialog() }
        btnBack.setOnClickListener { finish() }

        // Cargar datos para spinners (desde API existente)
        loadStudents()
        loadRoutes()
        loadBuses()
    }

    // ===================== LÓGICA =====================

    private fun onAddAttendance() {
        val student = spinnerStudent.selectedItem as? UiStudent
        if (student == null || student.id.isBlank()) {
            Toast.makeText(this, "Select a student", Toast.LENGTH_SHORT).show()
            return
        }

        val route = spinnerRoute.selectedItem as? UiRoute
        val bus = spinnerBus.selectedItem as? UiBus
        val date = etDate.text.toString().trim()
        val status = spinnerStatus.selectedItem as? String ?: "Present"

        if (date.isEmpty()) {
            Toast.makeText(this, "Enter a date", Toast.LENGTH_SHORT).show()
            return
        }

        val attendance = UiAttendance(
            id = System.currentTimeMillis().toString(),
            student = student,
            route = if (route?.id.isNullOrBlank()) null else route,
            bus = if (bus?.id.isNullOrBlank()) null else bus,
            date = date,
            status = status
        )

        attendances.add(attendance)
        Toast.makeText(this, "Attendance saved (local only)", Toast.LENGTH_SHORT).show()

        // Limpiar solo la fecha (puedes limpiar todo si quieres)
        // etDate.text.clear()
    }

    private fun showAttendanceListDialog() {
        if (attendances.isEmpty()) {
            Toast.makeText(this, "No attendance records", Toast.LENGTH_SHORT).show()
            return
        }

        val items = attendances.map { it.toString() }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Attendance records")
            .setItems(items, null)
            .setNegativeButton("Close", null)
            .show()
    }

    // ===================== CARGA DE STUDENTS / ROUTES / BUSES =====================

    private fun loadStudents() {
        ApiClient.studentService.getStudents()
            .enqueue(object : Callback<List<StudentDto>> {
                override fun onResponse(
                    call: Call<List<StudentDto>>,
                    response: Response<List<StudentDto>>
                ) {
                    if (response.isSuccessful) {
                        val dtoList = response.body() ?: emptyList()

                        students.clear()
                        students.add(UiStudent(id = "", name = "(Select student)"))
                        students.addAll(
                            dtoList.map { dto ->
                                UiStudent(
                                    id = dto.id ?: "",
                                    name = dto.fullName
                                )
                            }
                        )
                        studentAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@AtendanceActivity,
                            "Error loading students (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<StudentDto>>, t: Throwable) {
                    Toast.makeText(
                        this@AtendanceActivity,
                        "Failed to load students: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

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
                                    label = "${dto.name}"
                                )
                            }
                        )
                        routeAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@AtendanceActivity,
                            "Error loading routes (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<RouteDto>>, t: Throwable) {
                    Toast.makeText(
                        this@AtendanceActivity,
                        "Failed to load routes: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

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
                                    label = dto.plate
                                )
                            }
                        )
                        busAdapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@AtendanceActivity,
                            "Error loading buses (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<BusDto>>, t: Throwable) {
                    Toast.makeText(
                        this@AtendanceActivity,
                        "Failed to load buses: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
