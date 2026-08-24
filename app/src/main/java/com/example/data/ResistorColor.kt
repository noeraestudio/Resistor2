package com.example.data

import androidx.compose.ui.graphics.Color

enum class ResistorColor(
    val idName: String,
    val nameIndonesian: String,
    val hexColor: Long,
    val textColorHex: Long,
    val digit: Int?,
    val multiplier: Double,
    val tolerancePercent: Double?,
    val tempCoeffPpm: Int?
) {
    BLACK(
        idName = "BLACK",
        nameIndonesian = "Hitam",
        hexColor = 0xFF1E293BL,
        textColorHex = 0xFFFFFFFFL,
        digit = 0,
        multiplier = 1.0,
        tolerancePercent = null,
        tempCoeffPpm = 200
    ),
    BROWN(
        idName = "BROWN",
        nameIndonesian = "Cokelat",
        hexColor = 0xFF8B4513L,
        textColorHex = 0xFFFFFFFFL,
        digit = 1,
        multiplier = 10.0,
        tolerancePercent = 1.0,
        tempCoeffPpm = 100
    ),
    RED(
        idName = "RED",
        nameIndonesian = "Merah",
        hexColor = 0xFFDC2626L,
        textColorHex = 0xFFFFFFFFL,
        digit = 2,
        multiplier = 100.0,
        tolerancePercent = 2.0,
        tempCoeffPpm = 50
    ),
    ORANGE(
        idName = "ORANGE",
        nameIndonesian = "Oranye",
        hexColor = 0xFFEA580CL,
        textColorHex = 0xFFFFFFFFL,
        digit = 3,
        multiplier = 1_000.0,
        tolerancePercent = 0.05,
        tempCoeffPpm = 15
    ),
    YELLOW(
        idName = "YELLOW",
        nameIndonesian = "Kuning",
        hexColor = 0xFFEAB308L,
        textColorHex = 0xFF000000L,
        digit = 4,
        multiplier = 10_000.0,
        tolerancePercent = 0.02,
        tempCoeffPpm = 25
    ),
    GREEN(
        idName = "GREEN",
        nameIndonesian = "Hijau",
        hexColor = 0xFF16A34AL,
        textColorHex = 0xFFFFFFFFL,
        digit = 5,
        multiplier = 100_000.0,
        tolerancePercent = 0.5,
        tempCoeffPpm = 20
    ),
    BLUE(
        idName = "BLUE",
        nameIndonesian = "Biru",
        hexColor = 0xFF2563EBL,
        textColorHex = 0xFFFFFFFFL,
        digit = 6,
        multiplier = 1_000_000.0,
        tolerancePercent = 0.25,
        tempCoeffPpm = 10
    ),
    VIOLET(
        idName = "VIOLET",
        nameIndonesian = "Ungu",
        hexColor = 0xFF9333EAL,
        textColorHex = 0xFFFFFFFFL,
        digit = 7,
        multiplier = 10_000_000.0,
        tolerancePercent = 0.1,
        tempCoeffPpm = 5
    ),
    GREY(
        idName = "GREY",
        nameIndonesian = "Abu-abu",
        hexColor = 0xFF64748BL,
        textColorHex = 0xFFFFFFFFL,
        digit = 8,
        multiplier = 100_000_000.0,
        tolerancePercent = 0.01,
        tempCoeffPpm = 1
    ),
    WHITE(
        idName = "WHITE",
        nameIndonesian = "Putih",
        hexColor = 0xFFF8FAFCL,
        textColorHex = 0xFF000000L,
        digit = 9,
        multiplier = 1_000_000_000.0,
        tolerancePercent = null,
        tempCoeffPpm = null
    ),
    GOLD(
        idName = "GOLD",
        nameIndonesian = "Emas",
        hexColor = 0xFFD4AF37L,
        textColorHex = 0xFF000000L,
        digit = null,
        multiplier = 0.1,
        tolerancePercent = 5.0,
        tempCoeffPpm = null
    ),
    SILVER(
        idName = "SILVER",
        nameIndonesian = "Perak",
        hexColor = 0xFFC0C0C0L,
        textColorHex = 0xFF000000L,
        digit = null,
        multiplier = 0.01,
        tolerancePercent = 10.0,
        tempCoeffPpm = null
    ),
    NONE(
        idName = "NONE",
        nameIndonesian = "Tanpa Gelang",
        hexColor = 0x00000000L,
        textColorHex = 0xFF000000L,
        digit = null,
        multiplier = 1.0,
        tolerancePercent = 20.0,
        tempCoeffPpm = null
    );

    val composeColor: Color
        get() = Color(hexColor)

    val composeTextColor: Color
        get() = Color(textColorHex)

    companion object {
        fun getDigitColors(): List<ResistorColor> = listOf(
            BLACK, BROWN, RED, ORANGE, YELLOW, GREEN, BLUE, VIOLET, GREY, WHITE
        )

        fun getMultiplierColors(): List<ResistorColor> = listOf(
            BLACK, BROWN, RED, ORANGE, YELLOW, GREEN, BLUE, VIOLET, GREY, WHITE, GOLD, SILVER
        )

        fun getToleranceColors(): List<ResistorColor> = listOf(
            BROWN, RED, GREEN, BLUE, VIOLET, GREY, GOLD, SILVER, NONE
        )

        fun getTempCoeffColors(): List<ResistorColor> = listOf(
            BLACK, BROWN, RED, ORANGE, YELLOW, GREEN, BLUE, VIOLET, GREY
        )
    }
}
