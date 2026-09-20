package com.gem.expensetracker.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gem.expensetracker.ui.theme.FintechAccent
import com.gem.expensetracker.ui.theme.FintechWhite
import com.gem.expensetracker.util.CurrencyUtils
import com.gem.expensetracker.viewmodel.ExpenseViewModel
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun AddExpenseScreen(
    viewModel: ExpenseViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val phZone = ZoneId.of("Asia/Manila")
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now(phZone)) }
    
    val context = LocalContext.current
    val dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.getDefault())

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onDone) {
                Icon(Icons.Default.Close, contentDescription = "Cancel")
            }
            Text(
                text = "Add Expense",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        OutlinedTextField(
            value = amount,
            onValueChange = { if (it.isEmpty() || it.toDoubleOrNull() != null) amount = it },
            label = { Text("Amount") },
            prefix = { Text(CurrencyUtils.getPesoSymbol()) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        OutlinedTextField(
            value = category,
            onValueChange = { category = it },
            label = { Text("Category (e.g., Subscriptions, Hardware)") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = FintechAccent,
                cursorColor = FintechAccent,
                focusedLabelColor = FintechAccent,
                unfocusedTextColor = FintechWhite,
                focusedTextColor = FintechWhite
            ),
            singleLine = true
        )

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("Item Name / Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Date: ${date.format(dateFormatter)}",
                style = MaterialTheme.typography.bodyLarge
            )
            OutlinedButton(onClick = {
                DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        date = LocalDate.of(year, month + 1, dayOfMonth)
                    },
                    date.year,
                    date.monthValue - 1,
                    date.dayOfMonth
                ).show()
            }) {
                Icon(Icons.Default.DateRange, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Change Date")
            }
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val amountVal = amount.toDoubleOrNull() ?: 0.0
                val trimmedCategory = category.trim()
                val sanitizedCategory = if (trimmedCategory.isBlank()) {
                    "Uncategorized"
                } else {
                    trimmedCategory.lowercase().replaceFirstChar { it.uppercase() }
                }

                viewModel.addExpense(
                    amount = amountVal,
                    category = sanitizedCategory,
                    note = note,
                    date = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                )
                onDone()
            },
            enabled = (amount.toDoubleOrNull() ?: 0.0) > 0.0,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Expense")
        }
    }
}
