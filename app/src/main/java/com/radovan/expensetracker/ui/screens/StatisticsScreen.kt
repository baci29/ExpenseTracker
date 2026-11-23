package com.radovan.expensetracker.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radovan.expensetracker.data.TransactionEntity
import com.radovan.expensetracker.ui.components.EmptyState
import com.radovan.expensetracker.ui.components.FinanceCard
import com.radovan.expensetracker.ui.components.ScreenHeading
import com.radovan.expensetracker.ui.theme.AppAccent
import com.radovan.expensetracker.ui.theme.AppMuted
import com.radovan.expensetracker.ui.theme.AppRed
import com.radovan.expensetracker.ui.theme.AppSurface
import com.radovan.expensetracker.ui.theme.AppText
import com.radovan.expensetracker.util.formatMoney
import com.radovan.expensetracker.util.isInCurrentMonth

private val chartColors = listOf(
    Color(0xFFB5F36B),
    Color(0xFF76B9A0),
    Color(0xFF7396D1),
    Color(0xFFC49BEA),
    Color(0xFFE5B66A),
    Color(0xFFDB8585)
)

@Composable
fun StatisticsScreen(transactions: List<TransactionEntity>) {
    val monthTransactions = transactions.filter { isInCurrentMonth(it.dateMillis) }
    val income = monthTransactions.filter { it.isIncome }.sumOf { it.amountCents }
    val expenses = monthTransactions.filter { !it.isIncome }.sumOf { it.amountCents }
    val categoryTotals = monthTransactions
        .filter { !it.isIncome }
        .groupBy { it.category }
        .mapValues { (_, items) -> items.sumOf { it.amountCents } }
        .toList()
        .sortedByDescending { it.second }
    val totalExpenses = categoryTotals.sumOf { it.second }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF101110))
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        ScreenHeading("Statistics", "Your spending this month")

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            FinanceCard(modifier = Modifier.weight(1f)) {
                Text("INCOME", color = AppMuted, fontSize = 10.sp, letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))
                Text(formatMoney(income), color = AppAccent, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
            FinanceCard(modifier = Modifier.weight(1f)) {
                Text("EXPENSES", color = AppMuted, fontSize = 10.sp, letterSpacing = 1.sp)
                Spacer(Modifier.height(8.dp))
                Text(formatMoney(expenses), color = AppRed, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }

        FinanceCard {
            Text(
                "Spending by category",
                color = AppText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(20.dp))

            if (totalExpenses == 0L) {
                EmptyState(
                    title = "No spending recorded",
                    message = "Your category breakdown will appear here."
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(190.dp)) {
                        var startAngle = -90f
                        val stroke = 27.dp.toPx()
                        categoryTotals.forEachIndexed { index, item ->
                            val sweep = item.second.toFloat() / totalExpenses.toFloat() * 360f
                            drawArc(
                                color = chartColors[index % chartColors.size],
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                style = Stroke(width = stroke, cap = StrokeCap.Butt)
                            )
                            startAngle += sweep
                        }
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("TOTAL SPENT", color = AppMuted, fontSize = 9.sp, letterSpacing = 1.sp)
                        Text(formatMoney(totalExpenses), color = AppText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(20.dp))

                categoryTotals.forEachIndexed { index, item ->
                    val fraction = item.second.toFloat() / totalExpenses.toFloat()
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 7.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(9.dp)
                                        .background(chartColors[index % chartColors.size], CircleShape)
                                )
                                Spacer(Modifier.size(9.dp))
                                Text(item.first, color = AppText, fontSize = 13.sp)
                            }
                            Text(formatMoney(item.second), color = AppMuted, fontSize = 12.sp)
                        }
                        Spacer(Modifier.height(7.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .background(Color(0xFF343A33), RoundedCornerShape(20.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(fraction)
                                    .height(6.dp)
                                    .background(chartColors[index % chartColors.size], RoundedCornerShape(20.dp))
                            )
                        }
                    }
                }
            }
        }

        FinanceCard {
            Text("Monthly summary", color = AppText, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            SummaryLine("Income", formatMoney(income), AppAccent)
            SummaryLine("Expenses", formatMoney(expenses), AppRed)
            SummaryLine("Net result", formatMoney(income - expenses), AppText)
        }
    }
}

@Composable
private fun SummaryLine(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = AppMuted, fontSize = 13.sp)
        Text(value, color = color, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}
