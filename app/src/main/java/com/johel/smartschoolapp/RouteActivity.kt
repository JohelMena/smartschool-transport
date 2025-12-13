package com.johel.smartschoolapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.johel.smartschoolapp.api.ApiClient
import com.johel.smartschoolapp.api.RouteDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RouteActivity : AppCompatActivity() {

    // --- UI ---
    private lateinit var etName: EditText
    private lateinit var etStart: EditText
    private lateinit var etEnd: EditText

    // --- Datos en memoria para el diálogo ---
    private val routes = mutableListOf<UiRoute>()
    private var selectedRoute: UiRoute? = null

    data class UiRoute(
        val id: String,
        val name: String,
        val startPoint: String,
        val endPoint: String
    ) {
        override fun toString(): String = "$name: $startPoint -> $endPoint"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // UI
        etName = findViewById(R.id.etRouteName)
        etStart = findViewById(R.id.etStartPoint)
        etEnd = findViewById(R.id.etEndPoint)

        val btnAdd = findViewById<Button>(R.id.btnAddRoute)
        val btnUpdate = findViewById<Button>(R.id.btnUpdateRoute)
        val btnDelete = findViewById<Button>(R.id.btnDeleteRoute)
        val btnViewList = findViewById<Button>(R.id.btnViewRouteList)
        val btnBack = findViewById<Button>(R.id.btnBackToMain)

        btnAdd.setOnClickListener { onAddRoute() }
        btnUpdate.setOnClickListener { onUpdateRoute() }
        btnDelete.setOnClickListener { onDeleteRoute() }
        btnViewList.setOnClickListener { showRouteListDialog() }
        btnBack.setOnClickListener { finish() }

        // Cargar rutas iniciales desde el API
        refreshRouteList()
    }

    // ---------------------- ACCIONES ----------------------

    private fun onAddRoute() {
        val name = etName.text.toString().trim()
        val start = etStart.text.toString().trim()
        val end = etEnd.text.toString().trim()

        if (name.isEmpty()) {
            Toast.makeText(this, "Enter route name", Toast.LENGTH_SHORT).show()
            return
        }
        if (start.isEmpty()) {
            Toast.makeText(this, "Enter start point", Toast.LENGTH_SHORT).show()
            return
        }
        if (end.isEmpty()) {
            Toast.makeText(this, "Enter end point", Toast.LENGTH_SHORT).show()
            return
        }

        val dto = RouteDto(
            id = null,
            name = name,
            driverId = start,
            busId = end
        )


        ApiClient.routeService.createRoute(dto)
            .enqueue(object : Callback<RouteDto> {
                override fun onResponse(
                    call: Call<RouteDto>,
                    response: Response<RouteDto>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@RouteActivity,
                            "Route created in API",
                            Toast.LENGTH_SHORT
                        ).show()
                        clearFields()
                        refreshRouteList()
                    } else {
                        Toast.makeText(
                            this@RouteActivity,
                            "Error creating route (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<RouteDto>, t: Throwable) {
                    Toast.makeText(
                        this@RouteActivity,
                        "Failed to connect: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun onUpdateRoute() {
        val current = selectedRoute
        if (current == null) {
            Toast.makeText(this, "Select a route from the list first", Toast.LENGTH_SHORT).show()
            return
        }

        showConfirmationDialog("Update") {
            val name = etName.text.toString().trim()
            val start = etStart.text.toString().trim()
            val end = etEnd.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Enter route name", Toast.LENGTH_SHORT).show()
                return@showConfirmationDialog
            }
            if (start.isEmpty()) {
                Toast.makeText(this, "Enter start point", Toast.LENGTH_SHORT).show()
                return@showConfirmationDialog
            }
            if (end.isEmpty()) {
                Toast.makeText(this, "Enter end point", Toast.LENGTH_SHORT).show()
                return@showConfirmationDialog
            }

            val dto = RouteDto(
                id = current.id,
                name = name,
                driverId = start,
                busId = end
            )


            ApiClient.routeService.updateRoute(current.id, dto)
                .enqueue(object : Callback<RouteDto> {
                    override fun onResponse(
                        call: Call<RouteDto>,
                        response: Response<RouteDto>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@RouteActivity,
                                "Route updated in API",
                                Toast.LENGTH_SHORT
                            ).show()
                            refreshRouteList()
                        } else {
                            Toast.makeText(
                                this@RouteActivity,
                                "Error updating route (${response.code()})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<RouteDto>, t: Throwable) {
                        Toast.makeText(
                            this@RouteActivity,
                            "Failed to connect: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun onDeleteRoute() {
        val current = selectedRoute
        if (current == null) {
            Toast.makeText(this, "Select a route from the list first", Toast.LENGTH_SHORT).show()
            return
        }

        showConfirmationDialog("Delete") {
            ApiClient.routeService.deleteRoute(current.id)
                .enqueue(object : Callback<RouteDto> {
                    override fun onResponse(
                        call: Call<RouteDto>,
                        response: Response<RouteDto>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@RouteActivity,
                                "Route deleted in API",
                                Toast.LENGTH_SHORT
                            ).show()
                            selectedRoute = null
                            clearFields()
                            refreshRouteList()
                        } else {
                            Toast.makeText(
                                this@RouteActivity,
                                "Error deleting route (${response.code()})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<RouteDto>, t: Throwable) {
                        Toast.makeText(
                            this@RouteActivity,
                            "Failed to connect: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    // ---------------------- LISTA / DIÁLOGO ----------------------

    private fun refreshRouteList() {
        ApiClient.routeService.getRoutes()
            .enqueue(object : Callback<List<RouteDto>> {
                override fun onResponse(
                    call: Call<List<RouteDto>>,
                    response: Response<List<RouteDto>>
                ) {
                    if (response.isSuccessful) {
                        val dtoList = response.body() ?: emptyList()

                        routes.clear()
                        routes.addAll(
                            dtoList.map { dto ->
                                UiRoute(
                                    id = dto.id ?: "",
                                    name = dto.name,
                                    startPoint = dto.driverId ?: "(no start)",
                                    endPoint  = dto.busId ?: "(no end)"
                                )
                            }
                        )

                    } else {
                        Toast.makeText(
                            this@RouteActivity,
                            "Error loading routes (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<RouteDto>>, t: Throwable) {
                    Toast.makeText(
                        this@RouteActivity,
                        "Failed to connect: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showRouteListDialog() {
        if (routes.isEmpty()) {
            Toast.makeText(this, "No routes found", Toast.LENGTH_SHORT).show()
            return
        }

        val items = routes.map { it.toString() }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Routes")
            .setItems(items) { _, which ->
                val route = routes[which]
                selectedRoute = route
                etName.setText(route.name)
                etStart.setText(route.startPoint)
                etEnd.setText(route.endPoint)
            }
            .setNegativeButton("Close", null)
            .show()
    }

    // ---------------------- HELPERS ----------------------

    private fun clearFields() {
        etName.text.clear()
        etStart.text.clear()
        etEnd.text.clear()
    }

    private fun showConfirmationDialog(action: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("$action confirmation")
            .setMessage("Are you sure you want to $action this route?")
            .setPositiveButton("Yes") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
