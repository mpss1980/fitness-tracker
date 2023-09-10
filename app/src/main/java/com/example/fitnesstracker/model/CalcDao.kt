package com.example.fitnesstracker.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CalcDao {
    @Insert
    fun insert(calc: Calc)

    @Query("SELECT * FROM calc WHERE type = :type")
    fun getByType(type: String) : List<Calc>

    @Delete
    fun delete(calc: Calc): Int

    @Update
    fun update(calc: Calc)
}