package com.gem.expensetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gem.expensetracker.ui.components.EmptyState
import com.gem.expensetracker.ui.theme.FintechAccent
import com.gem.expensetracker.ui.theme.FintechDarkGray
import com.gem.expensetracker.util.CurrencyUtils
import com.gem.expensetracker.viewmodel.ExpenseViewModel
import com.gem.expensetracker.viewmodel.LeaderboardType
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier
) {
    val leaderboardType by viewModel.leaderboardType.collectAsStateWithLifecycle()
    val leaderboardYear by viewModel.leaderboardYear.collectAsStateWithLifecycle()
    val availableYears by viewModel.availableYears.collectAsStateWithLifecycle()
    
    val categoryLeaderboard by viewModel.categoryLeaderboard.collectAsStateWithLifecycle()
    val yearlyLeaderboard by viewModel.yearlyLeaderboard.collectAsStateWithLifecycle()
    val monthLeaderboard by viewModel.monthLeaderboard.collectAsStateWithLifecycle()

    val emptyMessage = when (leaderboardType) {
        LeaderboardType.CATEGORY -> "No categories logged yet"
        LeaderboardType.YEAR -> "No years logged yet"
        LeaderboardType.MONTH -> "No expenses this year"
    }

    val isEmpty = when (leaderboardType) {
        LeaderboardType.CATEGORY -> categoryLeaderboard.isEmpty()
        LeaderboardType.YEAR -> yearlyLeaderboard.isEmpty()
        LeaderboardType.MONTH -> monthLeaderboard.isEmpty()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            LeaderboardType.entries.forEachIndexed { index, type ->
                SegmentedButton(
                    selected = leaderboardType == type,
                    onClick = { viewModel.selectLeaderboardType(type) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = LeaderboardType.entries.size),
                    icon = {}
                ) {
                    Text(type.name.lowercase().replaceFirstChar { it.uppercase() })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (leaderboardType == LeaderboardType.CATEGORY || leaderboardType == LeaderboardType.MONTH) {
            YearDropdown(
                selectedYear = leaderboardYear,
                availableYears = availableYears,
                onYearSelected = { viewModel.selectLeaderboardYear(it) },
                showAllTime = leaderboardType == LeaderboardType.CATEGORY
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (isEmpty) {
            EmptyState(
                icon = Icons.Default.BarChart,
                message = emptyMessage,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (leaderboardType) {
                    LeaderboardType.CATEGORY -> {
                        itemsIndexed(categoryLeaderboard) { index, entry ->
                            LeaderboardItem(
                                rank = index + 1,
                                label = entry.category.lowercase().replaceFirstChar { it.uppercase() },
                                amount = entry.total
                            )
                        }
                    }
                    LeaderboardType.YEAR -> {
                        itemsIndexed(yearlyLeaderboard) { index, entry ->
                            LeaderboardItem(
                                rank = index + 1,
                                label = entry.year,
                                amount = entry.total
                            )
                        }
                    }
                    LeaderboardType.MONTH -> {
                        val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())
                        itemsIndexed(monthLeaderboard) { index, entry ->
                            val label = try {
                                YearMonth.parse(entry.month).format(monthFormatter)
                            } catch (_: Exception) {
                                entry.month
                            }
                            LeaderboardItem(
                                rank = index + 1,
                                label = label,
                                amount = entry.total
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun YearDropdown(
    selectedYear: String,
    availableYears: List<String>,
    onYearSelected: (String) -> Unit,
    showAllTime: Boolean
) {
    var expanded by remember { mutableStateOf(false) }
    
    val displayText = if (selectedYear == "ALL") "All Time" else selectedYear

    Box {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.wrapContentSize()
        ) {
            Text(displayText)
            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (showAllTime) {
                DropdownMenuItem(
                    text = { Text("All Time") },
                    onClick = {
                        onYearSelected("ALL")
                        expanded = false
                    }
                )
            }
            availableYears.forEach { year ->
                DropdownMenuItem(
                    text = { Text(year) },
                    onClick = {
                        onYearSelected(year)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun LeaderboardItem(
    rank: Int,
    label: String,
    amount: Double
) {
    val isTopThree = rank <= 3
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = FintechDarkGray
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isTopThree) FintechAccent else MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(40.dp)
                )
                
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Text(
                text = CurrencyUtils.formatPeso(amount),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = if (isTopThree) FintechAccent else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
