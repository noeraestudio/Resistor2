package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.ui.components.BandColorBottomSheet
import com.example.ui.components.CompactBandTilesRow
import com.example.ui.components.ResistorGraphic
import com.example.util.ResistorCalculator

@Composable
fun ColorCodeCalculatorScreen(
    viewModel: ResistorViewModel,
    calculationResult: com.example.util.ResistorCalculationResult,
    ohmLawResult: com.example.util.OhmLawResult,
    selectedBands: List<ResistorColor>,
    bandCount: Int,
    wattageRating: Double,
    onNavigateToPowerTab: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeBandIndexForSheet by remember { mutableStateOf<Int?>(null) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showWattageDialog by remember { mutableStateOf(false) }
    var showEditResistanceDialog by remember { mutableStateOf(false) }
    var editResistanceInput by remember { mutableStateOf("") }
    var editToleranceSelected by remember { mutableDoubleStateOf(5.0) }

    var saveTitleInput by remember { mutableStateOf("") }
    var saveNotesInput by remember { mutableStateOf("") }
    var saveSuccessMessage by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // === FIXED / STICKY TOP HEADER: RESISTANCE VALUE & RESISTOR GRAPHIC ===

        // 1. Nilai Resistansi Card (Clickable to Edit Value Directly)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable {
                    editResistanceInput = ResistorCalculator.formatResistance(calculationResult.resistanceOhms)
                        .replace(" Ω", "")
                        .replace(" kΩ", "k")
                        .replace(" MΩ", "M")
                        .replace(" GΩ", "G")
                    editToleranceSelected = calculationResult.tolerancePercent
                    showEditResistanceDialog = true
                }
                .testTag("resistance_value_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header row inside Card: Label & Rating Daya Icon + Action Icons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "NILAI RESISTANSI",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.75f),
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Nilai",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(12.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Rating Daya Resistor Icon Chip
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { showWattageDialog = true }
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                    RoundedCornerShape(10.dp)
                                )
                                .testTag("wattage_rating_icon_button"),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                            tonalElevation = 1.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.FlashOn,
                                    contentDescription = "Rating Daya Watt",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = formatWattLabel(wattageRating),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Bookmark / Save Action Button
                        IconButton(
                            onClick = {
                                saveTitleInput = calculationResult.formattedResistance
                                showSaveDialog = true
                            },
                            modifier = Modifier
                                .size(30.dp)
                                .testTag("save_resistor_quick_icon")
                        ) {
                            Icon(
                                Icons.Default.BookmarkAdd,
                                contentDescription = "Simpan Resistor",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Watt & Volt Navigation Action Button
                        IconButton(
                            onClick = onNavigateToPowerTab,
                            modifier = Modifier
                                .size(30.dp)
                                .testTag("power_calc_quick_icon")
                        ) {
                            Icon(
                                Icons.Default.ElectricBolt,
                                contentDescription = "Buka Kalkulator Daya",
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Big Resistance Value (Clickable)
                Text(
                    text = calculationResult.formattedResistance,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 32.sp,
                    modifier = Modifier.testTag("result_resistance_text")
                )

                // Tap to edit prompt hint
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.TouchApp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Ketuk untuk isi nilai langsung (mis. 4.7k, 220, 1M)",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Tolerance & Temperature PPM badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                    ) {
                        Text(
                            text = "Toleransi: ±${calculationResult.tolerancePercent}% (${calculationResult.formattedRange})",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (calculationResult.tempCoeffPpm != null) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                        ) {
                            Text(
                                text = "${calculationResult.tempCoeffPpm} ppm",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        // 2. Fixed Resistor Graphic with Integrated Band Count Icon/Menu (4, 5, 6)
        ResistorGraphic(
            bands = selectedBands,
            bandCount = bandCount,
            selectedBandIndex = activeBandIndexForSheet,
            onBandClick = { clickedIndex ->
                activeBandIndexForSheet = clickedIndex
            },
            onBandCountChange = { count ->
                viewModel.setBandCount(count)
                if (activeBandIndexForSheet != null && activeBandIndexForSheet!! >= count) {
                    activeBandIndexForSheet = null
                }
            }
        )

        // === SCROLLABLE CONTENT (BAND TILES & DETAILS) ===
        Column(
            modifier = Modifier
                .weight(1f, fill = false)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            AnimatedVisibility(visible = saveSuccessMessage, enter = fadeIn(), exit = fadeOut()) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF16A34A)
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Resistor berhasil disimpan ke Riwayat!",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Compact Band Tiles Row for Selecting Colors
            CompactBandTilesRow(
                bandCount = bandCount,
                selectedBands = selectedBands,
                activeBandIndex = activeBandIndexForSheet,
                onBandTileClick = { bandIndex ->
                    activeBandIndexForSheet = bandIndex
                }
            )
        }
    }

    // Modal Bottom Sheet Popup for selecting Color of Active Band
    if (activeBandIndexForSheet != null) {
        val bandIdx = activeBandIndexForSheet!!
        val currentColor = selectedBands.getOrNull(bandIdx) ?: ResistorColor.BLACK

        BandColorBottomSheet(
            bandIndex = bandIdx,
            bandCount = bandCount,
            selectedColor = currentColor,
            onColorSelected = { idx, color ->
                viewModel.updateBandColor(idx, color)
            },
            onDismissRequest = {
                activeBandIndexForSheet = null
            }
        )
    }

    // Direct Resistance Input Dialog (Bidirectional: enter value -> updates colors)
    if (showEditResistanceDialog) {
        val parsedOhms = remember(editResistanceInput) {
            viewModel.parseInputOhms(editResistanceInput)
        }
        val previewBands = remember(parsedOhms, editToleranceSelected, bandCount) {
            if (parsedOhms != null && parsedOhms > 0) {
                ResistorCalculator.convertValueToBands(
                    targetResistanceOhms = parsedOhms,
                    tolerancePercent = editToleranceSelected,
                    bandCount = bandCount
                )
            } else null
        }

        AlertDialog(
            onDismissRequest = { showEditResistanceDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Isi Nilai Resistansi",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Ketik nilai resistor (mis. 4.7k, 220, 1M, 4k7, 100) untuk mengubah warna gelang resistor secara otomatis:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    OutlinedTextField(
                        value = editResistanceInput,
                        onValueChange = { editResistanceInput = it },
                        label = { Text("Nilai Resistansi (Ω / k / M)") },
                        placeholder = { Text("contoh: 4.7k, 220, 1M, 4700") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("direct_resistance_input_field")
                    )

                    // Quick Presets
                    Text(
                        text = "Pilihan Nilai Cepat:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("100", "220", "330", "470", "1k", "2.2k", "4.7k", "10k", "47k", "100k", "1M").forEach { preset ->
                            FilterChip(
                                selected = editResistanceInput == preset,
                                onClick = { editResistanceInput = preset },
                                label = { Text(preset, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Tolerance Selector Chips
                    Text(
                        text = "Pilih Toleransi:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            1.0 to "±1% (Cokelat)",
                            2.0 to "±2% (Merah)",
                            5.0 to "±5% (Emas)",
                            10.0 to "±10% (Perak)"
                        ).forEach { (tol, label) ->
                            FilterChip(
                                selected = editToleranceSelected == tol,
                                onClick = { editToleranceSelected = tol },
                                label = { Text(label, fontSize = 11.sp) }
                            )
                        }
                    }

                    // Live preview of resulting bands if valid
                    if (previewBands != null && parsedOhms != null) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Preview: ${ResistorCalculator.formatResistance(parsedOhms)} (±$editToleranceSelected%)",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    previewBands.forEach { c ->
                                        Box(
                                            modifier = Modifier
                                                .size(width = 16.dp, height = 24.dp)
                                                .clip(RoundedCornerShape(3.dp))
                                                .border(0.5.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(3.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Surface(
                                                modifier = Modifier.fillMaxSize(),
                                                color = c.composeColor
                                            ) {}
                                        }
                                    }
                                }
                            }
                        }
                    } else if (editResistanceInput.isNotBlank()) {
                        Text(
                            text = "Format tidak valid. Contoh format: 4.7k, 220, 1M, 4700, 4r7",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (parsedOhms != null && parsedOhms > 0) {
                            viewModel.setResistanceDirectly(parsedOhms, editToleranceSelected)
                            showEditResistanceDialog = false
                        }
                    },
                    enabled = parsedOhms != null && parsedOhms > 0,
                    modifier = Modifier.testTag("apply_resistance_button")
                ) {
                    Text("Terapkan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditResistanceDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }

    // Wattage Rating Popup Selection Dialog
    if (showWattageDialog) {
        AlertDialog(
            onDismissRequest = { showWattageDialog = false },
            title = {
                Text(
                    text = "Pilih Rating Daya Resistor",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Pilih rating daya fisik maksimum untuk resistor Anda:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    listOf(
                        0.125 to "1/8 Watt (0.125 W) - Mini SMD/DIP",
                        0.25 to "1/4 Watt (0.25 W) - Standar Karbon",
                        0.5 to "1/2 Watt (0.5 W) - Sedang",
                        1.0 to "1 Watt (1.0 W) - Metal Film",
                        2.0 to "2 Watt (2.0 W) - Daya Tinggi",
                        5.0 to "5 Watt (5.0 W) - Keramik / Wirewound"
                    ).forEach { (wattVal, desc) ->
                        val isSelected = wattageRating == wattVal
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.setWattageRating(wattVal)
                                    showWattageDialog = false
                                }
                                .border(
                                    width = if (isSelected) 2.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(10.dp)
                                ),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            tonalElevation = if (isSelected) 3.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = desc,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWattageDialog = false }) {
                    Text("Tutup")
                }
            }
        )
    }

    // Save Resistor Dialog
    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Simpan Resistor") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Simpan nilai ${calculationResult.formattedResistance} (±${calculationResult.tolerancePercent}%) untuk referensi cepat.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedTextField(
                        value = saveTitleInput,
                        onValueChange = { saveTitleInput = it },
                        label = { Text("Nama / Label Resistor") },
                        placeholder = { Text("mis. R1 LED Indikator, Pull-up MCU") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("save_title_field")
                    )
                    OutlinedTextField(
                        value = saveNotesInput,
                        onValueChange = { saveNotesInput = it },
                        label = { Text("Catatan Tambahan (Opsional)") },
                        placeholder = { Text("mis. Rangkaian sensor suhu") },
                        modifier = Modifier.fillMaxWidth().testTag("save_notes_field")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveCurrentResistor(saveTitleInput, saveNotesInput)
                        showSaveDialog = false
                        saveSuccessMessage = true
                    },
                    modifier = Modifier.testTag("confirm_save_button")
                ) {
                    Text("Simpan")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

private fun formatWattLabel(watts: Double): String {
    return when (watts) {
        0.125 -> "1/8W"
        0.25 -> "1/4W"
        0.5 -> "1/2W"
        1.0 -> "1W"
        2.0 -> "2W"
        5.0 -> "5W"
        else -> "${watts}W"
    }
}
