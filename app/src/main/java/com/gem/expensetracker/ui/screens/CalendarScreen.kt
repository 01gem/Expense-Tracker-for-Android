package com.gem.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gem.expensetracker.ui.components.EmptyState
import com.gem.expensetracker.ui.components.ExpenseListItem
import com.gem.expensetracker.ui.theme.FintechAccent
import com.gem.expensetracker.ui.theme.FintechBlack
import com.gem.expensetracker.ui.theme.FintechDarkGray
import com.gem.expensetracker.viewmodel.ExpenseViewModel
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun CalendarScreen(
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier
) {
    val phZone = ZoneId.of("Asia/Manila")
    val availableYears by viewModel.availableYears.collectAsStateWithLifecycle()
    val allExpenses by viewModel.allExpenses.collectAsStateWithLifecycle()
    
    var selectedDate by remember { mutableStateOf(LocalDate.now(phZone)) }
    var viewingMonth by remember { mutableStateOf(YearMonth.now(phZone)) }
    var showJumpDialog by remember { mutableStateOf(false) }

    val filteredExpenses = remember(allExpenses, selectedDate) {
        val dateStr = selectedDate.format(DateTimeFormatter.ISO_LOCAL_DATE)
        allExpenses.filter { it.date == dateStr }
    }

    // Days in current viewing month that have entries
    val daysWithEntries = remember(allExpenses, viewingMonth) {
        allExpenses
            .filter { it.date.startsWith(viewingMonth.toString()) }
            .map { LocalDate.parse(it.date).dayOfMonth }
            .toSet()
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Jump Navigation Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedButton(
                onClick = { showJumpDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.primary
                )
            ) {
                val label = "${viewingMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${viewingMonth.year}"
                Text(label, fontWeight = FontWeight.Bold)
            }
        }

        // Custom Fintech Calendar Grid
        FintechCalendarGrid(
            viewingMonth = viewingMonth,
            selectedDate = selectedDate,
            daysWithEntries = daysWithEntries,
            onDateSelected = { selectedDate = it },
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
        )

        if (filteredExpenses.isEmpty()) {
            EmptyState(
                icon = Icons.Outlined.CalendarToday,
                message = "No expenses for ${if (selectedDate == LocalDate.now(phZone)) "today" else selectedDate.format(DateTimeFormatter.ofPattern("MMM dd"))}",
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

    if (showJumpDialog) {
        JumpSequentialDialog(
            availableYears = availableYears,
            onJump = { year, month ->
                viewingMonth = YearMonth.of(year, month)
                showJumpDialog = false
            },
            onReset = {
                val today = LocalDate.now(phZone)
                selectedDate = today
                viewingMonth = YearMonth.now(phZone)
                showJumpDialog = false
            },
            onDismiss = { showJumpDialog = false }
        )
    }
}

@Composable
private fun FintechCalendarGrid(
    viewingMonth: YearMonth,
    selectedDate: LocalDate,
    daysWithEntries: Set<Int>,
    onDateSelected: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val daysInMonth = viewingMonth.lengthOfMonth()
    val firstDayOfWeek = viewingMonth.atDay(1).dayOfWeek.value % 7 // Sunday = 0
    val totalSlots = (daysInMonth + firstDayOfWeek + 6) / 7 * 7

    Column(modifier = modifier) {
        // Weekday Headers
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Grid Rows
        for (row in 0 until (totalSlots / 7)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0 until 7) {
                    val slotIndex = row * 7 + col
                    val dayNum = slotIndex - firstDayOfWeek + 1
                    
                    Box(modifier = Modifier.weight(1f).aspectRatio(1f), contentAlignment = Alignment.Center) {
                        if (dayNum in 1..daysInMonth) {
                            val date = viewingMonth.atDay(dayNum)
                            val isSelected = date == selectedDate
                            val hasEntry = daysWithEntries.contains(dayNum)

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) FintechAccent else Color.Transparent)
                                    .clickable { onDateSelected(date) }
                            ) {
                                Text(
                                    text = dayNum.toString(),
                                    color = if (isSelected) FintechBlack else MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                                // Entry Indicator Dot
                                if (hasEntry) {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 2.dp)
                                            .size(4.dp)
                                            .clip(CircleShape)
                                            .background(if (isSelected) FintechBlack else FintechAccent)
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(6.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JumpSequentialDialog(
    availableYears: List<String>,
    onJump: (Int, Int) -> Unit,
    onReset: () -> Unit,
    onDismiss: () -> Unit
) {
    var selectedYear by remember { mutableStateOf<Int?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(containerColor = FintechDarkGray)
        ) {
            Box(modifier = Modifier.padding(16.dp)) {
                IconButton(
                    onClick = onReset,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Reset to Today", tint = MaterialTheme.colorScheme.outline)
                }

                Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (selectedYear == null) "Select Year" else "Select Month",
                        style = MaterialTheme.typography.headlineSmall,
                        color = FintechAccent,
                        fontWeight = FontWeight.Black
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    if (selectedYear == null) {
                        Box(modifier = Modifier.heightIn(max = 300.dp)) {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(availableYears) { yearStr ->
                                    SelectionItem(label = yearStr) {
                                        selectedYear = yearStr.toIntOrNull()
                                    }
                                }
                            }
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val months = (1..12).chunked(3)
                            months.forEach { rowMonths ->
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    rowMonths.forEach { m ->
                                        val name = Month.of(m).getDisplayName(TextStyle.SHORT, Locale.getDefault())
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .aspectRatio(1.2f)
                                                .clip(MaterialTheme.shapes.medium)
                                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                                .clickable { onJump(selectedYear!!, m) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(name, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

@Composable
private fun SelectionItem(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(16.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
