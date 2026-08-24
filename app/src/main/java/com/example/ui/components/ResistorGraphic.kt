package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ResistorColor

@Composable
fun ResistorGraphic(
    bands: List<ResistorColor>,
    bandCount: Int,
    selectedBandIndex: Int? = null,
    onBandClick: ((Int) -> Unit)? = null,
    onBandCountChange: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var showBandMenu by remember { mutableStateOf(false) }

    val leadColor = Color(0xFF94A3B8) // Metallic lead wire

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp)
        ) {
            // Main Resistor Canvas drawing
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val centerY = canvasHeight / 2f

                // Draw metallic wire leads
                val leadThickness = 6.dp.toPx()
                drawRect(
                    color = leadColor,
                    topLeft = Offset(0f, centerY - leadThickness / 2f),
                    size = Size(canvasWidth, leadThickness)
                )

                // Resistor main ceramic body dimensions
                val bodyWidth = canvasWidth * 0.72f
                val bodyHeight = 52.dp.toPx()
                val bodyLeft = (canvasWidth - bodyWidth) / 2f
                val bodyTop = centerY - bodyHeight / 2f

                // Resistor body gradient (3D cylinder look)
                val bodyBrush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFCBD5E1),
                        Color(0xFFF1F5F9),
                        Color(0xFFE2E8F0),
                        Color(0xFF94A3B8)
                    ),
                    startY = bodyTop,
                    endY = bodyTop + bodyHeight
                )

                drawRoundRect(
                    brush = bodyBrush,
                    topLeft = Offset(bodyLeft, bodyTop),
                    size = Size(bodyWidth, bodyHeight),
                    cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                )

                // Inner shoulder indents (characteristic resistor bulbs)
                val bulbWidth = 12.dp.toPx()
                val leftBulbLeft = bodyLeft - bulbWidth / 2f
                val rightBulbLeft = bodyLeft + bodyWidth - bulbWidth / 2f
                val bulbHeight = bodyHeight + 8.dp.toPx()
                val bulbTop = centerY - bulbHeight / 2f

                drawRoundRect(
                    brush = bodyBrush,
                    topLeft = Offset(leftBulbLeft, bulbTop),
                    size = Size(bulbWidth, bulbHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
                drawRoundRect(
                    brush = bodyBrush,
                    topLeft = Offset(rightBulbLeft, bulbTop),
                    size = Size(bulbWidth, bulbHeight),
                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )

                // Draw bands on canvas
                val effectiveBands = bands.take(bandCount)
                val bandCountTotal = effectiveBands.size
                val bandWidth = 14.dp.toPx()

                // Calculate band positions across body
                val availableSpan = bodyWidth - 32.dp.toPx()
                val bandStart = bodyLeft + 16.dp.toPx()

                effectiveBands.forEachIndexed { index, resistorColor ->
                    val c = if (resistorColor == ResistorColor.NONE) Color.Transparent else resistorColor.composeColor

                    val xPos = if (index == bandCountTotal - 1 && bandCountTotal >= 4) {
                        // Tolerance band is pushed further to the right
                        bodyLeft + bodyWidth - 28.dp.toPx()
                    } else {
                        val spacing = if (bandCountTotal > 1) {
                            (availableSpan - 30.dp.toPx()) / (bandCountTotal - 1.5f)
                        } else 0f
                        bandStart + (index * spacing)
                    }

                    if (c != Color.Transparent) {
                        // Band 3D vertical shine brush
                        val bandBrush = Brush.verticalGradient(
                            colors = listOf(
                                c.copy(alpha = 0.8f),
                                c,
                                c,
                                c.copy(alpha = 0.7f)
                            ),
                            startY = bodyTop - 4.dp.toPx(),
                            endY = bodyTop + bodyHeight + 4.dp.toPx()
                        )

                        drawRect(
                            brush = bandBrush,
                            topLeft = Offset(xPos, bodyTop - 2.dp.toPx()),
                            size = Size(bandWidth, bodyHeight + 4.dp.toPx())
                        )

                        // Highlight border if selected
                        if (selectedBandIndex == index) {
                            drawRect(
                                color = Color.White,
                                topLeft = Offset(xPos - 2.dp.toPx(), bodyTop - 4.dp.toPx()),
                                size = Size(bandWidth + 4.dp.toPx(), bodyHeight + 8.dp.toPx()),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                            )
                        }
                    }
                }
            }

            // Compact Band Count selector icon button floating in top-right
            if (onBandCountChange != null) {
                Box(
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showBandMenu = true }
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .testTag("resistor_band_count_button"),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        tonalElevation = 2.dp
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Layers,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$bandCount Gelang",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = showBandMenu,
                        onDismissRequest = { showBandMenu = false }
                    ) {
                        listOf(4, 5, 6).forEach { count ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "$count Gelang",
                                        fontWeight = if (bandCount == count) FontWeight.Bold else FontWeight.Normal,
                                        color = if (bandCount == count) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                    )
                                },
                                onClick = {
                                    onBandCountChange(count)
                                    showBandMenu = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
