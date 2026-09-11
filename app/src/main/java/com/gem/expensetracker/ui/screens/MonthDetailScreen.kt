package com.gem.expensetracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.EventBusy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gem.expensetracker.ui.components.CategoryBreakdownBar
import com.gem.expensetracker.ui.components.EmptyState
import com.gem.expensetracker.ui.components.ExpenseListItem
import com.gem.expensetracker.viewmodel.ExpenseViewModel
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun MonthDetailScreen(
    viewModel: ExpenseViewModel,
    yearMonth: String, // format "yyyy-MM"
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val expenses by viewModel.expensesForMonth(yearMonth).collectAsStateWithLifecycle()
    val breakdown by viewModel.categoryBreakdownForMonth(yearMonth).collectAsStateWithLifecycle()
    
    val totalAmount = expenses.sumOf { it.amount }
    
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
    val title = try {
        YearMonth.parse(yearMonth).format(formatter)
    } catch (_: Exception) {
        yearMonth
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back"
                )
            }
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Total Spending: ₱%,.2f".format(totalAmount),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )

            if (breakdown.isNotEmpty()) {
                CategoryBreakdownBar(breakdown = breakdown)
            }
        }

        if (expenses.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.EventBusy,
                message = "No expenses this month",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(expenses, key = { it.id }) { expense ->
                    ExpenseListItem(
                        expense = expense,
                        onDelete = { viewModel.deleteExpense(expense) }
                    )
                }
            }
        }
    }
}
