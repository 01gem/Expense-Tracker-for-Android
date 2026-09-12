package com.gem.expensetracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gem.expensetracker.data.MonthlyTotal
import java.time.Month
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun MonthGrid(
    monthlyTotals: List<MonthlyTotal>,
    currentMonth: String, // format "yyyy-MM"
    onMonthClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val year = currentMonth.take(4)
    val monthEntries = (1..12).map { month ->
        val monthStr = "%s-%02d".format(year, month)
        val total = monthlyTotals.find { it.month == monthStr }?.total ?: 0.0
        val label = Month.of(month).getDisplayName(TextStyle.SHORT, Locale.getDefault())
        MonthEntry(monthStr, label, total)
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (row in 0..2) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (col in 0..3) {
                    val index = row * 4 + col
                    val entry = monthEntries[index]
                    val isCurrent = entry.monthStr == currentMonth
                    val hasData = entry.total > 0

                    MonthCell(
                        entry = entry,
                        isCurrent = isCurrent,
                        hasData = hasData,
                        onClick = { if (hasData) onMonthClick(entry.monthStr) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthCell(
    entry: MonthEntry,
    isCurrent: Boolean,
    hasData: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (hasData) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }

    val textColor = if (hasData) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
    }

    val border = if (isCurrent) {
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else null

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .then(if (border != null) Modifier.border(border, RoundedCornerShape(12.dp)) else Modifier)
            .clickable(enabled = hasData, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = entry.label,
            color = textColor,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

private data class MonthEntry(
    val monthStr: String,
    val label: String,
    val total: Double
)
