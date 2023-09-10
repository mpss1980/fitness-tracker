package com.example.fitnesstracker

import com.example.fitnesstracker.model.Calc

interface OnListClickListener {
    fun onClick(id: Int, type: String)
    fun onLongClick(position: Int, calc: Calc)
}