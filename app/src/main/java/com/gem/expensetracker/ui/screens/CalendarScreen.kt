package com.gem.expensetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gem.expensetracker.ui.components.EmptyState
import com.gem.expensetracker.ui.components.ExpenseListItem
import com.gem.expensetracker.viewmodel.ExpenseViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier
) {
    val phZone = ZoneId.of("Asia/Manila")
    val utcZone = ZoneId.of("UTC")
    
    // DatePickerState expects millis at Midnight UTC
    fun getTodayUtcMillis() = LocalDate.now(phZone)
        .atStartOfDay(utcZone)
        .toInstant()
        .toEpochMilli()

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = getTodayUtcMillis()
    )

    LaunchedEffect(Unit) {
        datePickerState.selectedDateMillis = getTodayUtcMillis()
    }
    
    val selectedDate = remember(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            Instant.ofEpochMilli(it).atZone(utcZone).toLocalDate()
        } ?: LocalDate.now(phZone)
    }

    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    val filteredExpenses = remember(allExpenses, selectedDate) {
        val dateStr = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        allExpenses.filter { it.date == dateStr }
    }

    Column(modifier = modifier.fillMaxSize()) {
        DatePicker(
            state = datePickerState,
            showModeToggle = false,
            title = null,
            headline = null,
            modifier = Modifier.scale(0.9f) // Scale down slightly to fit better
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )

        if (filteredExpenses.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.CalendarToday,
                message = "No expenses for this date",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredExpenses, key = { it.id }) { expense ->
                    ExpenseListItem(
                        expense = expense,
                        onDelete = { viewModel.deleteExpense(expense) },
                        showCategoryColor = false
                    )
                }
            }
        }
    }
}
