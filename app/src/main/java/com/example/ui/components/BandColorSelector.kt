package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ResistorColor

/**
 * Compact horizontal / grid tiles representing each resistor band.
 * Clicking a tile triggers the color selection popup / bottom sheet.
 */
@Composable
fun CompactBandTilesRow(
    bandCount: Int,
    selectedBands: List<ResistorColor>,
    activeBandIndex: Int?,
    onBandTileClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val totalBands = bandCount.coerceIn(4, 6)
    
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pilih Warna Gelang (Tap untuk ubah)",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid / Row of compact tiles for all bands
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                for (i in 0 until totalBands) {
                    val color = selectedBands.getOrNull(i) ?: ResistorColor.BLACK
                    val isSelected = activeBandIndex == i
                    val roleShort = getBandRoleShortName(i, totalBands)
                    val valueLabel = getSubtitleForColor(color, i, totalBands)

                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onBandTileClick(i) }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .testTag("band_tile_$i"),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface,
                        tonalElevation = if (isSelected) 4.dp else 0.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = roleShort,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Circle Color swatch
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .background(if (color == ResistorColor.NONE) Color.Gray else color.composeColor)
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f), CircleShape)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = color.nameIndonesian,
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )

                            if (valueLabel.isNotEmpty()) {
                                Text(
                                    text = valueLabel,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Modal BottomSheet for selecting colors without scrolling the main screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BandColorBottomSheet(
    bandIndex: Int,
    bandCount: Int,
    selectedColor: ResistorColor,
    onColorSelected: (Int, ResistorColor) -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val roleTitle = getBandRoleTitle(bandIndex, bandCount)
    val availableColors = getAvailableColorsForBand(bandIndex, bandCount)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
        ) {
            // Header with title and close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Pilih Warna Gelang",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = roleTitle,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onDismissRequest) {
                    Icon(Icons.Default.Close, contentDescription = "Tutup")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grid of selectable colors
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.heightIn(max = 380.dp)
            ) {
                items(availableColors) { resistorColor ->
                    val isSelected = resistorColor == selectedColor
                    val subtitleValue = getSubtitleForColor(resistorColor, bandIndex, bandCount)

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onColorSelected(bandIndex, resistorColor)
                                onDismissRequest()
                            }
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .testTag("popup_color_${resistorColor.idName}"),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        tonalElevation = if (isSelected) 3.dp else 1.dp
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(if (resistorColor == ResistorColor.NONE) Color.LightGray else resistorColor.composeColor)
                                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    val iconTint = if (resistorColor == ResistorColor.WHITE || resistorColor == ResistorColor.YELLOW || resistorColor == ResistorColor.GOLD || resistorColor == ResistorColor.SILVER) Color.Black else Color.White
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = null,
                                        tint = iconTint,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = resistorColor.nameIndonesian,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                                if (subtitleValue.isNotEmpty()) {
                                    Text(
                                        text = subtitleValue,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getSubtitleForColor(color: ResistorColor, bandIndex: Int, totalBands: Int): String {
    val isMultiplier = (totalBands == 4 && bandIndex == 2) || (totalBands >= 5 && bandIndex == 3)
    val isTolerance = (totalBands == 4 && bandIndex == 3) || (totalBands >= 5 && bandIndex == 4)
    val isTempCoeff = (totalBands == 6 && bandIndex == 5)

    return when {
        isMultiplier -> {
            when {
                color.multiplier >= 1_000_000_000 -> "x1G"
                color.multiplier >= 1_000_000 -> "x1M"
                color.multiplier >= 1_000 -> "x1k"
                color.multiplier == 100.0 -> "x100"
                color.multiplier == 10.0 -> "x10"
                color.multiplier == 1.0 -> "x1"
                color.multiplier == 0.1 -> "x0.1"
                color.multiplier == 0.01 -> "x0.01"
                else -> "x${color.multiplier}"
            }
        }
        isTolerance -> {
            color.tolerancePercent?.let { "±$it%" } ?: ""
        }
        isTempCoeff -> {
            color.tempCoeffPpm?.let { "$it ppm" } ?: ""
        }
        else -> { // Digit
            color.digit?.toString() ?: ""
        }
    }
}

fun getBandRoleTitle(index: Int, totalBands: Int): String {
    return when (totalBands) {
        4 -> when (index) {
            0 -> "Gelang 1 (Digit Pertama)"
            1 -> "Gelang 2 (Digit Kedua)"
            2 -> "Gelang 3 (Pengali / Multiplier)"
            3 -> "Gelang 4 (Toleransi)"
            else -> ""
        }
        5 -> when (index) {
            0 -> "Gelang 1 (Digit Pertama)"
            1 -> "Gelang 2 (Digit Kedua)"
            2 -> "Gelang 3 (Digit Ketiga)"
            3 -> "Gelang 4 (Pengali / Multiplier)"
            4 -> "Gelang 5 (Toleransi)"
            else -> ""
        }
        6 -> when (index) {
            0 -> "Gelang 1 (Digit Pertama)"
            1 -> "Gelang 2 (Digit Kedua)"
            2 -> "Gelang 3 (Digit Ketiga)"
            3 -> "Gelang 4 (Pengali / Multiplier)"
            4 -> "Gelang 5 (Toleransi)"
            5 -> "Gelang 6 (Koefisien Suhu PPM)"
            else -> ""
        }
        else -> ""
    }
}

fun getBandRoleShortName(index: Int, totalBands: Int): String {
    return when (totalBands) {
        4 -> when (index) {
            0 -> "Digit 1"
            1 -> "Digit 2"
            2 -> "Pengali"
            3 -> "Toleransi"
            else -> "B${index+1}"
        }
        5 -> when (index) {
            0 -> "Digit 1"
            1 -> "Digit 2"
            2 -> "Digit 3"
            3 -> "Pengali"
            4 -> "Toleransi"
            else -> "B${index+1}"
        }
        6 -> when (index) {
            0 -> "Digit 1"
            1 -> "Digit 2"
            2 -> "Digit 3"
            3 -> "Pengali"
            4 -> "Toleransi"
            5 -> "PPM"
            else -> "B${index+1}"
        }
        else -> "B${index+1}"
    }
}

fun getAvailableColorsForBand(index: Int, totalBands: Int): List<ResistorColor> {
    val isMultiplier = (totalBands == 4 && index == 2) || (totalBands >= 5 && index == 3)
    val isTolerance = (totalBands == 4 && index == 3) || (totalBands >= 5 && index == 4)
    val isTempCoeff = (totalBands == 6 && index == 5)

    return when {
        isMultiplier -> ResistorColor.getMultiplierColors()
        isTolerance -> ResistorColor.getToleranceColors()
        isTempCoeff -> ResistorColor.getTempCoeffColors()
        else -> ResistorColor.getDigitColors()
    }
}
