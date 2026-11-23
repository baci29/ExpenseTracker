package com.radovan.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.radovan.expensetracker.data.ExpenseDatabase
import com.radovan.expensetracker.data.TransactionEntity
import com.radovan.expensetracker.data.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    private val preferences = application.getSharedPreferences(
        "expense_tracker",
        Application.MODE_PRIVATE
    )

    private val repository = TransactionRepository(
        ExpenseDatabase.getInstance(application).transactionDao()
    )

    val transactions: StateFlow<List<TransactionEntity>> =
        repository.observeTransactions().stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _budgetCents = MutableStateFlow(
        preferences.getLong(KEY_BUDGET_CENTS, DEFAULT_BUDGET_CENTS)
    )
    val budgetCents: StateFlow<Long> = _budgetCents

    init {
        migrateLegacyPreferences()
    }

    fun addTransaction(
        title: String,
        amountCents: Long,
        category: String,
        isIncome: Boolean,
        dateMillis: Long = System.currentTimeMillis()
    ) {
        viewModelScope.launch {
            repository.add(
                TransactionEntity(
                    title = title,
                    amountCents = amountCents,
                    category = category,
                    isIncome = isIncome,
                    dateMillis = dateMillis
                )
            )
        }
    }

    fun updateTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.update(transaction)
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.delete(transaction)
        }
    }

    fun updateBudget(amountCents: Long) {
        if (amountCents <= 0L) return
        _budgetCents.value = amountCents
        preferences.edit().putLong(KEY_BUDGET_CENTS, amountCents).apply()
    }

    private fun migrateLegacyPreferences() {
        viewModelScope.launch(Dispatchers.IO) {
            if (preferences.getBoolean(KEY_MIGRATION_DONE, false)) return@launch

            if (repository.count() == 0) {
                val json = preferences.getString("transactions", null)
                if (!json.isNullOrBlank()) {
                    val imported = runCatching {
                        val array = JSONArray(json)
                        buildList {
                            for (index in 0 until array.length()) {
                                val item = array.optJSONObject(index) ?: continue
                                val title = item.optString("name").trim()
                                val amount = item.optDouble("amount", 0.0)
                                if (title.isBlank() || amount <= 0.0) continue

                                val cents = BigDecimal.valueOf(amount)
                                    .setScale(2, RoundingMode.HALF_UP)
                                    .movePointRight(2)
                                    .longValueExact()

                                val isIncome = item.optBoolean("isIncome", false)
                                val oldCategory = item.optString("category", "Other")
                                val category = normalizeLegacyCategory(oldCategory, isIncome)
                                val dateMillis = parseLegacyDate(item.optString("date"))

                                add(
                                    TransactionEntity(
                                        title = title,
                                        amountCents = cents,
                                        category = category,
                                        isIncome = isIncome,
                                        dateMillis = dateMillis
                                    )
                                )
                            }
                        }
                    }.getOrDefault(emptyList())

                    if (imported.isNotEmpty()) repository.addAll(imported)
                }
            }

            preferences.edit().putBoolean(KEY_MIGRATION_DONE, true).apply()
        }
    }

    private fun normalizeLegacyCategory(value: String, isIncome: Boolean): String {
        if (isIncome) return "Income"
        val clean = value.substringBefore(" · ").trim()
        return when (clean.lowercase(Locale.ROOT)) {
            "groceries", "food" -> "Food"
            "transport" -> "Transport"
            "shopping" -> "Shopping"
            "entertainment" -> "Entertainment"
            "bills" -> "Bills"
            "other", "expense" -> "Other"
            else -> clean.ifBlank { "Other" }
        }
    }

    private fun parseLegacyDate(value: String): Long {
        if (value.isBlank()) return System.currentTimeMillis()
        return runCatching {
            SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).parse(value)?.time
        }.getOrNull() ?: System.currentTimeMillis()
    }

    class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
                return ExpenseViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        private const val KEY_MIGRATION_DONE = "room_migration_done"
        private const val KEY_BUDGET_CENTS = "monthly_budget_cents"
        private const val DEFAULT_BUDGET_CENTS = 2_500_000L
    }
}
