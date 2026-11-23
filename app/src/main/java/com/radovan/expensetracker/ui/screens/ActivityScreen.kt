package com.radovan.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radovan.expensetracker.data.TransactionEntity
import com.radovan.expensetracker.ui.components.EmptyState
import com.radovan.expensetracker.ui.components.ScreenHeading
import com.radovan.expensetracker.ui.components.TransactionFilterChips
import com.radovan.expensetracker.ui.components.TransactionRow
import com.radovan.expensetracker.ui.theme.AppBackground
import com.radovan.expensetracker.ui.theme.AppMuted
import com.radovan.expensetracker.ui.theme.AppText
import com.radovan.expensetracker.ui.theme.AppAccent

@Composable
fun ActivityScreen(
    transactions: List<TransactionEntity>,
    onOpenTransaction: (TransactionEntity) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("All") }

    val filtered = transactions.filter { transaction ->
        val matchesType = when (filter) {
            "Income" -> transaction.isIncome
            "Expenses" -> !transaction.isIncome
            else -> true
        }
        val matchesQuery = transaction.title.contains(query, ignoreCase = true) ||
            transaction.category.contains(query, ignoreCase = true)
        matchesType && matchesQuery
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 22.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        item {
            ScreenHeading("Activity", "${transactions.size} saved transactions")
        }
        item {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search transactions") },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = AppText,
                    unfocusedTextColor = AppText,
                    focusedBorderColor = AppAccent,
                    unfocusedBorderColor = AppMuted,
                    focusedLabelColor = AppAccent,
                    unfocusedLabelColor = AppMuted,
                    cursorColor = AppAccent
                )
            )
        }
        item {
            TransactionFilterChips(
                selected = filter,
                onSelected = { filter = it }
            )
        }
        item {
            Text(
                "${filtered.size} results",
                color = AppMuted,
                fontSize = 11.sp
            )
        }
        if (filtered.isEmpty()) {
            item {
                EmptyState(
                    title = "Nothing here yet",
                    message = if (query.isBlank()) "Add a transaction to get started." else "Try a different search or filter."
                )
            }
        } else {
            items(filtered, key = { it.id }) { transaction ->
                TransactionRow(transaction) { onOpenTransaction(transaction) }
            }
        }
    }
}
