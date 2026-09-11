package com.gem.expensetracker.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FintechColorScheme = darkColorScheme(
    primary = FintechAccent,
    onPrimary = FintechBlack,
    primaryContainer = FintechDarkGray,
    onPrimaryContainer = FintechAccent,
    
    secondary = FintechAccent,
    onSecondary = FintechBlack,
    
    background = FintechBlack,
    onBackground = FintechWhite,
    
    surface = FintechDarkGray,
    onSurface = FintechWhite,
    onSurfaceVariant = FintechLightGray,
    
    error = FintechError,
    onError = FintechBlack,
    
    outline = FintechLightGray.copy(alpha = 0.5f)
)

@Composable
fun ExpenseTrackerTheme(
    // We ignore darkTheme and dynamicColor to force the strict Fintech dark look
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Force the FintechColorScheme regardless of system settings
    val colorScheme = FintechColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
