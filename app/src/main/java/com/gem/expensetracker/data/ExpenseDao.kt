package com.gem.expensetracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

data class MonthlyTotal(val month: String, val total: Double)
data class CategoryTotal(val category: String, val total: Double)
data class YearTotal(val year: String, val total: Double)

@Dao
interface ExpenseDao {
    @Insert
    suspend fun insert(expense: Expense)

    @Delete
    suspend fun delete(expense: Expense)

    @Query("SELECT * FROM expenses ORDER BY date DESC")
    fun getAll(): Flow<List<Expense>>

    @Query("SELECT * FROM expenses WHERE substr(date,1,7) = :yearMonth ORDER BY date DESC")
    fun getByMonth(yearMonth: String): Flow<List<Expense>>

    @Query("SELECT substr(date,1,7) AS month, SUM(amount) AS total FROM expenses WHERE substr(date,1,4) = :year GROUP BY month ORDER BY month")
    fun getMonthlyTotalsForYear(year: String): Flow<List<MonthlyTotal>>

    @Query("SELECT COALESCE(SUM(amount),0) FROM expenses WHERE substr(date,1,4) = :year")
    fun getYearTotal(year: String): Flow<Double>

    @Query("SELECT DISTINCT substr(date,1,4) AS year FROM expenses ORDER BY year DESC")
    fun getAvailableYears(): Flow<List<String>>

    @Query("SELECT category, SUM(amount) AS total FROM expenses GROUP BY category ORDER BY total DESC")
    fun getCategoryLeaderboard(): Flow<List<CategoryTotal>>

    @Query("SELECT category, SUM(amount) AS total FROM expenses WHERE substr(date,1,4) = :year GROUP BY category ORDER BY total DESC")
    fun getCategoryLeaderboardForYear(year: String): Flow<List<CategoryTotal>>

    @Query("SELECT substr(date,1,4) AS year, SUM(amount) AS total FROM expenses GROUP BY year ORDER BY total DESC")
    fun getYearlyLeaderboard(): Flow<List<YearTotal>>
}
