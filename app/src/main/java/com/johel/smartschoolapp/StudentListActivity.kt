package com.johel.smartschoolapp

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.android.material.appbar.MaterialToolbar
import com.johel.smartschoolapp.api.ApiClient
import com.johel.smartschoolapp.api.BusDto
import com.johel.smartschoolapp.api.GuardianDto
import com.johel.smartschoolapp.api.RouteDto
import com.johel.smartschoolapp.api.StudentDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentListActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var searchView: SearchView

    // texto que se muestra en el ListView
    private val displayItems = mutableListOf<String>()
    private lateinit var adapter: ArrayAdapter<String>

    // diccionarios para resolver nombres
    private val guardianMap = mutableMapOf<String, String>() // guardianId -> fullName
    private val routeMap = mutableMapOf<String, String>()    // routeId   -> name
    private val busMap = mutableMapOf<String, String>()      // busId     -> "PLACA (Cap: X)"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_list)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBarStudentList)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // UI
        listView = findViewById(R.id.listViewStudentList)
        searchView = findViewById(R.id.searchStudentList)

        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayItems)
        listView.adapter = adapter

        // Búsqueda
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

        // Cargar todo
        loadAllData()
    }

    // ======================================================
    // Carga secuencial: guardians -> routes -> buses -> students
    // ======================================================

    private fun loadAllData() {
        loadGuardians()
    }

    private fun loadGuardians() {
        ApiClient.guardianService.getGuardians()
            .enqueue(object : Callback<List<GuardianDto>> {
                override fun onResponse(
                    call: Call<List<GuardianDto>>,
                    response: Response<List<GuardianDto>>
                ) {
                    if (response.isSuccessful) {
                        val list = response.body() ?: emptyList()
                        guardianMap.clear()
                        for (g in list) {
                            val id = g.id ?: continue
                            guardianMap[id] = g.fullName
                        }
                        loadRoutes()
                    } else {
                        Toast.makeText(
                            this@StudentListActivity,
                            "Error loading guardians (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadRoutes() // seguimos igual para no bloquear
                    }
                }

                override fun onFailure(call: Call<List<GuardianDto>>, t: Throwable) {
                    Toast.makeText(
                        this@StudentListActivity,
                        "Failed to load guardians: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadRoutes()
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
                        val list = response.body() ?: emptyList()
                        routeMap.clear()
                        for (r in list) {
                            val id = r.id ?: continue
                            // Si tienes startPoint/endPoint y quieres mostrarlos:
                            // val label = "${r.name}: ${r.startPoint ?: ""} -> ${r.endPoint ?: ""}"
                            val label = r.name
                            routeMap[id] = label
                        }
                        loadBuses()
                    } else {
                        Toast.makeText(
                            this@StudentListActivity,
                            "Error loading routes (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadBuses()
                    }
                }

                override fun onFailure(call: Call<List<RouteDto>>, t: Throwable) {
                    Toast.makeText(
                        this@StudentListActivity,
                        "Failed to load routes: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadBuses()
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
                        val list = response.body() ?: emptyList()
                        busMap.clear()
                        for (b in list) {
                            val id = b.id ?: continue
                            val label = "${b.plate} (Cap: ${b.capacity})"
                            busMap[id] = label
                        }
                        loadStudents()
                    } else {
                        Toast.makeText(
                            this@StudentListActivity,
                            "Error loading buses (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                        loadStudents()
                    }
                }

                override fun onFailure(call: Call<List<BusDto>>, t: Throwable) {
                    Toast.makeText(
                        this@StudentListActivity,
                        "Failed to load buses: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    loadStudents()
                }
            })
    }

    private fun loadStudents() {
        ApiClient.studentService.getStudents()
            .enqueue(object : Callback<List<StudentDto>> {
                override fun onResponse(
                    call: Call<List<StudentDto>>,
                    response: Response<List<StudentDto>>
                ) {
                    if (response.isSuccessful) {
                        val list = response.body() ?: emptyList()

                        displayItems.clear()

                        for (s in list) {
                            val guardianName = s.guardianId?.let { guardianMap[it] } ?: "(No guardian)"
                            val routeName = s.routeId?.let { routeMap[it] } ?: "(No route)"
                            val busLabel = s.busId?.let { busMap[it] } ?: "(No bus)"

                            val detail = """
                                Name: ${s.fullName}
                                Grade: ${s.grade}
                                Guardian: $guardianName
                                Route: $routeName
                                Bus: $busLabel
                            """.trimIndent()

                            displayItems.add(detail)
                        }

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
                        "Failed to load students: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}
