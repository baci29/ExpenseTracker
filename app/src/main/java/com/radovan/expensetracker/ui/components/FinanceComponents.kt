package com.radovan.expensetracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.radovan.expensetracker.data.TransactionEntity
import com.radovan.expensetracker.ui.theme.AppAccent
import com.radovan.expensetracker.ui.theme.AppBackground
import com.radovan.expensetracker.ui.theme.AppMuted
import com.radovan.expensetracker.ui.theme.AppRed
import com.radovan.expensetracker.ui.theme.AppSurface
import com.radovan.expensetracker.ui.theme.AppText
import com.radovan.expensetracker.util.formatMoney
import com.radovan.expensetracker.util.formatTransactionDate

@Composable
fun FinanceCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AppSurface, RoundedCornerShape(22.dp))
            .padding(20.dp),
        content = { content() }
    )
}

@Composable
fun ScreenHeading(
    title: String,
    subtitle: String? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            color = AppText,
            fontSize = 27.sp,
            fontWeight = FontWeight.Bold
        )
        if (subtitle != null) {
            Text(subtitle, color = AppMuted, fontSize = 13.sp)
        }
    }
}

@Composable
fun TransactionRow(
    transaction: TransactionEntity,
    onClick: () -> Unit
) {
    val icon = when (transaction.category) {
        "Food" -> "🛒"
        "Transport" -> "🚗"
        "Shopping" -> "🛍"
        "Entertainment" -> "▶"
        "Bills" -> "⌂"
        "Income" -> "↗"
        else -> "•"
    }
    val amountColor = if (transaction.isIncome) AppAccent else AppText
    val amountPrefix = if (transaction.isIncome) "+ " else "− "

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppSurface, RoundedCornerShape(17.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(AppSurface.copy(alpha = 0.9f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(icon, fontSize = 18.sp)
        }
        Spacer(Modifier.width(13.dp))
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(
                transaction.title,
                color = AppText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                "${transaction.category} · ${formatTransactionDate(transaction.dateMillis)}",
                color = AppMuted,
                fontSize = 10.sp
            )
        }
        Text(
            amountPrefix + formatMoney(transaction.amountCents),
            color = amountColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun TransactionFilterChips(
    selected: String,
    onSelected: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("All", "Income", "Expenses").forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelected(option) },
                label = { Text(option) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = AppAccent,
                    selectedLabelColor = AppBackground,
                    labelColor = AppMuted
                )
            )
        }
    }
}

@Composable
fun EmptyState(
    title: String,
    message: String
) {
    FinanceCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("＋", color = AppAccent, fontSize = 30.sp)
            Text(title, color = AppText, fontWeight = FontWeight.SemiBold)
            Text(message, color = AppMuted, fontSize = 12.sp)
        }
    }
}
