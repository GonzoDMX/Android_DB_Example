package com.example.db_example

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var database: PersonDatabase
    private lateinit var tableLayout: TableLayout
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var ageInput: EditText
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize database
        database = PersonDatabase.getDatabase(this)

        // Initialize views
        tableLayout = findViewById(R.id.tableLayout)
        firstNameInput = findViewById(R.id.firstNameInput)
        lastNameInput = findViewById(R.id.lastNameInput)
        ageInput = findViewById(R.id.ageInput)
        submitButton = findViewById(R.id.submitButton)

        // Set up submit button click listener
        submitButton.setOnClickListener {
            addPerson()
        }

        // Observe database changes
        lifecycleScope.launch {
            database.personDao().getAllPersons().collect { persons ->
                updateTable(persons)
            }
        }
    }

    private fun addPerson() {
        val firstName = firstNameInput.text.toString()
        val lastName = lastNameInput.text.toString()
        val ageText = ageInput.text.toString()

        if (firstName.isEmpty() || lastName.isEmpty() || ageText.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val age = ageText.toIntOrNull()
        if (age == null || age <= 0) {
            Toast.makeText(this, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val person = Person(
                firstName = firstName,
                lastName = lastName,
                age = age
            )
            database.personDao().insertPerson(person)

            // Clear inputs
            firstNameInput.text.clear()
            lastNameInput.text.clear()
            ageInput.text.clear()

            Toast.makeText(this@MainActivity, "Person added successfully", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTable(persons: List<Person>) {
        // Remove all rows except header
        while (tableLayout.childCount > 1) {
            tableLayout.removeViewAt(1)
        }

        // Add rows for each person
        persons.forEach { person ->
            val row = TableRow(this).apply {
                layoutParams = TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
                )
                val paddingInPx = (8 * resources.displayMetrics.density).toInt()
                setPadding(paddingInPx, paddingInPx, paddingInPx, paddingInPx)
            }

            // Add cells
            listOf(person.id.toString(), person.firstName, person.lastName, person.age.toString()).forEach { text ->
                TextView(this).apply {
                    setText(text)
                    layoutParams = TableRow.LayoutParams(
                        TableRow.LayoutParams.WRAP_CONTENT,
                        TableRow.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(8, 8, 8, 8)
                }.also { row.addView(it) }
            }

            tableLayout.addView(row)
        }
    }
}