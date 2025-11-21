package com.johel.smartschoolapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.johel.smartschoolapp.data.BusStorage
import com.johel.smartschoolapp.data.DriverStorage
import com.johel.smartschoolapp.data.RouteStorage
import com.johel.smartschoolapp.domain.Bus
import com.johel.smartschoolapp.domain.Driver
import com.johel.smartschoolapp.domain.Route
import com.johel.smartschoolapp.util.IdGenerator

class RouteActivity : AppCompatActivity() {

    private lateinit var etRouteName: EditText
    private lateinit var spDriver: Spinner
    private lateinit var spBus: Spinner
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    // Display list
    private val displayRoutes = mutableListOf<String>()

    // Selected route
    private var selectedRoute: Route? = null

    // Data lists
    private lateinit var drivers: List<Driver>
    private lateinit var buses: List<Bus>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBarRoute)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // UI
        etRouteName = findViewById(R.id.etRouteName)
        spDriver = findViewById(R.id.spDriver)
        spBus = findViewById(R.id.spBus)
        listView = findViewById(R.id.listViewRoutes)

        // Load drivers
        drivers = DriverStorage.getAll()
        val driverNames = drivers.map { it.fullName }
        val driverAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            driverNames
        )
        driverAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDriver.adapter = driverAdapter

        // Load buses
        buses = BusStorage.getAll()
        val busNames = buses.map { it.plate }
        val busAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            busNames
        )
        busAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spBus.adapter = busAdapter

        // List adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayRoutes)
        listView.adapter = adapter

        // ADD
        findViewById<Button>(R.id.btnAddRoute).setOnClickListener {
            val name = etRouteName.text.toString().trim()

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter route name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (drivers.isEmpty()) {
                Toast.makeText(this, "No drivers available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (buses.isEmpty()) {
                Toast.makeText(this, "No buses available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val driver = drivers.getOrNull(spDriver.selectedItemPosition)
            val bus = buses.getOrNull(spBus.selectedItemPosition)

            val newRoute = Route(
                id = IdGenerator.newID(),
                name = name,
                driverId = driver?.id ?: "",
                busId = bus?.id ?: ""
            )

            RouteStorage.controller.add(newRoute)
            clearFields()
            refreshRouteList()
            Toast.makeText(this, "Route added", Toast.LENGTH_SHORT).show()
        }

        // UPDATE
        findViewById<Button>(R.id.btnUpdateRoute).setOnClickListener {
            val current = selectedRoute
            if (current == null) {
                Toast.makeText(this, "Select a route first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmationDialog("Update") {
                val name = etRouteName.text.toString().trim()

                if (name.isEmpty()) {
                    Toast.makeText(this, "Please enter route name", Toast.LENGTH_SHORT).show()
                    return@showConfirmationDialog
                }

                val driver = drivers.getOrNull(spDriver.selectedItemPosition)
                val bus = buses.getOrNull(spBus.selectedItemPosition)

                val updatedRoute = Route(
                    id = current.id,
                    name = name,
                    driverId = driver?.id ?: "",
                    busId = bus?.id ?: ""
                )

                RouteStorage.controller.update(updatedRoute)
                selectedRoute = updatedRoute
                refreshRouteList()
                Toast.makeText(this, "Route updated", Toast.LENGTH_SHORT).show()
            }
        }

        // DELETE
        findViewById<Button>(R.id.btnDeleteRoute).setOnClickListener {
            val current = selectedRoute
            if (current == null) {
                Toast.makeText(this, "Select a route first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmationDialog("Delete") {
                RouteStorage.controller.removeById(current.id)
                selectedRoute = null
                clearFields()
                refreshRouteList()
                Toast.makeText(this, "Route deleted", Toast.LENGTH_SHORT).show()
            }
        }

        // LIST item click
        listView.setOnItemClickListener { _, _, position, _ ->
            val displayText = adapter.getItem(position) ?: return@setOnItemClickListener
            val route = findRouteByDisplay(displayText)
            if (route != null) {
                selectedRoute = route
                etRouteName.setText(route.name)

                // Set driver spinner
                val driverIndex = drivers.indexOfFirst { it.id == route.driverId }
                if (driverIndex >= 0) spDriver.setSelection(driverIndex) else spDriver.setSelection(0)

                // Set bus spinner
                val busIndex = buses.indexOfFirst { it.id == route.busId }
                if (busIndex >= 0) spBus.setSelection(busIndex) else spBus.setSelection(0)
            }
        }

        // SEARCH
        val searchView = findViewById<SearchView>(R.id.searchRoute)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        // Load initial list
        refreshRouteList()
    }

    private fun refreshRouteList() {
        val routes = RouteStorage.controller.getAll()
        displayRoutes.clear()
        displayRoutes.addAll(
            routes.map { route ->
                val driverName = DriverStorage.getAll().find { it.id == route.driverId }?.fullName ?: "No driver"
                val busPlate = BusStorage.getAll().find { it.id == route.busId }?.plate ?: "No bus"
                "${route.name} - Driver: $driverName - Bus: $busPlate"
            }
        )
        adapter.notifyDataSetChanged()
    }

    private fun clearFields() {
        etRouteName.text.clear()
        if (::drivers.isInitialized && drivers.isNotEmpty()) spDriver.setSelection(0)
        if (::buses.isInitialized && buses.isNotEmpty()) spBus.setSelection(0)
    }

    private fun findRouteByDisplay(display: String): Route? {
        val name = display.substringBefore(" - ").trim()
        return RouteStorage.controller.getAll().find { it.name == name }
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
