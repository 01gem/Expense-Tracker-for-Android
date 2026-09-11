package com.gem.expensetracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gem.expensetracker.data.MonthlyTotal

@Composable
fun GlowingLineChart(
    monthlyTotals: List<MonthlyTotal>,
    currentMonth: String, // format "yyyy-MM"
    onMonthClick: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val year = currentMonth.take(4)
    val months = (1..12).map { month ->
        val monthStr = "%s-%02d".format(year, month)
        monthlyTotals.find { it.month == monthStr }?.total ?: 0.0
    }

    val maxTotal = months.maxOrNull()?.coerceAtLeast(1.0) ?: 1.0
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(modifier = modifier.padding(16.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        val canvasWidth = size.width
                        val clickedIndex = (offset.x / (canvasWidth / 12)).toInt().coerceIn(0, 11)
                        val clickedMonthStr = "%s-%02d".format(year, clickedIndex + 1)
                        if (months[clickedIndex] > 0) {
                            onMonthClick(clickedMonthStr)
                        }
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val xStep = canvasWidth / 11 // Connect 12 points over 11 intervals

            val points = months.mapIndexed { index, total ->
                val x = index * (canvasWidth / 12) + (canvasWidth / 24) // Center point in the month slot
                val y = canvasHeight - ((total / maxTotal) * (canvasHeight - 20.dp.toPx())).toFloat() - 10.dp.toPx()
                x to y
            }

            // 1. Draw the Glow (Area under the line)
            val glowPath = Path().apply {
                moveTo(points.first().first, canvasHeight)
                points.forEach { (x, y) -> lineTo(x, y) }
                lineTo(points.last().first, canvasHeight)
                close()
            }

            drawPath(
                path = glowPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.4f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = canvasHeight
                )
            )

            // 2. Draw the Main Line
            val linePath = Path().apply {
                val firstPoint = points.first()
                moveTo(firstPoint.first, firstPoint.second)
                points.drop(1).forEach { (x, y) -> lineTo(x, y) }
            }

            drawPath(
                path = linePath,
                color = primaryColor,
                style = Stroke(
                    width = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    join = StrokeJoin.Round
                )
            )
            
            // 3. Highlight Current Month Dot
            val currentIndex = (currentMonth.takeLast(2).toIntOrNull() ?: 1) - 1
            if (currentIndex in points.indices) {
                val currentPoint = points[currentIndex]
                drawCircle(
                    color = primaryColor,
                    radius = 6.dp.toPx(),
                    center = Offset(currentPoint.first, currentPoint.second)
                )
                drawCircle(
                    color = Color.White,
                    radius = 3.dp.toPx(),
                    center = androidx.compose.ui.geometry.Offset(currentPoint.first, currentPoint.second)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val labels = listOf("J", "F", "M", "A", "M", "J", "J", "A", "S", "O", "N", "D")
            labels.forEach { label ->
                Text(
                    text = label,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
