package com.radovan.expensetracker.data

import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val dao: TransactionDao
) {
    fun observeTransactions(): Flow<List<TransactionEntity>> = dao.observeAll()

    suspend fun count(): Int = dao.count()

    suspend fun add(transaction: TransactionEntity) = dao.insert(transaction)

    suspend fun addAll(transactions: List<TransactionEntity>) = dao.insertAll(transactions)

    suspend fun update(transaction: TransactionEntity) = dao.update(transaction)

    suspend fun delete(transaction: TransactionEntity) = dao.delete(transaction)
}
