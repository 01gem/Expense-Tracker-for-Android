package com.gem.expensetracker.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.gem.expensetracker.ui.theme.ExpenseTrackerTheme
import com.gem.expensetracker.ui.theme.categoryColor
import com.gem.expensetracker.util.CurrencyUtils
import com.gem.expensetracker.viewmodel.CategoryBreakdown

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryBreakdownBar(
    breakdown: List<CategoryBreakdown>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Segmented Bar
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp)
                .clip(RoundedCornerShape(6.dp))
        ) {
            var currentX = 0f
            val canvasWidth = size.width

            breakdown.forEach { item ->
                val segmentWidth = (item.percentage * canvasWidth).toFloat()
                drawRect(
                    color = categoryColor(item.category),
                    topLeft = Offset(currentX, 0f),
                    size = Size(segmentWidth, size.height)
                )
                currentX += segmentWidth
            }
            
            // If total percentage is less than 1 (rounding issues or empty), 
            // fill remaining with a neutral color
            if (currentX < canvasWidth) {
                drawRect(
                    color = Color.LightGray.copy(alpha = 0.3f),
                    topLeft = Offset(currentX, 0f),
                    size = Size(canvasWidth - currentX, size.height)
                )
            }
        }

        // Legend
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            breakdown.forEach { item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(categoryColor(item.category))
                    )
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyUtils.formatPeso(item.total),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryBreakdownBarPreview() {
    ExpenseTrackerTheme {
        CategoryBreakdownBar(
            breakdown = listOf(
                CategoryBreakdown("Food", 1500.0, 0.45),
                CategoryBreakdown("Transport", 800.0, 0.24),
                CategoryBreakdown("Bills", 1000.0, 0.31)
            ),
            modifier = Modifier.padding(16.dp)
        )
    }
}
