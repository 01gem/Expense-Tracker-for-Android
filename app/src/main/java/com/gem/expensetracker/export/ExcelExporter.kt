package com.gem.expensetracker.export

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import com.gem.expensetracker.data.CategoryType
import com.gem.expensetracker.data.Expense
import org.dhatim.fastexcel.Workbook
import java.io.OutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object ExcelExporter {

    fun export(context: Context, expenses: List<Expense>): Result<String> {
        if (expenses.isEmpty()) {
            return Result.success("No expenses to export")
        }

        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        val filename = "expense_tracker_$timestamp.xlsx"

        return try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/ExpenseTracker")
            }

            val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                ?: return Result.failure(Exception("Failed to create MediaStore entry"))

            val outputStream: OutputStream = context.contentResolver.openOutputStream(uri)
                ?: return Result.failure(Exception("Failed to open output stream"))

            outputStream.use { os ->
                val wb = Workbook(os, "Expense Tracker", "1.0")
                
                // Sheet 1: Expenses
                val wsExpenses = wb.newWorksheet("Expenses")
                val headers = listOf("Date", "Category", "Note", "Amount")
                
                headers.forEachIndexed { col, header ->
                    wsExpenses.value(0, col, header)
                    wsExpenses.style(0, col).bold().set()
                }

                val sortedExpenses = expenses.sortedBy { it.date }
                sortedExpenses.forEachIndexed { index, expense ->
                    val row = index + 1
                    wsExpenses.value(row, 0, expense.date)
                    wsExpenses.value(row, 1, expense.category)
                    wsExpenses.value(row, 2, expense.note)
                    wsExpenses.value(row, 3, expense.amount)
                    wsExpenses.style(row, 3).format("#,##0.00").set()
                }

                // Sheet 2: Monthly Summary
                val wsSummary = wb.newWorksheet("Monthly Summary")
                wsSummary.value(0, 0, "Month")
                wsSummary.style(0, 0).bold().set()
                wsSummary.value(0, 1, "Total")
                wsSummary.style(0, 1).bold().set()

                val monthlySummary = expenses
                    .groupBy { it.date.substring(0, 7) }
                    .mapValues { it.value.sumOf { exp -> exp.amount } }
                    .toSortedMap()

                var summaryRow = 1
                monthlySummary.forEach { (month, total) ->
                    wsSummary.value(summaryRow, 0, month)
                    wsSummary.value(summaryRow, 1, total)
                    wsSummary.style(summaryRow, 1).format("#,##0.00").set()
                    summaryRow++
                }

                wb.finish()
            }

            Result.success("Saved to Downloads/ExpenseTracker/$filename")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
