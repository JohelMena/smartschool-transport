package com.johel.smartschoolapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.johel.smartschoolapp.data.GuardianStorage
import com.johel.smartschoolapp.domain.Guardian
import com.johel.smartschoolapp.util.IdGenerator
import com.johel.smartschoolapp.util.Validators

class GuardianActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var listView: ListView
    private lateinit var adapter: ArrayAdapter<String>

    private val displayGuardians = mutableListOf<String>()
    private var selectedGuardian: Guardian? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardian)

        // Toolbar
        val toolbar = findViewById<MaterialToolbar>(R.id.topAppBarGuardian)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // UI
        etName = findViewById(R.id.etGuardianName)
        etPhone = findViewById(R.id.etGuardianPhone)
        etAddress = findViewById(R.id.etGuardianAddress)
        listView = findViewById(R.id.listViewGuardians)

        // List adapter
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, displayGuardians)
        listView.adapter = adapter

        // ADD
        findViewById<Button>(R.id.btnAddGuardian).setOnClickListener {
            val name = etName.text.toString().trim()
            val phone = etPhone.text.toString().trim()
            val address = etAddress.text.toString().trim()

            if (!Validators.isValidName(name)) {
                Toast.makeText(
                    this,
                    "Invalid name (at least 3 characters)",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (phone.isEmpty()) {
                Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val guardian = Guardian(
                id = IdGenerator.newID(),
                fullName = name,
                phone = phone,
                address = address
            )

            GuardianStorage.add(guardian)
            clearFields()
            refreshGuardianList()
            Toast.makeText(this, "Guardian added", Toast.LENGTH_SHORT).show()
        }

        // UPDATE
        findViewById<Button>(R.id.btnUpdateGuardian).setOnClickListener {
            val current = selectedGuardian
            if (current == null) {
                Toast.makeText(this, "Select a guardian first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmationDialog("Update") {
                val name = etName.text.toString().trim()
                val phone = etPhone.text.toString().trim()
                val address = etAddress.text.toString().trim()

                if (!Validators.isValidName(name)) {
                    Toast.makeText(
                        this,
                        "Invalid name (at least 3 characters)",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@showConfirmationDialog
                }

                if (phone.isEmpty()) {
                    Toast.makeText(this, "Please enter phone number", Toast.LENGTH_SHORT).show()
                    return@showConfirmationDialog
                }

                val updatedGuardian = Guardian(
                    id = current.id,
                    fullName = name,
                    phone = phone,
                    address = address
                )

                GuardianStorage.update(updatedGuardian)
                selectedGuardian = updatedGuardian
                refreshGuardianList()
                Toast.makeText(this, "Guardian updated", Toast.LENGTH_SHORT).show()
            }
        }

        // DELETE
        findViewById<Button>(R.id.btnDeleteGuardian).setOnClickListener {
            val current = selectedGuardian
            if (current == null) {
                Toast.makeText(this, "Select a guardian first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showConfirmationDialog("Delete") {
                GuardianStorage.removeById(current.id)
                selectedGuardian = null
                clearFields()
                refreshGuardianList()
                Toast.makeText(this, "Guardian deleted", Toast.LENGTH_SHORT).show()
            }
        }

        // LIST item click
        listView.setOnItemClickListener { _, _, position, _ ->
            val displayText = adapter.getItem(position) ?: return@setOnItemClickListener
            val guardian = findGuardianByDisplay(displayText)
            if (guardian != null) {
                selectedGuardian = guardian
                etName.setText(guardian.fullName)
                etPhone.setText(guardian.phone)
                etAddress.setText(guardian.address)
            }
        }

        // SEARCH
        val searchView = findViewById<SearchView>(R.id.searchGuardian)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return false
            }
        })

        // Initial load
        refreshGuardianList()
    }

    private fun refreshGuardianList() {
        val guardians = GuardianStorage.getAll()
        displayGuardians.clear()
        displayGuardians.addAll(
            guardians.map { g ->
                // Lo que se ve en la lista
                "${g.fullName} - ${g.phone}"
            }
        )
        adapter.notifyDataSetChanged()
    }

    private fun clearFields() {
        etName.text.clear()
        etPhone.text.clear()
        etAddress.text.clear()
    }

    private fun findGuardianByDisplay(display: String): Guardian? {
        val parts = display.split(" - ")
        if (parts.size < 2) return null
        val name = parts[0]
        val phone = parts[1]
        return GuardianStorage.getAll().find {
            it.fullName == name && it.phone == phone
        }
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
