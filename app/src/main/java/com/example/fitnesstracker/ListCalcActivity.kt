package com.example.fitnesstracker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker.model.Calc
import java.text.SimpleDateFormat
import java.util.Locale

class ListCalcActivity : AppCompatActivity(), OnListClickListener {

    private lateinit var adapter: ListCalcAdapter
    private lateinit var calcList: MutableList<Calc>
    private lateinit var rvListCalc: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_calc)

        calcList = mutableListOf<Calc>()
        adapter = ListCalcAdapter(calcList, this)

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
        private val listener: OnListClickListener
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
                (itemView as TextView).apply {
                    val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("pt", "BR"))
                        .format(item.createdDate)
                    text = getString(R.string.list_response, item.res, date)
                    setOnClickListener {
                        listener.onClick(item.id, item.type)
                    }
                    setOnLongClickListener {
                        listener.onLongClick(adapterPosition, item)
                        true
                    }
                }
            }
        }
    }

    override fun onClick(id: Int, type: String) {
        when (type) {
            "IMC" -> {
                Intent(this, ImcActivity::class.java).apply {
                    putExtra("updateId", id)
                    startActivity(this)
                }
            }
            "TMB" -> {
                Intent(this, TmbActivity::class.java).apply {
                    putExtra("updateId", id)
                    startActivity(this)
                }
            }
        }
        finish()
    }

    override fun onLongClick(position: Int, calc: Calc) {
        Log.i("TAG", "onLongClick: $position")
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.delete_message))
            .setNegativeButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                Thread(Runnable {
                    val response = (application as App).db.calcDao().delete(calc)
                    if (response > 0) {
                        runOnUiThread {
                            calcList.removeAt(position)
                            adapter.notifyItemRemoved(position)
                        }
                    }
                }).start()
            }.show()
    }
}