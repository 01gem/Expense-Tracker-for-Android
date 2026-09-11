package com.gem.expensetracker.ui.theme

import androidx.compose.ui.graphics.Color

fun categoryColor(category: String): Color {
    val normalized = category.trim().lowercase()
    val hash = normalized.hashCode()
    val hue = ((hash % 360) + 360) % 360
    return Color.hsv(hue.toFloat(), 0.55f, 0.82f)
}
