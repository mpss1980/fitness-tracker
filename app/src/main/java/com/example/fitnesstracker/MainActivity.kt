package com.example.fitnesstracker

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fitnesstracker.R.*

class MainActivity : AppCompatActivity() {

    private lateinit var rvMain: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.activity_main)

        val mainItems = populateButtons()

        rvMain = findViewById(id.rv_main)
        rvMain.adapter = MainAdapter(mainItems) { id ->
            when (id) {
                1 -> {
                    startActivity(Intent(this@MainActivity, ImcActivity::class.java))
                }

                2 -> {
                    startActivity(Intent(this@MainActivity, TmbActivity::class.java))
                }
            }
        }
        rvMain.layoutManager = GridLayoutManager(this, 2)

    }

    private fun populateButtons(): MutableList<MainItem> {
        return mutableListOf<MainItem>().apply {
            add(
                MainItem(
                    id = 1,
                    drawableId = drawable.baseline_wb_sunny_24,
                    textStringId = string.imc,
                    color = Color.GREEN
                )
            )

            add(
                MainItem(
                    id = 2,
                    drawableId = drawable.baseline_visibility_24,
                    textStringId = string.tmb,
                    color = Color.YELLOW
                )
            )
        }
    }

    private inner class MainAdapter(
        private val mainItems: List<MainItem>,
        private val onItemClickListener: (Int) -> Unit
    ) : RecyclerView.Adapter<MainAdapter.MainViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
            return MainViewHolder(layoutInflater.inflate(layout.main_item, parent, false))
        }

        override fun getItemCount(): Int {
            return mainItems.size
        }

        override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
            val mainItem = mainItems[position]
            holder.bind(mainItem)
        }

        private inner class MainViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(menuItem: MainItem) {
                itemView.findViewById<ImageView?>(id.item_img_icon).apply {
                    setImageResource(menuItem.drawableId)
                }

                itemView.findViewById<TextView?>(id.item_txt_name).apply {
                    setText(menuItem.textStringId)
                }

                itemView.findViewById<LinearLayout>(id.icon_container_imc).apply {
                    setBackgroundColor(menuItem.color)
                    setOnClickListener {
                        onItemClickListener.invoke(menuItem.id)
                    }
                }

            }
        }
    }
}