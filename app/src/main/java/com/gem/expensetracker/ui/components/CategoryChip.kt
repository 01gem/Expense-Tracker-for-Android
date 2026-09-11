package com.gem.expensetracker.ui.components

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.gem.expensetracker.data.CategoryType

@Composable
fun CategoryChip(
    category: CategoryType,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = Color(category.colorHex)
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(category.label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = categoryColor.copy(alpha = 0.2f),
            selectedLabelColor = categoryColor,
            selectedLeadingIconColor = categoryColor
        ),
        modifier = modifier
    )
}
