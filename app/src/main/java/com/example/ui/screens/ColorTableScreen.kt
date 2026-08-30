package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ResistorColor
import com.example.ui.components.ResistorGraphic
import com.example.util.ResistorCalculator

enum class TableViewMode(val title: String) {
    TABLE_ALL("Tabel Kode Warna"),
    GUIDE_4_BAND("Panduan 4 Gelang"),
    GUIDE_5_BAND("Panduan 5 Gelang"),
    GUIDE_6_BAND("Panduan 6 Gelang"),
    PRACTICE("Latihan Mandiri")
}

@Composable
fun ColorTableScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedMode by remember { mutableStateOf(TableViewMode.TABLE_ALL) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // === HEADER CARD ===
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    modifier = Modifier.size(44.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Default.School,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TABEL & PANDUAN KODE WARNA",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Pelajari & Hitung Manual Nilai Resistor",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // === JEMBATAN KELEDAI HAFALAN CARD ===
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.Lightbulb,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Jembatan Keledai / Rumus Cepat Hafal:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "HI - CO - ME - O - KU - HI - BI - U - A - PU (EM - PE)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        letterSpacing = 0.8.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Hitam(0), Cokelat(1), Merah(2), Oranye(3), Kuning(4), Hijau(5), Biru(6), Ungu(7), Abu-abu(8), Putih(9), Emas(±5%), Perak(±10%).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f),
                    fontSize = 11.sp
                )
            }
        }

        // === MODE SELECTOR CHIPS / SEGMENTED BUTTONS ===
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TableViewMode.values().forEach { mode ->
                FilterChip(
                    selected = selectedMode == mode,
                    onClick = { selectedMode = mode },
                    label = {
                        Text(
                            text = mode.title,
                            fontWeight = if (selectedMode == mode) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_mode_${mode.name.lowercase()}")
                )
            }
        }

        // === CONTENT BASED ON SELECTED MODE ===
        when (selectedMode) {
            TableViewMode.TABLE_ALL -> FullColorTableSection()
            TableViewMode.GUIDE_4_BAND -> Guide4BandSection()
            TableViewMode.GUIDE_5_BAND -> Guide5BandSection()
            TableViewMode.GUIDE_6_BAND -> Guide6BandSection()
            TableViewMode.PRACTICE -> PracticeCalculatorSection()
        }
    }
}

