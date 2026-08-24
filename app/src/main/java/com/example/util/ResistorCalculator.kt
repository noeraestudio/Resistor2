package com.example.util

import com.example.data.ResistorColor
import java.util.Locale
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

data class ResistorCalculationResult(
    val resistanceOhms: Double,
    val formattedResistance: String,
    val tolerancePercent: Double,
    val minResistanceOhms: Double,
    val maxResistanceOhms: Double,
    val formattedRange: String,
    val tempCoeffPpm: Int?,
    val isValid: Boolean = true,
    val errorMessage: String? = null
)

data class OhmLawResult(
    val resistanceOhms: Double,
    val voltageVolts: Double,
    val currentAmperes: Double,
    val powerWatts: Double,
    val ratedPowerWatts: Double,
    val maxSafeVoltageVolts: Double,
    val maxSafeCurrentAmperes: Double,
    val isOverloaded: Boolean,
    val loadPercentage: Double
)

object ResistorCalculator {

    val E12_VALUES = listOf(1.0, 1.2, 1.5, 1.8, 2.2, 2.7, 3.3, 3.9, 4.7, 5.6, 6.8, 8.2)
    val E24_VALUES = listOf(
        1.0, 1.1, 1.2, 1.3, 1.5, 1.6, 1.8, 2.0, 2.2, 2.4, 2.7, 3.0,
        3.3, 3.6, 3.9, 4.3, 4.7, 5.1, 5.6, 6.2, 6.8, 7.5, 8.2, 9.1
    )

    fun calculateFromBands(
        bands: List<ResistorColor>,
        bandCount: Int
    ): ResistorCalculationResult {
        if (bands.size < bandCount) {
            return ResistorCalculationResult(
                resistanceOhms = 0.0,
                formattedResistance = "0 Ω",
                tolerancePercent = 0.0,
                minResistanceOhms = 0.0,
                maxResistanceOhms = 0.0,
                formattedRange = "0 Ω - 0 Ω",
                tempCoeffPpm = null,
                isValid = false,
                errorMessage = "Pilih semua warna gelang"
            )
        }

        val digitsValue: Double
        val multiplier: Double
        val tolerancePercent: Double
        val tempCoeff: Int?

        when (bandCount) {
            4 -> {
                val d1 = bands[0].digit ?: 0
                val d2 = bands[1].digit ?: 0
                digitsValue = (d1 * 10 + d2).toDouble()
                multiplier = bands[2].multiplier
                tolerancePercent = bands[3].tolerancePercent ?: 20.0
                tempCoeff = null
            }
            5 -> {
                val d1 = bands[0].digit ?: 0
                val d2 = bands[1].digit ?: 0
                val d3 = bands[2].digit ?: 0
                digitsValue = (d1 * 100 + d2 * 10 + d3).toDouble()
                multiplier = bands[3].multiplier
                tolerancePercent = bands[4].tolerancePercent ?: 20.0
                tempCoeff = null
            }
            6 -> {
                val d1 = bands[0].digit ?: 0
                val d2 = bands[1].digit ?: 0
                val d3 = bands[2].digit ?: 0
                digitsValue = (d1 * 100 + d2 * 10 + d3).toDouble()
                multiplier = bands[3].multiplier
                tolerancePercent = bands[4].tolerancePercent ?: 20.0
                tempCoeff = bands[5].tempCoeffPpm
            }
            else -> return ResistorCalculationResult(
                resistanceOhms = 0.0,
                formattedResistance = "0 Ω",
                tolerancePercent = 0.0,
                minResistanceOhms = 0.0,
                maxResistanceOhms = 0.0,
                formattedRange = "0 Ω - 0 Ω",
                tempCoeffPpm = null,
                isValid = false,
                errorMessage = "Jumlah gelang tidak valid"
            )
        }

        val resistance = digitsValue * multiplier
        val tolDelta = resistance * (tolerancePercent / 100.0)
        val minR = (resistance - tolDelta).coerceAtLeast(0.0)
        val maxR = resistance + tolDelta

        val formattedRes = formatResistance(resistance)
        val formattedMinMax = "${formatResistance(minR)} - ${formatResistance(maxR)}"

        return ResistorCalculationResult(
            resistanceOhms = resistance,
            formattedResistance = formattedRes,
            tolerancePercent = tolerancePercent,
            minResistanceOhms = minR,
            maxResistanceOhms = maxR,
            formattedRange = formattedMinMax,
            tempCoeffPpm = tempCoeff,
            isValid = true
        )
    }

    fun formatResistance(ohms: Double): String {
        if (ohms <= 0) return "0 Ω"
        val absVal = abs(ohms)
        return when {
            absVal >= 1_000_000_000 -> {
                val valInG = ohms / 1_000_000_000.0
                cleanFormat(valInG) + " GΩ"
            }
            absVal >= 1_000_000 -> {
                val valInM = ohms / 1_000_000.0
                cleanFormat(valInM) + " MΩ"
            }
            absVal >= 1_000 -> {
                val valInK = ohms / 1_000.0
                cleanFormat(valInK) + " kΩ"
            }
            absVal < 1.0 -> {
                cleanFormat(ohms) + " Ω"
            }
            else -> {
                cleanFormat(ohms) + " Ω"
            }
        }
    }

    private fun cleanFormat(value: Double): String {
        return if (value == value.toLong().toDouble()) {
            String.format(Locale.US, "%d", value.toLong())
        } else {
            val formatted = String.format(Locale.US, "%.3f", value).trimEnd('0').trimEnd('.')
            if (formatted.isEmpty()) "0" else formatted
        }
    }

