package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ResistorColor
import com.example.data.local.AppDatabase
import com.example.data.local.ResistorRepository
import com.example.data.local.SavedResistor
import com.example.util.OhmLawResult
import com.example.util.ResistorCalculationResult
import com.example.util.ResistorCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResistorViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ResistorRepository(AppDatabase.getInstance(application).resistorDao())

    val savedResistors: StateFlow<List<SavedResistor>> = repository.allSavedResistors.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _bandCount = MutableStateFlow(4)
    val bandCount: StateFlow<Int> = _bandCount.asStateFlow()

    private val _selectedBands = MutableStateFlow(
        listOf(ResistorColor.BROWN, ResistorColor.BLACK, ResistorColor.RED, ResistorColor.GOLD)
    )
    val selectedBands: StateFlow<List<ResistorColor>> = _selectedBands.asStateFlow()

    private val _customVoltageInput = MutableStateFlow("5.0")
    val customVoltageInput: StateFlow<String> = _customVoltageInput.asStateFlow()

    private val _wattageRating = MutableStateFlow(0.25) // Default 1/4 Watt
    val wattageRating: StateFlow<Double> = _wattageRating.asStateFlow()

    // Mode Praktis / Reverse Calculator
    private val _inverseInputValue = MutableStateFlow("4700")
    val inverseInputValue: StateFlow<String> = _inverseInputValue.asStateFlow()

    private val _inverseTolerance = MutableStateFlow(5.0)
    val inverseTolerance: StateFlow<Double> = _inverseTolerance.asStateFlow()

    private val _inverseBandCount = MutableStateFlow(4)
    val inverseBandCount: StateFlow<Int> = _inverseBandCount.asStateFlow()

    // Combined Calculation Result State
    val calculationResult: StateFlow<ResistorCalculationResult> = combine(
        _selectedBands,
        _bandCount
    ) { bands, count ->
        ResistorCalculator.calculateFromBands(bands, count)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ResistorCalculator.calculateFromBands(_selectedBands.value, _bandCount.value)
    )

    // Combined Ohm's Law State
    val ohmLawResult: StateFlow<OhmLawResult> = combine(
        calculationResult,
        _customVoltageInput,
        _wattageRating
    ) { calc, voltStr, watt ->
        val v = voltStr.toDoubleOrNull() ?: 0.0
        ResistorCalculator.calculateOhmLaw(
            resistanceOhms = calc.resistanceOhms,
            voltageInput = v,
            powerRatingWatts = watt
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ResistorCalculator.calculateOhmLaw(
            calculationResult.value.resistanceOhms,
            5.0,
            0.25
        )
    )

    // Calculated Inverse Bands
    val inverseBandsResult: StateFlow<List<ResistorColor>?> = combine(
        _inverseInputValue,
        _inverseTolerance,
        _inverseBandCount
    ) { input, tol, count ->
        val parsedOhms = parseResistorInputToOhms(input)
        if (parsedOhms != null && parsedOhms > 0) {
            ResistorCalculator.convertValueToBands(
                targetResistanceOhms = parsedOhms,
                tolerancePercent = tol,
                bandCount = count
            )
        } else {
            null
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ResistorCalculator.convertValueToBands(4700.0, 5.0, 4)
    )

    fun setBandCount(count: Int) {
        if (_bandCount.value == count) return
        _bandCount.value = count
        val current = _selectedBands.value.toMutableList()
        if (count == 4) {
            _selectedBands.value = listOf(
                current.getOrElse(0) { ResistorColor.BROWN },
                current.getOrElse(1) { ResistorColor.BLACK },
                current.getOrElse(2) { ResistorColor.RED },
                current.getOrElse(3) { ResistorColor.GOLD }
            )
        } else if (count == 5) {
            _selectedBands.value = listOf(
                current.getOrElse(0) { ResistorColor.BROWN },
                current.getOrElse(1) { ResistorColor.BLACK },
                current.getOrElse(2) { ResistorColor.BLACK },
                current.getOrElse(3) { ResistorColor.BROWN },
                current.getOrElse(4) { ResistorColor.GOLD }
            )
        } else if (count == 6) {
            _selectedBands.value = listOf(
                current.getOrElse(0) { ResistorColor.BROWN },
                current.getOrElse(1) { ResistorColor.BLACK },
                current.getOrElse(2) { ResistorColor.BLACK },
                current.getOrElse(3) { ResistorColor.BROWN },
                current.getOrElse(4) { ResistorColor.GOLD },
                current.getOrElse(5) { ResistorColor.BROWN }
            )
        }
    }

    fun updateBandColor(index: Int, color: ResistorColor) {
        val list = _selectedBands.value.toMutableList()
        if (index in list.indices) {
            list[index] = color
            _selectedBands.value = list
        }
    }

    fun setVoltageInput(input: String) {
        _customVoltageInput.value = input
    }

    fun setWattageRating(watt: Double) {
        _wattageRating.value = watt
    }

    fun setInverseInput(input: String) {
        _inverseInputValue.value = input
    }

    fun setInverseTolerance(tolerance: Double) {
        _inverseTolerance.value = tolerance
    }

    fun setInverseBandCount(count: Int) {
        _inverseBandCount.value = count
    }

    fun loadResistorToCalculator(saved: SavedResistor) {
        _bandCount.value = saved.bandCount
        _wattageRating.value = saved.wattageRating
        val colorNames = saved.bandColorsCsv.split(",")
        val colors = colorNames.mapNotNull { name ->
            try { ResistorColor.valueOf(name.trim()) } catch (e: Exception) { null }
        }
        if (colors.size >= saved.bandCount) {
            _selectedBands.value = colors.take(saved.bandCount)
        }
    }

    fun saveCurrentResistor(title: String, notes: String = "") {
        val calc = calculationResult.value
        val bandsCsv = _selectedBands.value.joinToString(",") { it.idName }
        val resistorToSave = SavedResistor(
            title = if (title.isBlank()) calc.formattedResistance else title,
            resistanceOhms = calc.resistanceOhms,
            tolerancePercent = calc.tolerancePercent,
            wattageRating = _wattageRating.value,
            bandCount = _bandCount.value,
            bandColorsCsv = bandsCsv,
            notes = notes
        )
        viewModelScope.launch {
            repository.saveResistor(resistorToSave)
        }
    }

    fun deleteSavedResistor(id: Int) {
        viewModelScope.launch {
            repository.deleteResistor(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    private fun parseResistorInputToOhms(input: String): Double? {
        val cleaned = input.trim().lowercase()
        if (cleaned.isEmpty()) return null

        try {
            if (cleaned.endsWith("k") || cleaned.endsWith("kΩ") || cleaned.endsWith("kohm")) {
                val num = cleaned.replace("kohm", "").replace("kΩ", "").replace("k", "").toDoubleOrNull()
                return num?.times(1_000.0)
            } else if (cleaned.endsWith("m") || cleaned.endsWith("mΩ") || cleaned.endsWith("mohm")) {
                val num = cleaned.replace("mohm", "").replace("mΩ", "").replace("m", "").toDoubleOrNull()
                return num?.times(1_000_000.0)
            } else if (cleaned.endsWith("g") || cleaned.endsWith("gΩ") || cleaned.endsWith("gohm")) {
                val num = cleaned.replace("gohm", "").replace("gΩ", "").replace("g", "").toDoubleOrNull()
                return num?.times(1_000_000_000.0)
            } else if (cleaned.contains("r")) {
                // E.g. "4r7" = 4.7
                val parts = cleaned.replace("r", ".").toDoubleOrNull()
                return parts
            } else {
                val num = cleaned.replace("Ω", "").replace("ohm", "").replace("ohms", "").toDoubleOrNull()
                return num
            }
        } catch (e: Exception) {
            return null
        }
    }
}
