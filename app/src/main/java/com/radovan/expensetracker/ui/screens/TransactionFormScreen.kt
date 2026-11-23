package com.radovan.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radovan.expensetracker.data.TransactionEntity
import com.radovan.expensetracker.ui.components.ScreenHeading
import com.radovan.expensetracker.ui.theme.AppAccent
import com.radovan.expensetracker.ui.theme.AppBackground
import com.radovan.expensetracker.ui.theme.AppMuted
import com.radovan.expensetracker.ui.theme.AppRed
import com.radovan.expensetracker.ui.theme.AppSurface
import com.radovan.expensetracker.ui.theme.AppText
import com.radovan.expensetracker.util.formatAmountForInput
import com.radovan.expensetracker.util.formatTransactionDate
import com.radovan.expensetracker.util.parseAmountToCents

private val expenseCategories = listOf(
    "Food", "Transport", "Shopping", "Entertainment", "Bills", "Other"
)

@Composable
fun TransactionFormScreen(
    existing: TransactionEntity?,
    onBack: () -> Unit,
    onSave: (TransactionEntity) -> Unit,
    onDelete: (TransactionEntity) -> Unit
) {
    var title by rememberSaveable(existing?.id) { mutableStateOf(existing?.title.orEmpty()) }
    var amountText by rememberSaveable(existing?.id) {
        mutableStateOf(existing?.let { formatAmountForInput(it.amountCents) }.orEmpty())
    }
    var isIncome by rememberSaveable(existing?.id) { mutableStateOf(existing?.isIncome ?: false) }
    var category by rememberSaveable(existing?.id) {
        mutableStateOf(existing?.category?.takeIf { it in expenseCategories } ?: "Food")
    }
    var menuExpanded by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var validationMessage by remember { mutableStateOf<String?>(null) }

    val parsedAmount = parseAmountToCents(amountText)
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = AppText,
        unfocusedTextColor = AppText,
        focusedBorderColor = AppAccent,
        unfocusedBorderColor = AppMuted,
        focusedLabelColor = AppAccent,
        unfocusedLabelColor = AppMuted,
        cursorColor = AppAccent
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {
        TextButton(onClick = onBack) {
            Text("←  Back", color = AppAccent)
        }

        ScreenHeading(
            title = if (existing == null) "New transaction" else "Edit transaction",
            subtitle = "Keep your money in check"
        )

        Spacer(Modifier.height(2.dp))

        Text("TRANSACTION TYPE", color = AppMuted, fontSize = 10.sp, letterSpacing = 1.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            FilterChip(
                selected = !isIncome,
                onClick = { isIncome = false },
                label = { Text("Expense") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppAccent,
                    selectedLabelColor = AppBackground,
                    labelColor = AppMuted
                )
            )
            FilterChip(
                selected = isIncome,
                onClick = { isIncome = true },
                label = { Text("Income") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppAccent,
                    selectedLabelColor = AppBackground,
                    labelColor = AppMuted
                )
            )
        }

        Text("NAME", color = AppMuted, fontSize = 10.sp, letterSpacing = 1.sp)
        OutlinedTextField(
            value = title,
            onValueChange = { title = it; validationMessage = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("e.g. Weekly groceries") },
            singleLine = true,
            colors = textFieldColors
        )

        Text("AMOUNT", color = AppMuted, fontSize = 10.sp, letterSpacing = 1.sp)
        OutlinedTextField(
            value = amountText,
            onValueChange = { amountText = it; validationMessage = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Amount in Kč") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = textFieldColors
        )

        if (!isIncome) {
            Text("CATEGORY", color = AppMuted, fontSize = 10.sp, letterSpacing = 1.sp)
            androidx.compose.foundation.layout.Box {
                OutlinedButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(13.dp)
                ) {
                    Text(category, color = AppText)
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(AppSurface)
                ) {
                    expenseCategories.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option, color = AppText) },
                            onClick = {
                                category = option
                                menuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Text(
            if (existing == null) "Date: today" else "Date: ${formatTransactionDate(existing.dateMillis)}",
            color = AppMuted,
            fontSize = 12.sp
        )

        if (validationMessage != null) {
            Text(validationMessage!!, color = AppRed, fontSize = 12.sp)
        }

        Button(
            onClick = {
                if (title.isBlank()) {
                    validationMessage = "Please enter a transaction name."
                } else if (parsedAmount == null) {
                    validationMessage = "Enter an amount greater than zero."
                } else {
                    onSave(
                        TransactionEntity(
                            id = existing?.id ?: 0L,
                            title = title.trim(),
                            amountCents = parsedAmount,
                            category = if (isIncome) "Income" else category,
                            isIncome = isIncome,
                            dateMillis = existing?.dateMillis ?: System.currentTimeMillis()
                        )
                    )
                }
            },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppAccent,
                contentColor = AppBackground
            )
        ) {
            Text(if (existing == null) "Save transaction" else "Save changes", fontWeight = FontWeight.Bold)
        }

        if (existing != null) {
            OutlinedButton(
                onClick = { showDeleteConfirmation = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("Delete transaction", color = AppRed)
            }
        }

        Spacer(Modifier.height(12.dp))
    }

    if (showDeleteConfirmation && existing != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            containerColor = AppSurface,
            title = { Text("Delete transaction?", color = AppText) },
            text = { Text("This action cannot be undone.", color = AppMuted) },
            confirmButton = {
                TextButton(onClick = { onDelete(existing) }) {
                    Text("Delete", color = AppRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel", color = AppMuted)
                }
            }
        )
    }
}