    fun calculateOhmLaw(
        resistanceOhms: Double,
        voltageInput: Double?,
        powerRatingWatts: Double = 0.25
    ): OhmLawResult {
        val r = resistanceOhms.coerceAtLeast(0.001)
        val v = voltageInput ?: 0.0
        val i = v / r
        val pActual = (v * v) / r

        val maxSafeVoltage = sqrt(powerRatingWatts * r)
        val maxSafeCurrent = sqrt(powerRatingWatts / r)

        val overloaded = pActual > powerRatingWatts
        val loadPercentage = (pActual / powerRatingWatts) * 100.0

        return OhmLawResult(
            resistanceOhms = r,
            voltageVolts = v,
            currentAmperes = i,
            powerWatts = pActual,
            ratedPowerWatts = powerRatingWatts,
            maxSafeVoltageVolts = maxSafeVoltage,
            maxSafeCurrentAmperes = maxSafeCurrent,
            isOverloaded = overloaded,
            loadPercentage = loadPercentage
        )
    }

    fun convertValueToBands(
        targetResistanceOhms: Double,
        tolerancePercent: Double = 5.0,
        bandCount: Int = 4,
        tempCoeffPpm: Int? = 100
    ): List<ResistorColor>? {
        if (targetResistanceOhms <= 0) return null

        val allDigitColors = ResistorColor.getDigitColors()
        val allMultiplierColors = ResistorColor.getMultiplierColors()
        val allTolColors = ResistorColor.getToleranceColors()
        val allPpmColors = ResistorColor.getTempCoeffColors()

        val toleranceBand = allTolColors.minByOrNull {
            abs((it.tolerancePercent ?: 20.0) - tolerancePercent)
        } ?: ResistorColor.GOLD

        if (bandCount == 4) {
            val exp = log10(targetResistanceOhms).toInt()
            var multExp = exp - 1
            if (multExp < -2) multExp = -2
            if (multExp > 9) multExp = 9

            var multValue = 10.0.pow(multExp.toDouble())
            var sigDigits = (targetResistanceOhms / multValue).roundToInt()

            if (sigDigits >= 100) {
                multExp += 1
                multValue = 10.0.pow(multExp.toDouble())
                sigDigits = (targetResistanceOhms / multValue).roundToInt()
            } else if (sigDigits < 10 && multExp > -2) {
                multExp -= 1
                multValue = 10.0.pow(multExp.toDouble())
                sigDigits = (targetResistanceOhms / multValue).roundToInt()
            }

            sigDigits = sigDigits.coerceIn(0, 99)
            val d1Val = sigDigits / 10
            val d2Val = sigDigits % 10

            val b1 = allDigitColors.firstOrNull { it.digit == d1Val } ?: ResistorColor.BLACK
            val b2 = allDigitColors.firstOrNull { it.digit == d2Val } ?: ResistorColor.BLACK
            val b3Mult = allMultiplierColors.minByOrNull { abs(it.multiplier - multValue) }
                ?: ResistorColor.BLACK

            return listOf(b1, b2, b3Mult, toleranceBand)
        } else {
            // 5 or 6 band
            val exp = log10(targetResistanceOhms).toInt()
            var multExp = exp - 2
            if (multExp < -2) multExp = -2
            if (multExp > 9) multExp = 9

            var multValue = 10.0.pow(multExp.toDouble())
            var sigDigits = (targetResistanceOhms / multValue).roundToInt()

            if (sigDigits >= 1000) {
                multExp += 1
                multValue = 10.0.pow(multExp.toDouble())
                sigDigits = (targetResistanceOhms / multValue).roundToInt()
            } else if (sigDigits < 100 && multExp > -2) {
                multExp -= 1
                multValue = 10.0.pow(multExp.toDouble())
                sigDigits = (targetResistanceOhms / multValue).roundToInt()
            }

            sigDigits = sigDigits.coerceIn(0, 999)
            val d1Val = sigDigits / 100
            val d2Val = (sigDigits / 10) % 10
            val d3Val = sigDigits % 10

            val b1 = allDigitColors.firstOrNull { it.digit == d1Val } ?: ResistorColor.BLACK
            val b2 = allDigitColors.firstOrNull { it.digit == d2Val } ?: ResistorColor.BLACK
            val b3 = allDigitColors.firstOrNull { it.digit == d3Val } ?: ResistorColor.BLACK
            val b4Mult = allMultiplierColors.minByOrNull { abs(it.multiplier - multValue) }
                ?: ResistorColor.BLACK

            if (bandCount == 5) {
                return listOf(b1, b2, b3, b4Mult, toleranceBand)
            } else {
                val ppmBand = allPpmColors.minByOrNull {
                    abs((it.tempCoeffPpm ?: 100) - (tempCoeffPpm ?: 100))
                } ?: ResistorColor.BROWN
                return listOf(b1, b2, b3, b4Mult, toleranceBand, ppmBand)
            }
        }
    }

    fun findNearestEStandard(targetOhms: Double, eSeries: List<Double>): Double {
        if (targetOhms <= 0) return 1.0
        val power = 10.0.pow(log10(targetOhms).toInt().toDouble())
        val normalized = targetOhms / power

        val nearestBase = eSeries.minByOrNull { abs(it - normalized) } ?: 1.0
        return nearestBase * power
    }
}
