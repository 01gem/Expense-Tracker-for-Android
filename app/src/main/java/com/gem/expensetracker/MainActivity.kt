package com.gem.expensetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gem.expensetracker.ui.components.AppNavigationBar
import com.gem.expensetracker.ui.components.NavigationTab
import com.gem.expensetracker.ui.screens.AddExpenseScreen
import com.gem.expensetracker.ui.screens.CalendarScreen
import com.gem.expensetracker.ui.screens.DashboardScreen
import com.gem.expensetracker.ui.screens.ExportScreen
import com.gem.expensetracker.ui.screens.LeaderboardScreen
import com.gem.expensetracker.ui.screens.MonthDetailScreen
import com.gem.expensetracker.ui.theme.ExpenseTrackerTheme
import com.gem.expensetracker.viewmodel.ExpenseViewModel

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExpenseTrackerTheme {
                val viewModel: ExpenseViewModel = viewModel(
                    factory = ExpenseViewModel.Factory(LocalContext.current)
                )
                var selectedTab by remember { mutableStateOf(NavigationTab.HOME) }
                var detailMonth by remember { mutableStateOf<String?>(null) }

                val topBarTitle = when (selectedTab) {
                    NavigationTab.HOME -> if (detailMonth != null) "Month Details" else "Gem's Expense Tracker"
                    NavigationTab.CALENDAR -> "Calendar"
                    NavigationTab.ADD -> "Add Expense"
                    NavigationTab.LEADERBOARD -> "Leaderboard"
                    NavigationTab.EXPORT -> "Export Data"
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        Surface(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .statusBarsPadding(),
                            shape = RoundedCornerShape(24.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            tonalElevation = 4.dp,
                            shadowElevation = 8.dp
                        ) {
                            CenterAlignedTopAppBar(
                                title = {
                                    Text(
                                        topBarTitle,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                },
                                colors = TopAppBarDefaults.topAppBarColors(
                                    containerColor = Color.Transparent,
                                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                )
                            )
                        }
                    },
                    bottomBar = {
                        AppNavigationBar(
                            selectedTab = selectedTab,
                            onTabSelected = { 
                                selectedTab = it
                                detailMonth = null // Reset detail when switching tabs
                            }
                        )
                    }
                ) { innerPadding ->
                    when (selectedTab) {
                        NavigationTab.HOME -> {
                            if (detailMonth != null) {
                                MonthDetailScreen(
                                    viewModel = viewModel,
                                    yearMonth = detailMonth!!,
                                    onBack = { detailMonth = null },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            } else {
                                DashboardScreen(
                                    viewModel = viewModel,
                                    onMonthClick = { detailMonth = it },
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                        NavigationTab.ADD -> {
                            AddExpenseScreen(
                                viewModel = viewModel,
                                onDone = { selectedTab = NavigationTab.HOME },
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        NavigationTab.EXPORT -> {
                            ExportScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        NavigationTab.CALENDAR -> {
                            CalendarScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                        NavigationTab.LEADERBOARD -> {
                            LeaderboardScreen(
                                viewModel = viewModel,
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}
