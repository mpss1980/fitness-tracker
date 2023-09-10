package com.example.fitnesstracker

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.fitnesstracker.R.*
import com.example.fitnesstracker.model.Calc

class ImcActivity : AppCompatActivity() {

    private lateinit var editWeight: EditText
    private lateinit var editHeight: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_imc)

        editWeight = findViewById(id.edt_imc_weight)
        editHeight = findViewById(id.edt_imc_height)

        val btnSend: Button = findViewById(id.btn_imc_send)
        btnSend.setOnClickListener {
            if (!validateFields()) {
                Toast.makeText(this, string.fields_message, Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            val imc = calculateImc(
                editWeight.text.toString().toInt(),
                editHeight.text.toString().toInt()
            )

            imcResponse(imc)
        }
    }

    private fun imcResponse(imc: Double) {
        val result = when {
            imc < 15.0 -> string.imc_severely_low_weight
            imc < 16.0 -> string.imc_very_low_weight
            imc < 18.5 -> string.imc_low_weight
            imc < 25 -> string.normal
            imc < 30 -> string.imc_high_weight
            imc < 35 -> string.imc_so_high_weight
            imc < 40 -> string.imc_severely_high_weight
            else -> string.imc_extreme_weight
        }

        AlertDialog.Builder(this)
            .setTitle(getString(string.imc_response, imc))
            .setMessage(result)
            .setPositiveButton(
                android.R.string.ok,
                DialogInterface.OnClickListener() { dialog, _ ->
                    dialog.dismiss()
                })
            .setNegativeButton(
                string.save,
                DialogInterface.OnClickListener() { _, _ ->
                    saveCalc(imc)
                })
            .create()
            .show()

        (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    private fun saveCalc(imc: Double) {
        Thread(Runnable {
            (application as App).db.calcDao().apply {
                insert(Calc(type = "IMC", res = imc))

                runOnUiThread {
                    Intent(this@ImcActivity, ListCalcActivity::class.java).apply {
                        putExtra("type", "IMC")
                        startActivity(this)
                    }
                }
            }
        }).start()
    }

    private fun validateFields(): Boolean {
        val weight = editWeight.text.toString()
        val height = editHeight.text.toString()

        return weight.isNotEmpty() &&
                weight.toInt() > 0 &&
                height.isNotEmpty() &&
                height.toInt() > 0
    }

    private fun calculateImc(weight: Int, height: Int): Double {
        val heightInMeters = height / 100.0
        return weight / (heightInMeters * heightInMeters)
    }
}