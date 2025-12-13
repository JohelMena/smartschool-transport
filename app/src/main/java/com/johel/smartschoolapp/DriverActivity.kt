package com.johel.smartschoolapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.google.android.material.appbar.MaterialToolbar
import com.johel.smartschoolapp.api.ApiClient
import com.johel.smartschoolapp.api.DriverDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DriverActivity : AppCompatActivity() {

    // UI
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etLicense: EditText
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView
    private lateinit var btnBackToMain: Button

    // Modelo local (para manejar id + data de forma sencilla)
    private data class UiDriver(
        val id: String,   // viene de la API
        var name: String,
        var phone: String,
        var license: String
    )

    private val drivers = mutableListOf<UiDriver>()          // lista real
    private val displayDrivers = mutableListOf<String>()     // textos para ListView
    private lateinit var adapter: ArrayAdapter<String>

    // Driver seleccionado para update/delete
    private var selectedDriver: UiDriver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver)

        // Toolbar con back
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // Referencias UI
        etName = findViewById(R.id.etDriverName)
        etPhone = findViewById(R.id.etDriverPhone)
        etLicense = findViewById(R.id.etDriverLicense)
        listView = findViewById(R.id.listViewDrivers)
        searchView = findViewById(R.id.searchDriver)
        btnBackToMain = findViewById(R.id.btnBackToMain)

        // Adapter de la lista
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayDrivers)
        listView.adapter = adapter

        // SearchView expandido
        searchView.isIconified = false

        // Botón volver al menú principal
        btnBackToMain.setOnClickListener {
            finish()
        }

        // ========= Botón AGREGAR =========
        findViewById<Button>(R.id.btnAddDriver).setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val license = etLicense.text.toString().trim()

            if (name.length < 3) {
                Toast.makeText(this, "Name must have at least 3 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (phone.isEmpty()) {
                Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (license.isEmpty()) {
                Toast.makeText(this, "Enter license number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dto = DriverDto(
                id = null,
                fullName = name,
                licenseNumber = license,
                phone = phone
            )


            ApiClient.driverService.createDriver(dto)
                .enqueue(object : Callback<DriverDto> {
                    override fun onResponse(
                        call: Call<DriverDto>,
                        response: Response<DriverDto>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@DriverActivity,
                                "Driver created in API",
                                Toast.LENGTH_SHORT
                            ).show()
                            clearFields()
                            refreshDriverList()
                        } else {
                            Toast.makeText(
                                this@DriverActivity,
                                "Error creating driver (${response.code()})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<DriverDto>, t: Throwable) {
                        Toast.makeText(
                            this@DriverActivity,
                            "Failed to connect to API: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }

        // ========= Botón ACTUALIZAR =========
        findViewById<Button>(R.id.btnUpdateDriver).setOnClickListener {
            val current = selectedDriver
            if (current == null) {
                Toast.makeText(this, "Select a driver first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmationDialog("Update") {
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val license = etLicense.text.toString().trim()

                if (name.length < 3) {
                    Toast.makeText(this, "Name must have at least 3 characters", Toast.LENGTH_SHORT).show()
                    return@showConfirmationDialog
                }
                if (phone.isEmpty()) {
                    Toast.makeText(this, "Enter phone number", Toast.LENGTH_SHORT).show()
                    return@showConfirmationDialog
                }
                if (license.isEmpty()) {
                    Toast.makeText(this, "Enter license number", Toast.LENGTH_SHORT).show()
                    return@showConfirmationDialog
                }

                val dto = DriverDto(
                    id = current.id,
                    fullName = name,
                    licenseNumber = license,
                    phone = phone
                )


                ApiClient.driverService.updateDriver(current.id, dto)
                    .enqueue(object : Callback<DriverDto> {
                        override fun onResponse(
                            call: Call<DriverDto>,
                            response: Response<DriverDto>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@DriverActivity,
                                    "Driver updated in API",
                                    Toast.LENGTH_SHORT
                                ).show()
                                refreshDriverList()
                            } else {
                                Toast.makeText(
                                    this@DriverActivity,
                                    "Error updating driver (${response.code()})",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<DriverDto>, t: Throwable) {
                            Toast.makeText(
                                this@DriverActivity,
                                "Failed to connect to API: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }

        // ========= Botón ELIMINAR =========
        findViewById<Button>(R.id.btnDeleteDriver).setOnClickListener {
            val current = selectedDriver
            if (current == null) {
                Toast.makeText(this, "Select a driver first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmationDialog("Delete") {
                ApiClient.driverService.deleteDriver(current.id)
                    .enqueue(object : Callback<DriverDto> {
                        override fun onResponse(
                            call: Call<DriverDto>,
                            response: Response<DriverDto>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(
                                    this@DriverActivity,
                                    "Driver deleted in API",
                                    Toast.LENGTH_SHORT
                                ).show()
                                selectedDriver = null
                                clearFields()
                                refreshDriverList()
                            } else {
                                Toast.makeText(
                                    this@DriverActivity,
                                    "Error deleting driver (${response.code()})",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<DriverDto>, t: Throwable) {
                            Toast.makeText(
                                this@DriverActivity,
                                "Failed to connect to API: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }

        // ========= Click en la lista =========
        listView.setOnItemClickListener { _, _, position, _ ->
            val displayText = adapter.getItem(position) ?: return@setOnItemClickListener
            val driver = findDriverByDisplay(displayText)
            if (driver != null) {
                selectedDriver = driver
                etName.setText(driver.name)
                etPhone.setText(driver.phone)
                etLicense.setText(driver.license)
            }
        }

        // ========= Búsqueda =========
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

        // lista inicial desde la API
        refreshDriverList()
    }

    // ================== Helpers ==================

    private fun refreshDriverList() {
        ApiClient.driverService.getDrivers()
            .enqueue(object : Callback<List<DriverDto>> {
                override fun onResponse(
                    call: Call<List<DriverDto>>,
                    response: Response<List<DriverDto>>
                ) {
                    if (response.isSuccessful) {
                        val dtoList = response.body() ?: emptyList()

                        drivers.clear()
                        drivers.addAll(
                            dtoList.map { dto ->
                                UiDriver(
                                    id = dto.id ?: "",
                                    name = dto.fullName ?: "Unknown",
                                    phone = dto.phone ?: "No phone",
                                    license = dto.licenseNumber ?: "No license"
                                )
                            }
                        )

                        displayDrivers.clear()
                        displayDrivers.addAll(
                            drivers.map { d ->
                                "${d.name} - ${d.phone} - ${d.license}"
                            }
                        )

                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@DriverActivity,
                            "Error loading drivers (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<DriverDto>>, t: Throwable) {
                    Toast.makeText(
                        this@DriverActivity,
                        "Failed to connect: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }


    private fun clearFields() {
        etName.text.clear()
        etPhone.text.clear()
        etLicense.text.clear()
    }

    private fun findDriverByDisplay(display: String): UiDriver? {
        // Formato "Name - Phone - License"
        val parts = display.split(" - ")
        if (parts.size < 3) return null
        val name = parts[0]
        val phone = parts[1]
        val license = parts[2]

        return drivers.find { it.name == name && it.phone == phone && it.license == license }
    }

    private fun showConfirmationDialog(action: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("$action confirmation")
            .setMessage("Are you sure you want to $action this driver?")
            .setPositiveButton("Yes") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
