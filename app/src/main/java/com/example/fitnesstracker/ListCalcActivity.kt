package com.example.fitnesstracker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker.model.Calc
import java.text.SimpleDateFormat
import java.util.Locale

class ListCalcActivity : AppCompatActivity() {

    private lateinit var rvListCalc: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_calc)

        val calcList = mutableListOf<Calc>()
        val adapter = ListCalcAdapter(calcList)
        rvListCalc = findViewById(R.id.rv_list)
        rvListCalc.layoutManager = LinearLayoutManager(this)
        rvListCalc.adapter = adapter

        val type =
            intent?.extras?.getString("type") ?: throw IllegalAccessException("Type not found")

        Thread(Runnable {
            (application as App).db.calcDao().apply {
                val response = getByType(type)
                runOnUiThread {
                    calcList.clear()
                    calcList.addAll(response)
                    adapter.notifyDataSetChanged()
                }
            }
        }).start()
    }

    private inner class ListCalcAdapter(
        private val listCalc: List<Calc>,
    ) : RecyclerView.Adapter<ListCalcAdapter.ListCalcViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListCalcViewHolder {
            return ListCalcViewHolder(
                layoutInflater.inflate(
                    android.R.layout.simple_list_item_1,
                    parent,
                    false
                )
            )
        }

        override fun getItemCount(): Int {
            return listCalc.size
        }

        override fun onBindViewHolder(holder: ListCalcViewHolder, position: Int) {
            val mainItem = listCalc[position]
            holder.bind(mainItem)
        }

        private inner class ListCalcViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(item: Calc) {
                Log.i("ListCalcActivity", "Item: $item")
                (itemView as TextView).apply {
                    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
                        .format(item.createdDate)
                    text = getString(R.string.list_response, item.res, date)
                }
            }
        }
    }
}