
package com.radovan.expensetracker.ui

import android.app.Application
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.radovan.expensetracker.ui.screens.ActivityScreen
import com.radovan.expensetracker.ui.screens.HomeScreen
import com.radovan.expensetracker.ui.screens.StatisticsScreen
import com.radovan.expensetracker.ui.screens.TransactionFormScreen
import com.radovan.expensetracker.ui.theme.AppAccent
import com.radovan.expensetracker.ui.theme.AppBackground
import com.radovan.expensetracker.ui.theme.AppMuted
import com.radovan.expensetracker.ui.theme.AppSurface
import com.radovan.expensetracker.viewmodel.ExpenseViewModel

private object Routes {
    const val HOME = "home"
    const val ACTIVITY = "activity"
    const val STATISTICS = "statistics"
    const val TRANSACTION = "transaction/{transactionId}"

    fun transaction(id: String) = "transaction/$id"
}

@Composable
fun ExpenseTrackerApp() {
    val context = LocalContext.current
    val application = context.applicationContext as Application

    val viewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModel.Factory(application)
    )

    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val budgetCents by viewModel.budgetCents.collectAsStateWithLifecycle()

    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomNavigation =
        currentRoute != null && currentRoute != Routes.TRANSACTION

    Scaffold(
        containerColor = AppBackground,
        bottomBar = {
            if (showBottomNavigation) {
                NavigationBar(
                    containerColor = AppSurface
                ) {
                    NavigationBarItem(
                        selected = currentRoute == Routes.HOME,
                        onClick = {
                            navController.navigate(Routes.HOME) {
                                popUpTo(Routes.HOME) {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        },
                        icon = { Text("⌂") },
                        label = { Text("Home") },
                        colors = navigationColors()
                    )

                    NavigationBarItem(
                        selected = currentRoute == Routes.ACTIVITY,
                        onClick = {
                            navController.navigate(Routes.ACTIVITY) {
                                popUpTo(Routes.HOME) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text("↕") },
                        label = { Text("Activity") },
                        colors = navigationColors()
                    )

                    NavigationBarItem(
                        selected = false,
                        onClick = {
                            navController.navigate(
                                Routes.transaction("new")
                            )
                        },
                        icon = {
                            Text("＋", color = AppAccent)
                        },
                        label = { Text("Add") },
                        colors = navigationColors()
                    )

                    NavigationBarItem(
                        selected = currentRoute == Routes.STATISTICS,
                        onClick = {
                            navController.navigate(Routes.STATISTICS) {
                                popUpTo(Routes.HOME) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Text("◉") },
                        label = { Text("Statistics") },
                        colors = navigationColors()
                    )
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {

            composable(Routes.HOME) {
                HomeScreen(
                    transactions = transactions,
                    budgetCents = budgetCents,
                    onBudgetChange = viewModel::updateBudget,
                    onAdd = {
                        navController.navigate(
                            Routes.transaction("new")
                        )
                    },
                    onOpenTransaction = {
                        navController.navigate(
                            Routes.transaction(it.id.toString())
                        )
                    }
                )
            }

            composable(Routes.ACTIVITY) {
                ActivityScreen(
                    transactions = transactions,
                    onOpenTransaction = {
                        navController.navigate(
                            Routes.transaction(it.id.toString())
                        )
                    }
                )
            }

            composable(Routes.STATISTICS) {
                StatisticsScreen(
                    transactions = transactions
                )
            }

            composable(
                route = Routes.TRANSACTION,
                arguments = listOf(
                    navArgument("transactionId") {
                        type = NavType.StringType
                    }
                )
            ) { entry ->

                val transactionId = entry.arguments
                    ?.getString("transactionId")

                val existing = transactionId
                    ?.toLongOrNull()
                    ?.let { id ->
                        transactions.firstOrNull { it.id == id }
                    }

                if (transactionId == "new" || existing != null) {
                    TransactionFormScreen(
                        existing = existing,
                        onBack = {
                            navController.popBackStack()
                        },
                        onSave = { transaction ->
                            if (existing == null) {
                                viewModel.addTransaction(
                                    title = transaction.title,
                                    amountCents = transaction.amountCents,
                                    category = transaction.category,
                                    isIncome = transaction.isIncome,
                                    dateMillis = transaction.dateMillis
                                )
                            } else {
                                viewModel.updateTransaction(transaction)
                            }

                            navController.popBackStack()
                        },
                        onDelete = { transaction ->
                            viewModel.deleteTransaction(transaction)
                            navController.popBackStack()
                        }
                    )
                } else {
                    Text(
                        "Loading transaction…",
                        color = AppMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun navigationColors() =
    NavigationBarItemDefaults.colors(
        selectedIconColor = AppAccent,
        selectedTextColor = AppAccent,
        unselectedIconColor = AppMuted,
        unselectedTextColor = AppMuted,
        indicatorColor = AppBackground
    )
