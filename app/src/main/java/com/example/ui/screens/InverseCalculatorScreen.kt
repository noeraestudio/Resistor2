package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ResistorColor
import com.example.ui.ResistorViewModel
import com.example.ui.components.ResistorGraphic
import com.example.util.ResistorCalculator

@Composable
fun InverseCalculatorScreen(
    viewModel: ResistorViewModel,
    inverseInput: String,
    inverseTolerance: Double,
    inverseBandCount: Int,
    resultBands: List<ResistorColor>?,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val parsedTargetOhms = remember(inverseInput) {
        parseInputToDouble(inverseInput)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Mode Title Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "MODE PRAKTIS: NILAI KE GELANG WARNA",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Ketik nilai resistor dalam Ohm (mis. 4700, 4.7k, 1M) untuk menentukan pita gelang warna dan rentang toleransinya secara otomatis.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // Input Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "1. Masukkan Nilai Resistor:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = inverseInput,
                    onValueChange = { viewModel.setInverseInput(it) },
                    label = { Text("Nilai Resistansi") },
                    placeholder = { Text("contoh: 4.7k, 220, 1M, 4700") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("inverse_input_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Quick Value Shortcut Chips
                Text(
                    text = "Pilihan Cepat:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("220", "1k", "4.7k", "10k", "100k", "1M").forEach { shortcut ->
                        FilterChip(
                            selected = inverseInput == shortcut,
                            onClick = { viewModel.setInverseInput(shortcut) },
                            label = { Text(shortcut) },
                            modifier = Modifier.testTag("shortcut_chip_$shortcut")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "2. Pilih Persentase Toleransi:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        1.0 to "±1% (Cokelat)",
                        2.0 to "±2% (Merah)",
                        5.0 to "±5% (Emas)",
                        10.0 to "±10% (Perak)"
                    ).forEach { (tolVal, label) ->
                        FilterChip(
                            selected = inverseTolerance == tolVal,
                            onClick = { viewModel.setInverseTolerance(tolVal) },
                            label = { Text(label) },
                            modifier = Modifier.testTag("tol_chip_${tolVal.toInt()}")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "3. Format Gelang:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    SingleChoiceSegmentedButtonRow {
                        listOf(4, 5, 6).forEachIndexed { index, count ->
                            SegmentedButton(
                                selected = inverseBandCount == count,
                                onClick = { viewModel.setInverseBandCount(count) },
                                shape = SegmentedButtonDefaults.itemShape(index = index, count = 3),
                                modifier = Modifier.testTag("inverse_band_btn_$count")
                            ) {
                                Text("$count Gelang")
                            }
                        }
                    }
                }
            }
        }

        // Result Graphic & Tolerance Calculation
        if (resultBands != null && parsedTargetOhms != null && parsedTargetOhms > 0) {
            val calcResult = ResistorCalculator.calculateFromBands(resultBands, inverseBandCount)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "GELANG WARNA RESISTOR HASIL KONVERSI",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ResistorGraphic(
                        bands = resultBands,
                        bandCount = inverseBandCount
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Tolerance Spread Breakdown Card
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "ANALISIS RENTANG TOLERANSI PRAKTIS",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Nilai Nominal:", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    calcResult.formattedResistance,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Toleransi (%):", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "±${calcResult.tolerancePercent}%",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Nilai Minimum (R_min):", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    ResistorCalculator.formatResistance(calcResult.minResistanceOhms),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF16A34A)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Nilai Maksimum (R_max):", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    ResistorCalculator.formatResistance(calcResult.maxResistanceOhms),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFDC2626)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            val spread = calcResult.maxResistanceOhms - calcResult.minResistanceOhms
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Rentang Toleransi Total (ΔR):", style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    ResistorCalculator.formatResistance(spread),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // E-Series Standard Recommendation
                    val nearestE12 = ResistorCalculator.findNearestEStandard(parsedTargetOhms, ResistorCalculator.E12_VALUES)
                    val nearestE24 = ResistorCalculator.findNearestEStandard(parsedTargetOhms, ResistorCalculator.E24_VALUES)

                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "REKOMENDASI NILAI STANDAR PASAR (E-SERIES):",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• Seri E12 (Toleransi 10%): ${ResistorCalculator.formatResistance(nearestE12)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = "• Seri E24 (Toleransi 5%): ${ResistorCalculator.formatResistance(nearestE24)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        } else if (inverseInput.isNotBlank()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Text(
                    text = "Format nilai resistansi tidak terdeteksi. Gunakan angka biasa (mis. 4700) atau satuan (mis. 4.7k, 1M).",
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

private fun parseInputToDouble(input: String): Double? {
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
