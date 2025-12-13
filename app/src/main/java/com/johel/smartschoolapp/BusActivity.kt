package com.johel.smartschoolapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.johel.smartschoolapp.api.ApiClient
import com.johel.smartschoolapp.api.BusDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BusActivity : AppCompatActivity() {

    private lateinit var etPlate: EditText
    private lateinit var etCapacity: EditText

    // lista en memoria para mostrar en el diálogo
    private val buses = mutableListOf<UiBus>()

    private var selectedBus: UiBus? = null

    // clase para la UI
    data class UiBus(
        val id: String,
        val plate: String,
        val capacity: Int
    ) {
        override fun toString(): String = "$plate - Capacity: $capacity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bus)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // UI
        etPlate = findViewById(R.id.etBusPlate)
        etCapacity = findViewById(R.id.etBusCapacity)

        val btnAdd = findViewById<Button>(R.id.btnAddBus)
        val btnUpdate = findViewById<Button>(R.id.btnUpdateBus)
        val btnDelete = findViewById<Button>(R.id.btnDeleteBus)
        val btnViewList = findViewById<Button>(R.id.btnViewBusList)
        val btnBack = findViewById<Button>(R.id.btnBackToMain)

        // Acciones
        btnAdd.setOnClickListener { onAddBus() }
        btnUpdate.setOnClickListener { onUpdateBus() }
        btnDelete.setOnClickListener { onDeleteBus() }
        btnViewList.setOnClickListener { showBusListDialog() }
        btnBack.setOnClickListener { finish() }

        // Cargar lista inicial desde el API
        refreshBusList()
    }

    // ================== ACCIONES ==================

    private fun onAddBus() {
        val plate = etPlate.text.toString().trim()
        val capacityStr = etCapacity.text.toString().trim()

        if (plate.isEmpty()) {
            Toast.makeText(this, "Enter bus plate", Toast.LENGTH_SHORT).show()
            return
        }
        if (capacityStr.isEmpty()) {
            Toast.makeText(this, "Enter bus capacity", Toast.LENGTH_SHORT).show()
            return
        }

        val capacity = capacityStr.toIntOrNull()
        if (capacity == null || capacity <= 0) {
            Toast.makeText(this, "Capacity must be a positive number", Toast.LENGTH_SHORT).show()
            return
        }

        val dto = BusDto(
            id = null,
            plate = plate,
            capacity = capacity
        )

        ApiClient.busService.createBus(dto)
            .enqueue(object : Callback<BusDto> {
                override fun onResponse(call: Call<BusDto>, response: Response<BusDto>) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@BusActivity,
                            "Bus created in API",
                            Toast.LENGTH_SHORT
                        ).show()
                        clearFields()
                        refreshBusList()
                    } else {
                        Toast.makeText(
                            this@BusActivity,
                            "Error creating bus (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<BusDto>, t: Throwable) {
                    Toast.makeText(
                        this@BusActivity,
                        "Failed to connect: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun onUpdateBus() {
        val current = selectedBus
        if (current == null) {
            Toast.makeText(this, "Select a bus from the list first", Toast.LENGTH_SHORT).show()
            return
        }

        showConfirmationDialog("Update") {
            val plate = etPlate.text.toString().trim()
            val capacityStr = etCapacity.text.toString().trim()

            if (plate.isEmpty()) {
                Toast.makeText(this, "Enter bus plate", Toast.LENGTH_SHORT).show()
                return@showConfirmationDialog
            }
            if (capacityStr.isEmpty()) {
                Toast.makeText(this, "Enter bus capacity", Toast.LENGTH_SHORT).show()
                return@showConfirmationDialog
            }

            val capacity = capacityStr.toIntOrNull()
            if (capacity == null || capacity <= 0) {
                Toast.makeText(this, "Capacity must be a positive number", Toast.LENGTH_SHORT)
                    .show()
                return@showConfirmationDialog
            }

            val dto = BusDto(
                id = current.id,
                plate = plate,
                capacity = capacity
            )

            ApiClient.busService.updateBus(current.id, dto)
                .enqueue(object : Callback<BusDto> {
                    override fun onResponse(
                        call: Call<BusDto>,
                        response: Response<BusDto>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@BusActivity,
                                "Bus updated in API",
                                Toast.LENGTH_SHORT
                            ).show()
                            refreshBusList()
                        } else {
                            Toast.makeText(
                                this@BusActivity,
                                "Error updating bus (${response.code()})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<BusDto>, t: Throwable) {
                        Toast.makeText(
                            this@BusActivity,
                            "Failed to connect: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun onDeleteBus() {
        val current = selectedBus
        if (current == null) {
            Toast.makeText(this, "Select a bus from the list first", Toast.LENGTH_SHORT).show()
            return
        }

        showConfirmationDialog("Delete") {
            ApiClient.busService.deleteBus(current.id)
                .enqueue(object : Callback<BusDto> {
                    override fun onResponse(
                        call: Call<BusDto>,
                        response: Response<BusDto>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@BusActivity,
                                "Bus deleted in API",
                                Toast.LENGTH_SHORT
                            ).show()
                            selectedBus = null
                            clearFields()
                            refreshBusList()
                        } else {
                            Toast.makeText(
                                this@BusActivity,
                                "Error deleting bus (${response.code()})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<BusDto>, t: Throwable) {
                        Toast.makeText(
                            this@BusActivity,
                            "Failed to connect: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    // ================== LISTA / DIÁLOGO ==================

    private fun refreshBusList() {
        ApiClient.busService.getBuses()
            .enqueue(object : Callback<List<BusDto>> {
                override fun onResponse(
                    call: Call<List<BusDto>>,
                    response: Response<List<BusDto>>
                ) {
                    if (response.isSuccessful) {
                        val dtoList = response.body() ?: emptyList()

                        buses.clear()
                        buses.addAll(
                            dtoList.map { dto ->
                                UiBus(
                                    id = dto.id ?: "",
                                    plate = dto.plate,
                                    capacity = dto.capacity
                                )
                            }
                        )
                    } else {
                        Toast.makeText(
                            this@BusActivity,
                            "Error loading buses (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<BusDto>>, t: Throwable) {
                    Toast.makeText(
                        this@BusActivity,
                        "Failed to connect: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun showBusListDialog() {
        if (buses.isEmpty()) {
            Toast.makeText(this, "No buses found", Toast.LENGTH_SHORT).show()
            return
        }

        val items = buses.map { it.toString() }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Buses")
            .setItems(items) { _, which ->
                val bus = buses[which]
                selectedBus = bus
                etPlate.setText(bus.plate)
                etCapacity.setText(bus.capacity.toString())
            }
            .setNegativeButton("Close", null)
            .show()
    }

    // ================== HELPERS ==================

    private fun clearFields() {
        etPlate.text.clear()
        etCapacity.text.clear()
    }

    private fun showConfirmationDialog(action: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("$action confirmation")
            .setMessage("Are you sure you want to $action this bus?")
            .setPositiveButton("Yes") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
