package com.gem.expensetracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val category: String, // Stores CategoryType.name
    val note: String = "",
    val date: String, // ISO format "yyyy-MM-dd"
    val createdAt: Long = System.currentTimeMillis()
)
