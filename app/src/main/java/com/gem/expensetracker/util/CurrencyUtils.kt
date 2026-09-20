package com.gem.expensetracker.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Utility for formatting currency consistently across different Android devices.
 * 
 * Using java.text.NumberFormat is more reliable for rendering the Philippine Peso sign (₱)
 * on devices with restrictive system fonts (like some Xiaomi/POCO models) because it 
 * leverages the system's internal localization and font fallback mechanisms.
 */
object CurrencyUtils {
    private val phLocale = Locale("en", "PH")
    private val currencyFormatter = NumberFormat.getCurrencyInstance(phLocale)

    /**
     * Formats a double amount into a Philippine Peso string (e.g., ₱1,234.56).
     */
    fun formatPeso(amount: Double): String {
        return try {
            currencyFormatter.format(amount)
        } catch (e: Exception) {
            // Manual fallback if system formatter fails unexpectedly
            "\u20B1%,.2f".format(amount)
        }
    }

    /**
     * Returns the Unicode Peso symbol (\u20B1).
     * Explicit Unicode escape is sometimes safer for compilers/bundlers than literal ₱.
     */
    fun getPesoSymbol(): String = "\u20B1"
}
