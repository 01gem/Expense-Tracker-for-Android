package com.gem.expensetracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.gem.expensetracker.ui.theme.FintechAccent
import com.gem.expensetracker.ui.theme.FintechBlack
import com.gem.expensetracker.ui.theme.FintechWhite

enum class NavigationTab {
    HOME, CALENDAR, ADD, LEADERBOARD, EXPORT
}

@Composable
fun AppNavigationBar(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        color = FintechBlack,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            NavigationItem(
                icon = Icons.Default.Home,
                selected = selectedTab == NavigationTab.HOME,
                onClick = { onTabSelected(NavigationTab.HOME) },
                modifier = Modifier.weight(1f)
            )
            NavigationItem(
                icon = Icons.Default.DateRange,
                selected = selectedTab == NavigationTab.CALENDAR,
                onClick = { onTabSelected(NavigationTab.CALENDAR) },
                modifier = Modifier.weight(1f)
            )
            
            // Highlighted Center Button
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    onClick = { onTabSelected(NavigationTab.ADD) },
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    color = FintechAccent,
                    shadowElevation = 4.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Expense",
                            tint = FintechBlack,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            NavigationItem(
                icon = Icons.Default.BarChart,
                selected = selectedTab == NavigationTab.LEADERBOARD,
                onClick = { onTabSelected(NavigationTab.LEADERBOARD) },
                modifier = Modifier.weight(1f)
            )
            NavigationItem(
                icon = Icons.Default.FileDownload,
                selected = selectedTab == NavigationTab.EXPORT,
                onClick = { onTabSelected(NavigationTab.EXPORT) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NavigationItem(
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (selected) FintechWhite else FintechWhite.copy(alpha = 0.4f),
            modifier = Modifier.size(28.dp)
        )
    }
}
