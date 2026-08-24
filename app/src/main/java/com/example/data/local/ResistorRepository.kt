package com.example.data.local

import kotlinx.coroutines.flow.Flow

class ResistorRepository(private val dao: ResistorDao) {
    val allSavedResistors: Flow<List<SavedResistor>> = dao.getAllSavedResistors()

    suspend fun saveResistor(resistor: SavedResistor) {
        dao.insertResistor(resistor)
    }

    suspend fun deleteResistor(id: Int) {
        dao.deleteResistorById(id)
    }

    suspend fun clearHistory() {
        dao.clearAll()
    }
}
