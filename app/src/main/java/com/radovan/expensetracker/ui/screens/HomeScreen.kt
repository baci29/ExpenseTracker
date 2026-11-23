
package com.radovan.expensetracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radovan.expensetracker.data.TransactionEntity
import com.radovan.expensetracker.ui.components.EmptyState
import com.radovan.expensetracker.ui.components.FinanceCard
import com.radovan.expensetracker.ui.components.ScreenHeading
import com.radovan.expensetracker.ui.components.TransactionRow
import com.radovan.expensetracker.ui.theme.AppAccent
import com.radovan.expensetracker.ui.theme.AppBackground
import com.radovan.expensetracker.ui.theme.AppMuted
import com.radovan.expensetracker.ui.theme.AppRed
import com.radovan.expensetracker.ui.theme.AppSurface
import com.radovan.expensetracker.ui.theme.AppText
import com.radovan.expensetracker.util.calculateBudgetProgress
import com.radovan.expensetracker.util.formatMoney
import com.radovan.expensetracker.util.isInCurrentMonth
import com.radovan.expensetracker.util.parseAmountToCents

@Composable
fun HomeScreen(
    transactions: List<TransactionEntity>,
    budgetCents: Long,
    onBudgetChange: (Long) -> Unit,
    onAdd: () -> Unit,
    onOpenTransaction: (TransactionEntity) -> Unit
) {
    val totalBalance = transactions.sumOf {
        if (it.isIncome) it.amountCents else -it.amountCents
    }

    val thisMonth = transactions.filter {
        isInCurrentMonth(it.dateMillis)
    }

    val monthIncome = thisMonth
        .filter { it.isIncome }
        .sumOf { it.amountCents }

    val monthExpenses = thisMonth
        .filter { !it.isIncome }
        .sumOf { it.amountCents }

    var showBudgetDialog by rememberSaveable {
        mutableStateOf(false)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppBackground),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = 22.dp,
            end = 20.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        "GOOD TO SEE YOU",
                        color = AppMuted,
                        fontSize = 10.sp,
                        letterSpacing = 1.5.sp
                    )

                    ScreenHeading(
                        "Your finances",
                        "A clearer view of your money"
                    )
                }

                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(
                            AppSurface,
                            RoundedCornerShape(50)
                        )
                        .padding(
                            horizontal = 15.dp,
                            vertical = 11.dp
                        )
                ) {
                    Text(
                        "R",
                        color = AppAccent,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                }
            }
        }

        item {
            FinanceCard {
                Text(
                    "TOTAL BALANCE",
                    color = AppMuted,
                    fontSize = 10.sp,
                    letterSpacing = 1.4.sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    formatMoney(totalBalance),
                    color = AppText,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    MetricBlock(
                        "INCOME THIS MONTH",
                        formatMoney(monthIncome),
                        AppAccent,
                        Modifier.weight(1f)
                    )

                    MetricBlock(
                        "SPENT THIS MONTH",
                        formatMoney(monthExpenses),
                        AppRed,
                        Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            FinanceCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "MONTHLY BUDGET",
                        color = AppMuted,
                        fontSize = 10.sp,
                        letterSpacing = 1.2.sp
                    )

                    TextButton(
                        onClick = { showBudgetDialog = true }
                    ) {
                        Text("Edit", color = AppAccent)
                    }
                }

                val progress = calculateBudgetProgress(
                    monthExpenses,
                    budgetCents
                )

                Text(
                    "${formatMoney(monthExpenses)} of ${formatMoney(budgetCents)}",
                    color = AppText,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(
                            Color(0xFF343A33),
                            RoundedCornerShape(20.dp)
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(8.dp)
                            .background(
                                AppAccent,
                                RoundedCornerShape(20.dp)
                            )
                    )
                }

                Spacer(Modifier.height(8.dp))

                val remaining = budgetCents - monthExpenses

                Text(
                    if (remaining >= 0) {
                        "${formatMoney(remaining)} remaining"
                    } else {
                        "Budget exceeded by ${formatMoney(-remaining)}"
                    },
                    color = if (remaining >= 0) AppMuted else AppRed,
                    fontSize = 12.sp
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Recent transactions",
                    color = AppText,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "LATEST",
                    color = AppMuted,
                    fontSize = 10.sp,
                    letterSpacing = 1.sp
                )
            }
        }

        if (transactions.isEmpty()) {
            item {
                EmptyState(
                    title = "Your story starts here",
                    message = "Add your first income or expense to see your overview."
                )
            }
        } else {
            items(
                transactions.take(5),
                key = { it.id }
            ) { transaction ->
                TransactionRow(transaction) {
                    onOpenTransaction(transaction)
                }
            }
        }

        item {
            Button(
                onClick = onAdd,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppAccent,
                    contentColor = AppBackground
                ),
                shape = RoundedCornerShape(15.dp)
            ) {
                Text(
                    "＋  Add transaction",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showBudgetDialog) {
        BudgetDialog(
            currentBudgetCents = budgetCents,
            onDismiss = {
                showBudgetDialog = false
            },
            onSave = {
                onBudgetChange(it)
                showBudgetDialog = false
            }
        )
    }
}

@Composable
private fun MetricBlock(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            label,
            color = AppMuted,
            fontSize = 9.sp,
            letterSpacing = 0.5.sp
        )

        Spacer(Modifier.height(5.dp))

        Text(
            value,
            color = color,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun BudgetDialog(
    currentBudgetCents: Long,
    onDismiss: () -> Unit,
    onSave: (Long) -> Unit
) {
    var value by rememberSaveable {
        mutableStateOf(
            (currentBudgetCents / 100.0).toString()
        )
    }

    val parsed = parseAmountToCents(value)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppSurface,
        title = {
            Text(
                "Monthly budget",
                color = AppText
            )
        },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = {
                    Text("Budget in Kč")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
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
        },
        confirmButton = {
            TextButton(
                onClick = {
                    parsed?.let(onSave)
                },
                enabled = parsed != null
            ) {
                Text(
                    "Save",
                    color = AppAccent
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    "Cancel",
                    color = AppMuted
                )
            }
        }
    )
}
