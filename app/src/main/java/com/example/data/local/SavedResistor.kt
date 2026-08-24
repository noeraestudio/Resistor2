package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_resistors")
data class SavedResistor(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val resistanceOhms: Double,
    val tolerancePercent: Double,
    val wattageRating: Double,
    val bandCount: Int,
    val bandColorsCsv: String, // e.g. "YELLOW,VIOLET,RED,GOLD"
    val timestamp: Long = System.currentTimeMillis(),
    val notes: String = ""
)
