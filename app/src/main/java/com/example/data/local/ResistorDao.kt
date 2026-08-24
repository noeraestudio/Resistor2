package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ResistorDao {
    @Query("SELECT * FROM saved_resistors ORDER BY timestamp DESC")
    fun getAllSavedResistors(): Flow<List<SavedResistor>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertResistor(resistor: SavedResistor): Long

    @Query("DELETE FROM saved_resistors WHERE id = :id")
    suspend fun deleteResistorById(id: Int)

    @Query("DELETE FROM saved_resistors")
    suspend fun clearAll()
}
