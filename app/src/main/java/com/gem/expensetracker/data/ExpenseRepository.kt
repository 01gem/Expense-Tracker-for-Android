package com.gem.expensetracker.data

import kotlinx.coroutines.flow.Flow

class ExpenseRepository(private val dao: ExpenseDao) {
    suspend fun insert(expense: Expense) = dao.insert(expense)
    suspend fun update(expense: Expense) = dao.update(expense)
    suspend fun delete(expense: Expense) = dao.delete(expense)
    fun getAll(): Flow<List<Expense>> = dao.getAll()
    fun getByMonth(yearMonth: String): Flow<List<Expense>> = dao.getByMonth(yearMonth)
    fun getMonthlyTotalsForYear(year: String): Flow<List<MonthlyTotal>> = dao.getMonthlyTotalsForYear(year)
    fun getYearTotal(year: String): Flow<Double> = dao.getYearTotal(year)
    fun getAvailableYears(): Flow<List<String>> = dao.getAvailableYears()
    fun getCategoryLeaderboard(): Flow<List<CategoryTotal>> = dao.getCategoryLeaderboard()
}
