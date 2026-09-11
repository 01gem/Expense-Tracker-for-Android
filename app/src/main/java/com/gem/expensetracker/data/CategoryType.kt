package com.gem.expensetracker.data

enum class CategoryType(val label: String, val colorHex: Long) {
    FOOD("Food", 0xFFE8A87C),
    TRANSPORT("Transport", 0xFF7CA8E8),
    BILLS("Bills", 0xFFE87CA8),
    SHOPPING("Shopping", 0xFFA87CE8),
    HEALTH("Health", 0xFF7CE8A8),
    ENTERTAINMENT("Entertainment", 0xFFE8D37C),
    OTHER("Other", 0xFF9E9E9E)
}
