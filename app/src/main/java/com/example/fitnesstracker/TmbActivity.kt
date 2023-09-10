package com.example.fitnesstracker

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.fitnesstracker.model.Calc

class TmbActivity : AppCompatActivity() {

    private lateinit var lifestyle: AutoCompleteTextView
    private lateinit var editWeight: EditText
    private lateinit var editHeight: EditText
    private lateinit var editAge: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tmb)

        editWeight = findViewById(R.id.edt_tmb_weight)
        editHeight = findViewById(R.id.edt_tmb_height)
        editAge = findViewById(R.id.edt_tmb_age)
        lifestyle = findViewById(R.id.auto_lifestyle)

        ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.tmb_lifestyle)
        ).also {
            lifestyle.setAdapter(it)
        }

        val btnSend: Button = findViewById(R.id.btn_tmb_send)
        btnSend.setOnClickListener {
            if (!validateFields()) {
                Toast.makeText(this, R.string.fields_message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val tmb = calculateTmb(
                editWeight.text.toString().toInt(),
                editHeight.text.toString().toInt(),
                editAge.text.toString().toInt()
            )

            tmbResponse(tmb)
        }

    }

    private fun calculateTmb(weight: Int, height: Int, age: Int): Double {
        return 66 + (13.7 * weight) + (5 * height) - (6.8 * age)
    }

    private fun tmbResponse(tmb: Double) {
        resources.getStringArray(R.array.tmb_lifestyle).apply {
            val index = indexOf(lifestyle.text.toString())

            if (index < 0) {
                Toast.makeText(this@TmbActivity, R.string.fields_message, Toast.LENGTH_LONG).show()
                return
            }

            val result = tmb * when (index) {
                0 -> 1.2
                1 -> 1.375
                2 -> 1.55
                3 -> 1.725
                else -> 1.9
            }

            AlertDialog.Builder(this@TmbActivity)
                .setTitle(R.string.tmb)
                .setMessage(getString(R.string.tmb_response, result))
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(R.string.save,
                    DialogInterface.OnClickListener() { _, _ ->
                        intent.extras?.getInt("updateId")?.let {
                            (application as App).db.calcDao().apply {
                                return@OnClickListener updateCalc(it, tmb)
                            }
                        }
                        saveCalc(result)
                    })
                .create()
                .show()
        }
    }

    private fun saveCalc(tmb: Double) {
        Thread(Runnable {
            (application as App).db.calcDao().apply {
                insert(Calc(type = "TMB", res = tmb))

                runOnUiThread {
                    finish()
                    openListActivity()
                }
            }
        }).start()
    }

    private fun updateCalc(id: Int, tmb: Double) {
        Thread(Runnable {
            (application as App).db.calcDao().apply {
                update(Calc(id = id, type = "TMB", res = tmb))

                runOnUiThread {
                    finish()
                    openListActivity()
                }
            }
        }).start()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_search) {
            finish()
            openListActivity()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openListActivity() {
        Intent(this, ListCalcActivity::class.java).apply {
            putExtra("type", "TMB")
            startActivity(this)
        }
    }


    private fun validateFields(): Boolean {
        val weight = editWeight.text.toString()
        val height = editHeight.text.toString()
        val age = editAge.text.toString()

        return weight.isNotEmpty() &&
                weight.toInt() > 0 &&
                height.isNotEmpty() &&
                height.toInt() > 0 &&
                age.isNotEmpty() &&
                age.toInt() > 0
    }
}