package com.johel.smartschoolapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.johel.smartschoolapp.api.ApiClient
import com.johel.smartschoolapp.api.GuardianDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.widget.SearchView
class GuardianActivity : AppCompatActivity() {

    // UI
    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var listView: ListView
    private lateinit var searchView: SearchView

    // Datos en memoria para la pantalla
    private val guardians = mutableListOf<UiGuardian>()          // lista real
    private val displayGuardians = mutableListOf<String>()       // textos para el ListView
    private lateinit var adapter: ArrayAdapter<String>

    private var selectedGuardian: UiGuardian? = null

    // Clase usada solo en la UI
    data class UiGuardian(
        val id: String,
        val fullName: String,
        val phone: String,
        val address: String
    ) {
        override fun toString(): String = "$fullName - $phone"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardian)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBarGuardian)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener { finish() }

        // Referencias UI
        etName = findViewById(R.id.etGuardianName)
        etPhone = findViewById(R.id.etGuardianPhone)
        etAddress = findViewById(R.id.etGuardianAddress)
        listView = findViewById(R.id.listViewGuardians)
        searchView = findViewById(R.id.searchGuardian)

        val btnAdd = findViewById<Button>(R.id.btnAddGuardian)
        val btnUpdate = findViewById<Button>(R.id.btnUpdateGuardian)
        val btnDelete = findViewById<Button>(R.id.btnDeleteGuardian)

        // Adapter de la lista
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayGuardians)
        listView.adapter = adapter

        // Acciones
        btnAdd.setOnClickListener { onAddGuardian() }
        btnUpdate.setOnClickListener { onUpdateGuardian() }
        btnDelete.setOnClickListener { onDeleteGuardian() }

        // Click en un elemento de la lista
        listView.setOnItemClickListener { _, _, position, _ ->
            val text = adapter.getItem(position) ?: return@setOnItemClickListener
            val guardian = guardians.find { it.toString() == text }
            if (guardian != null) {
                selectedGuardian = guardian
                etName.setText(guardian.fullName)
                etPhone.setText(guardian.phone)
                etAddress.setText(guardian.address)
            }
        }

        // Búsqueda
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                adapter.filter.filter(query)
                return true   // indicamos que manejamos el evento
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }
        })

        // Cargar lista inicial desde el API
        refreshGuardianList()
    }

    // ================== ACCIONES ==================

    private fun onAddGuardian() {
        val name = etName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val address = etAddress.text.toString().trim()

        if (name.length < 3) {
            Toast.makeText(this, "Enter guardian name (min 3 chars)", Toast.LENGTH_SHORT).show()
            return
        }
        if (phone.isEmpty()) {
            Toast.makeText(this, "Enter phone", Toast.LENGTH_SHORT).show()
            return
        }

        val dto = GuardianDto(
            id = null,
            fullName = name,
            phone = phone,
            address = if (address.isBlank()) null else address
        )

        ApiClient.guardianService.createGuardian(dto)
            .enqueue(object : Callback<GuardianDto> {
                override fun onResponse(
                    call: Call<GuardianDto>,
                    response: Response<GuardianDto>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(
                            this@GuardianActivity,
                            "Guardian created in API",
                            Toast.LENGTH_SHORT
                        ).show()
                        clearFields()
                        refreshGuardianList()
                    } else {
                        Toast.makeText(
                            this@GuardianActivity,
                            "Error creating guardian (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<GuardianDto>, t: Throwable) {
                    Toast.makeText(
                        this@GuardianActivity,
                        "Failed to connect: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun onUpdateGuardian() {
        val current = selectedGuardian
        if (current == null) {
            Toast.makeText(this, "Select a guardian from the list first", Toast.LENGTH_SHORT).show()
            return
        }

        showConfirmationDialog("Update") {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val address = etAddress.text.toString().trim()

            if (name.length < 3) {
                Toast.makeText(this, "Enter guardian name (min 3 chars)", Toast.LENGTH_SHORT).show()
                return@showConfirmationDialog
            }
            if (phone.isEmpty()) {
                Toast.makeText(this, "Enter phone", Toast.LENGTH_SHORT).show()
                return@showConfirmationDialog
            }

            val dto = GuardianDto(
                id = current.id,
                fullName = name,
                phone = phone,
                address = if (address.isBlank()) null else address
            )

            ApiClient.guardianService.updateGuardian(current.id, dto)
                .enqueue(object : Callback<GuardianDto> {
                    override fun onResponse(
                        call: Call<GuardianDto>,
                        response: Response<GuardianDto>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@GuardianActivity,
                                "Guardian updated in API",
                                Toast.LENGTH_SHORT
                            ).show()
                            refreshGuardianList()
                        } else {
                            Toast.makeText(
                                this@GuardianActivity,
                                "Error updating guardian (${response.code()})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<GuardianDto>, t: Throwable) {
                        Toast.makeText(
                            this@GuardianActivity,
                            "Failed to connect: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    private fun onDeleteGuardian() {
        val current = selectedGuardian
        if (current == null) {
            Toast.makeText(this, "Select a guardian from the list first", Toast.LENGTH_SHORT).show()
            return
        }

        showConfirmationDialog("Delete") {
            ApiClient.guardianService.deleteGuardian(current.id)
                .enqueue(object : Callback<GuardianDto> {
                    override fun onResponse(
                        call: Call<GuardianDto>,
                        response: Response<GuardianDto>
                    ) {
                        if (response.isSuccessful) {
                            Toast.makeText(
                                this@GuardianActivity,
                                "Guardian deleted in API",
                                Toast.LENGTH_SHORT
                            ).show()
                            selectedGuardian = null
                            clearFields()
                            refreshGuardianList()
                        } else {
                            Toast.makeText(
                                this@GuardianActivity,
                                "Error deleting guardian (${response.code()})",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<GuardianDto>, t: Throwable) {
                        Toast.makeText(
                            this@GuardianActivity,
                            "Failed to connect: ${t.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
        }
    }

    // ================== LISTA / API ==================

    private fun refreshGuardianList() {
        ApiClient.guardianService.getGuardians()
            .enqueue(object : Callback<List<GuardianDto>> {
                override fun onResponse(
                    call: Call<List<GuardianDto>>,
                    response: Response<List<GuardianDto>>
                ) {
                    if (response.isSuccessful) {
                        val dtoList = response.body() ?: emptyList()

                        guardians.clear()
                        guardians.addAll(
                            dtoList.map { dto ->
                                UiGuardian(
                                    id = dto.id ?: "",
                                    fullName = dto.fullName,
                                    phone = dto.phone,
                                    address = dto.address ?: ""
                                )
                            }
                        )

                        displayGuardians.clear()
                        displayGuardians.addAll(guardians.map { it.toString() })
                        adapter.notifyDataSetChanged()
                    } else {
                        Toast.makeText(
                            this@GuardianActivity,
                            "Error loading guardians (${response.code()})",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<GuardianDto>>, t: Throwable) {
                    Toast.makeText(
                        this@GuardianActivity,
                        "Failed to connect: ${t.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    // ================== HELPERS ==================

    private fun clearFields() {
        etName.text.clear()
        etPhone.text.clear()
        etAddress.text.clear()
    }

    private fun showConfirmationDialog(action: String, onConfirm: () -> Unit) {
        AlertDialog.Builder(this)
            .setTitle("$action confirmation")
            .setMessage("Are you sure you want to $action this guardian?")
            .setPositiveButton("Yes") { _, _ -> onConfirm() }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
