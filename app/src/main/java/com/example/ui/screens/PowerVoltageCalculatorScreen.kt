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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ResistorViewModel
import com.example.util.OhmLawResult
import com.example.util.ResistorCalculationResult
import java.util.Locale

@Composable
fun PowerVoltageCalculatorScreen(
    viewModel: ResistorViewModel,
    calculationResult: ResistorCalculationResult,
    ohmLawResult: OhmLawResult,
    customVoltageInput: String,
    wattageRating: Double,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Active Resistor Summary Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "RESISTOR TERPILIH",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = calculationResult.formattedResistance,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Rating: $wattageRating Watt",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Voltage Input Card with Presets
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Penyesuaian Tegangan (Voltase):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Masukkan tegangan kerja (V) yang diberikan pada resistor",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = customVoltageInput,
                    onValueChange = { viewModel.setVoltageInput(it) },
                    label = { Text("Tegangan Kerja (Volt)") },
                    trailingIcon = { Text("V", fontWeight = FontWeight.Bold, modifier = Modifier.padding(end = 12.dp)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voltage_input_field")
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Preset Tegangan Cepat:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("3.3", "5.0", "9.0", "12.0", "24.0").forEach { vPreset ->
                        FilterChip(
                            selected = customVoltageInput == vPreset,
                            onClick = { viewModel.setVoltageInput(vPreset) },
                            label = { Text("${vPreset}V") },
                            modifier = Modifier.testTag("voltage_preset_$vPreset")
                        )
                    }
                }
            }
        }

        // Rating Daya Resistor Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Pilih Rating Daya Resistor (Watt):",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        0.125 to "1/8W",
                        0.25 to "1/4W",
                        0.5 to "1/2W",
                        1.0 to "1W",
                        2.0 to "2W",
                        5.0 to "5W"
                    ).forEach { (wattVal, label) ->
                        FilterChip(
                            selected = wattageRating == wattVal,
                            onClick = { viewModel.setWattageRating(wattVal) },
                            label = { Text(label) },
                            modifier = Modifier.testTag("power_watt_chip_$label")
                        )
                    }
                }
            }
        }

        // Thermal Warning / Safe Status Banner
        if (ohmLawResult.isOverloaded) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.errorContainer
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PERINGATAN BAHAYA OVERLOAD!",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "Daya terdisipasi ${formatWatt(ohmLawResult.powerWatts)} melebihi rating ${ohmLawResult.ratedPowerWatts}W! Resistor akan sangat panas atau rusak. Gunakan resistor berkekuatan lebih tinggi atau naikkan nilai resistansinya.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        } else if (ohmLawResult.voltageVolts > 0) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF16A34A)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Shield,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "OPERASI AMAN",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Daya terdisipasi ${formatWatt(ohmLawResult.powerWatts)} berada dalam batas aman (${String.format(Locale.US, "%.1f", ohmLawResult.loadPercentage)}% kapasitas rating daya).",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // Calculation Results Grid
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Hasil Perhitungan Hukum Ohm:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Load capacity progress bar
                val progress = (ohmLawResult.loadPercentage / 100.0).toFloat().coerceIn(0f, 1f)
                val progressColor = when {
                    ohmLawResult.isOverloaded -> MaterialTheme.colorScheme.error
                    ohmLawResult.loadPercentage > 80.0 -> Color(0xFFEA580C)
                    else -> Color(0xFF16A34A)
                }

                Text(
                    text = "Beban Kapasitas Daya: ${String.format(Locale.US, "%.1f", ohmLawResult.loadPercentage)}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = progressColor,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Grid stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OhmStatCard(
                        title = "Daya Terdisipasi",
                        value = formatWatt(ohmLawResult.powerWatts),
                        subtitle = "P = V² / R",
                        icon = Icons.Default.Bolt,
                        highlight = ohmLawResult.isOverloaded,
                        modifier = Modifier.weight(1f)
                    )

                    OhmStatCard(
                        title = "Arus Mengalir",
                        value = formatAmpere(ohmLawResult.currentAmperes),
                        subtitle = "I = V / R",
                        icon = Icons.Default.Speed,
                        highlight = false,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OhmStatCard(
                        title = "Voltase Max Aman",
                        value = "${String.format(Locale.US, "%.2f", ohmLawResult.maxSafeVoltageVolts)} V",
                        subtitle = "V_max = √(P · R)",
                        icon = Icons.Default.ElectricBolt,
                        highlight = false,
                        modifier = Modifier.weight(1f)
                    )

                    OhmStatCard(
                        title = "Arus Max Aman",
                        value = formatAmpere(ohmLawResult.maxSafeCurrentAmperes),
                        subtitle = "I_max = √(P / R)",
                        icon = Icons.Default.Shield,
                        highlight = false,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun OhmStatCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    highlight: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (highlight) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val textColor = if (highlight) {
        MaterialTheme.colorScheme.onErrorContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (highlight) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (highlight) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = if (highlight) MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
            )
        }
    }
}

private fun formatWatt(watts: Double): String {
    return if (watts < 0.001) {
        "${String.format(Locale.US, "%.2f", watts * 1000000)} µW"
    } else if (watts < 1.0) {
        "${String.format(Locale.US, "%.2f", watts * 1000)} mW"
    } else {
        "${String.format(Locale.US, "%.2f", watts)} W"
    }
}

private fun formatAmpere(amps: Double): String {
    return if (amps < 0.001) {
        "${String.format(Locale.US, "%.2f", amps * 1000000)} µA"
    } else if (amps < 1.0) {
        "${String.format(Locale.US, "%.2f", amps * 1000)} mA"
    } else {
        "${String.format(Locale.US, "%.2f", amps)} A"
    }
}
