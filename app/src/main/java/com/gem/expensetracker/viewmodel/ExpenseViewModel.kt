package com.gem.expensetracker.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gem.expensetracker.data.*
import com.gem.expensetracker.export.ExcelExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Year

data class CategoryBreakdown(
    val category: String,
    val total: Double,
    val percentage: Double
)

enum class LeaderboardType { CATEGORY, YEAR, MONTH }

@OptIn(ExperimentalCoroutinesApi::class)
class ExpenseViewModel(private val repository: ExpenseRepository) : ViewModel() {

    private val monthlyExpensesCache = mutableMapOf<String, StateFlow<List<Expense>>>()
    private val categoryBreakdownCache = mutableMapOf<String, StateFlow<List<CategoryBreakdown>>>()

    val selectedYear = MutableStateFlow(Year.now().toString())

    val yearTotal: StateFlow<Double> = selectedYear
        .flatMapLatest { year -> repository.getYearTotal(year) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val monthlyTotals: StateFlow<List<MonthlyTotal>> = selectedYear
        .flatMapLatest { year -> repository.getMonthlyTotalsForYear(year) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableYears: StateFlow<List<String>> = repository.getAvailableYears()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf(Year.now().toString()))

    val allExpenses: StateFlow<List<Expense>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Leaderboard state
    val leaderboardType = MutableStateFlow(LeaderboardType.CATEGORY)
    val leaderboardYear = MutableStateFlow(Year.now().toString())

    val categoryLeaderboard: StateFlow<List<CategoryTotal>> = leaderboardYear.flatMapLatest { year ->
        if (year == "ALL") repository.getCategoryLeaderboard()
        else repository.getCategoryLeaderboardForYear(year)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val yearlyLeaderboard: StateFlow<List<YearTotal>> = repository.getYearlyLeaderboard()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val monthLeaderboard: StateFlow<List<MonthlyTotal>> = leaderboardYear.flatMapLatest { year ->
        val actualYear = if (year == "ALL") Year.now().toString() else year
        repository.getMonthlyTotalsForYear(actualYear)
    }.map { it.sortedByDescending { total -> total.total } }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun selectYear(year: String) {
        selectedYear.value = year
    }

    fun selectLeaderboardType(type: LeaderboardType) {
        leaderboardType.value = type
        if (type == LeaderboardType.MONTH && leaderboardYear.value == "ALL") {
            leaderboardYear.value = Year.now().toString()
        }
    }

    fun selectLeaderboardYear(year: String) {
        leaderboardYear.value = year
    }

    fun addExpense(amount: Double, category: String, note: String, date: String) {
        viewModelScope.launch {
            repository.insert(
                Expense(
                    amount = amount,
                    category = category,
                    note = note,
                    date = date
                )
            )
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.delete(expense)
        }
    }

    fun expensesForMonth(yearMonth: String): StateFlow<List<Expense>> {
        return monthlyExpensesCache.getOrPut(yearMonth) {
            repository.getByMonth(yearMonth)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        }
    }

    fun categoryBreakdownForMonth(yearMonth: String): StateFlow<List<CategoryBreakdown>> {
        return categoryBreakdownCache.getOrPut(yearMonth) {
            expensesForMonth(yearMonth)
                .map { expenses ->
                    val totalMonthAmount = expenses.sumOf { it.amount }
                    if (totalMonthAmount == 0.0) return@map emptyList()

                    expenses.groupBy { it.category }
                        .map { (categoryName, categoryExpenses) ->
                            val categoryTotal = categoryExpenses.sumOf { it.amount }
                            CategoryBreakdown(
                                category = categoryName,
                                total = categoryTotal,
                                percentage = categoryTotal / totalMonthAmount
                            )
                        }
                        .sortedByDescending { it.total }
                }
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        }
    }

    suspend fun exportToExcel(context: Context): Result<String> {
        val allExpenses = repository.getAll().first()
        return withContext(Dispatchers.IO) { ExcelExporter.export(context, allExpenses) }
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val database = ExpenseDatabase.getInstance(context)
            val repository = ExpenseRepository(database.expenseDao())
            return ExpenseViewModel(repository) as T
        }
    }
}