@Composable
fun FullColorTableSection() {
    val allColors = listOf(
        ResistorColor.BLACK,
        ResistorColor.BROWN,
        ResistorColor.RED,
        ResistorColor.ORANGE,
        ResistorColor.YELLOW,
        ResistorColor.GREEN,
        ResistorColor.BLUE,
        ResistorColor.VIOLET,
        ResistorColor.GREY,
        ResistorColor.WHITE,
        ResistorColor.GOLD,
        ResistorColor.SILVER,
        ResistorColor.NONE
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = "TABEL LENGKAP KODE WARNA RESISTOR",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Standar EIA-RS-279 / IEC 60062",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Table Header
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Warna",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(90.dp)
                    )
                    Text(
                        text = "Digit",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(42.dp)
                    )
                    Text(
                        text = "Pengali (×)",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Toleransi",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(64.dp)
                    )
                    Text(
                        text = "PPM",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(46.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Table Rows
            allColors.forEachIndexed { index, color ->
                val rowBg = if (index % 2 == 0) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    color = rowBg
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Color Pill & Name
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.width(90.dp)
                        ) {
                            if (color == ResistorColor.NONE) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(color.composeColor)
                                        .border(0.5.dp, Color.Gray.copy(alpha = 0.5f), CircleShape)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = color.nameIndonesian,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }

                        // Digit
                        Text(
                            text = color.digit?.toString() ?: "-",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(42.dp),
                            fontSize = 12.sp
                        )

                        // Multiplier
                        Text(
                            text = formatMultiplier(color.multiplier),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.weight(1f),
                            fontSize = 11.sp
                        )

                        // Tolerance
                        Text(
                            text = if (color.tolerancePercent != null) "±${color.tolerancePercent}%" else "-",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (color.tolerancePercent != null) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (color.tolerancePercent != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(64.dp),
                            fontSize = 11.sp
                        )

                        // PPM
                        Text(
                            text = color.tempCoeffPpm?.let { "$it" } ?: "-",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.width(46.dp),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Guide4BandSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "PANDUAN MENGHITUNG RESISTOR 4 GELANG",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Resistor 4 gelang adalah tipe resistor yang paling umum digunakan pada rangkaian elektronika dasar.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Step Breakdown
            val steps = listOf(
                "Gelang ke-1" to "Digit Angka Pertama (0-9)",
                "Gelang ke-2" to "Digit Angka Kedua (0-9)",
                "Gelang ke-3" to "Faktor Pengali (10ⁿ)",
                "Gelang ke-4" to "Nilai Toleransi (mis. Emas = ±5%, Perak = ±10%)"
            )

            steps.forEachIndexed { idx, (gelang, desc) ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${idx + 1}",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = gelang, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Formula Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Rumus Perhitungan Manual:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "R = (Digit 1 × 10 + Digit 2) × Pengali ± Toleransi",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Example Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Contoh Perhitungan Riil:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Gelang: Cokelat - Hitam - Merah - Emas\n" +
                                "• Cokelat = 1 (Digit 1)\n" +
                                "• Hitam = 0 (Digit 2)\n" +
                                "• Merah = × 100 (Pengali)\n" +
                                "• Emas = ± 5% (Toleransi)\n\n" +
                                "Perhitungan: (10) × 100 = 1.000 Ω (1 kΩ) ± 5%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun Guide5BandSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "PANDUAN MENGHITUNG RESISTOR 5 GELANG (PRESISI TINGGI)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Resistor 5 gelang umumnya adalah tipe Metal Film dengan presisi tinggi (toleransi 1% atau lebih kecil) dengan 3 digit signifikan.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val steps = listOf(
                "Gelang ke-1" to "Digit Angka Pertama (0-9)",
                "Gelang ke-2" to "Digit Angka Kedua (0-9)",
                "Gelang ke-3" to "Digit Angka Ketiga (0-9)",
                "Gelang ke-4" to "Faktor Pengali (10ⁿ)",
                "Gelang ke-5" to "Nilai Toleransi (mis. Cokelat = ±1%, Merah = ±2%)"
            )

            steps.forEachIndexed { idx, (gelang, desc) ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${idx + 1}",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = gelang, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // Formula Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Rumus Perhitungan Manual:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "R = (Digit 1 × 100 + Digit 2 × 10 + Digit 3) × Pengali ± Toleransi",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.primary,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Example Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Contoh Perhitungan Riil:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Gelang: Kuning - Ungu - Hitam - Oranye - Cokelat\n" +
                                "• Kuning = 4 (Digit 1)\n" +
                                "• Ungu = 7 (Digit 2)\n" +
                                "• Hitam = 0 (Digit 3)\n" +
                                "• Oranye = × 1.000 (Pengali)\n" +
                                "• Cokelat = ± 1% (Toleransi)\n\n" +
                                "Perhitungan: (470) × 1.000 = 470.000 Ω (470 kΩ) ± 1%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun Guide6BandSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "PANDUAN MENGHITUNG RESISTOR 6 GELANG",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Resistor 6 gelang identik dengan resistor 5 gelang, ditambah gelang ke-6 yang menentukan Koefisien Suhu (TCR / Temperature Coefficient of Resistance dalam ppm/K atau ppm/°C).",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val steps = listOf(
                "Gelang ke-1" to "Digit Angka Pertama (0-9)",
                "Gelang ke-2" to "Digit Angka Kedua (0-9)",
                "Gelang ke-3" to "Digit Angka Ketiga (0-9)",
                "Gelang ke-4" to "Faktor Pengali (10ⁿ)",
                "Gelang ke-5" to "Nilai Toleransi (mis. Cokelat = ±1%)",
                "Gelang ke-6" to "Koefisien Suhu TCR (Cokelat = 100 ppm, Merah = 50 ppm)"
            )

            steps.forEachIndexed { idx, (gelang, desc) ->
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${idx + 1}",
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = gelang, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            Text(text = desc, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            // TCR Explanation Box
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Arti Koefisien Suhu (PPM/°C):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "100 ppm/°C artinya nilai resistansi berubah maksimal 0,01% untuk setiap kenaikan suhu sebesar 1°C. Semakin kecil nilai ppm, semakin stabil resistor terhadap panas.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun PracticeCalculatorSection() {
    var practiceBandCount by remember { mutableIntStateOf(4) }
    var band1 by remember { mutableStateOf(ResistorColor.RED) }
    var band2 by remember { mutableStateOf(ResistorColor.RED) }
    var band3 by remember { mutableStateOf(ResistorColor.BROWN) }
    var band4 by remember { mutableStateOf(ResistorColor.GOLD) }
    var band5 by remember { mutableStateOf(ResistorColor.BROWN) }
    var band6 by remember { mutableStateOf(ResistorColor.BROWN) }

    val currentBands = when (practiceBandCount) {
        4 -> listOf(band1, band2, band3, band4)
        5 -> listOf(band1, band2, band3, band4, band5)
        else -> listOf(band1, band2, band3, band4, band5, band6)
    }

    val result = ResistorCalculator.calculateFromBands(currentBands, practiceBandCount)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LATIHAN HITUNG MANUAL",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                SingleChoiceSegmentedButtonRow {
                    listOf(4, 5, 6).forEachIndexed { idx, count ->
                        SegmentedButton(
                            selected = practiceBandCount == count,
                            onClick = { practiceBandCount = count },
                            shape = SegmentedButtonDefaults.itemShape(idx, 3)
                        ) {
                            Text("$count Gelang", fontSize = 11.sp)
                        }
                    }
                }
            }

            Text(
                text = "Pilih kombinasi warna dan amati rincian langkah rumus perhitungannya:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Resistor graphic preview
            ResistorGraphic(
                bands = currentBands,
                bandCount = practiceBandCount
            )

            // Step by step breakdown card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Rincian Langkah Perhitungan:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (practiceBandCount == 4) {
                        Text(
                            text = "1. Digit 1 (${band1.nameIndonesian}) = ${band1.digit ?: 0}\n" +
                                    "2. Digit 2 (${band2.nameIndonesian}) = ${band2.digit ?: 0}\n" +
                                    "   ➜ Angka Gabungan = ${(band1.digit ?: 0) * 10 + (band2.digit ?: 0)}\n" +
                                    "3. Pengali (${band3.nameIndonesian}) = × ${formatMultiplier(band3.multiplier)}\n" +
                                    "   ➜ ${(band1.digit ?: 0) * 10 + (band2.digit ?: 0)} × ${band3.multiplier} = ${result.resistanceOhms} Ω (${result.formattedResistance})\n" +
                                    "4. Toleransi (${band4.nameIndonesian}) = ± ${band4.tolerancePercent ?: 20.0}%\n" +
                                    "   ➜ Rentang Nilai: ${result.formattedRange}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    } else {
                        Text(
                            text = "1. Digit 1 (${band1.nameIndonesian}) = ${band1.digit ?: 0}\n" +
                                    "2. Digit 2 (${band2.nameIndonesian}) = ${band2.digit ?: 0}\n" +
                                    "3. Digit 3 (${band3.nameIndonesian}) = ${band3.digit ?: 0}\n" +
                                    "   ➜ Angka Gabungan = ${(band1.digit ?: 0) * 100 + (band2.digit ?: 0) * 10 + (band3.digit ?: 0)}\n" +
                                    "4. Pengali (${band4.nameIndonesian}) = × ${formatMultiplier(band4.multiplier)}\n" +
                                    "   ➜ Hasil: ${result.formattedResistance}\n" +
                                    "5. Toleransi (${band5.nameIndonesian}) = ± ${band5.tolerancePercent ?: 20.0}%\n" +
                                    (if (practiceBandCount == 6) "6. Koefisien Suhu (${band6.nameIndonesian}) = ${band6.tempCoeffPpm ?: 0} ppm/°C" else ""),
                            style = MaterialTheme.typography.bodyMedium,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Quick random question button
            OutlinedButton(
                onClick = {
                    val digits = ResistorColor.getDigitColors()
                    val mults = ResistorColor.getMultiplierColors()
                    val tols = ResistorColor.getToleranceColors()
                    band1 = digits.filter { (it.digit ?: 0) > 0 }.random()
                    band2 = digits.random()
                    band3 = if (practiceBandCount == 4) mults.random() else digits.random()
                    band4 = if (practiceBandCount == 4) tols.random() else mults.random()
                    band5 = tols.random()
                    band6 = ResistorColor.getTempCoeffColors().random()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Acak Soal Latihan Baru")
            }
        }
    }
}

private fun formatMultiplier(multiplier: Double): String {
    return when {
        multiplier >= 1_000_000_000 -> "${(multiplier / 1_000_000_000).toInt()} G (10⁹)"
        multiplier >= 1_000_000 -> "${(multiplier / 1_000_000).toInt()} M (10⁶)"
        multiplier >= 1_000 -> "${(multiplier / 1_000).toInt()} k (10³)"
        multiplier == 100.0 -> "100 (10²)"
        multiplier == 10.0 -> "10 (10¹)"
        multiplier == 1.0 -> "1 (10⁰)"
        multiplier == 0.1 -> "0.1 (10⁻¹)"
        multiplier == 0.01 -> "0.01 (10⁻²)"
        else -> "$multiplier"
    }
}
