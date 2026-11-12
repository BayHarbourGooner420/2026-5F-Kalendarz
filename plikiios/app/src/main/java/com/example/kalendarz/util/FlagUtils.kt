package com.example.kalendarz.util

object FlagUtils {
    fun getFlagEmoji(countryCode: String): String {
        if (countryCode.length != 2) return "🏳️"
        val code = countryCode.uppercase()
        return try {
            val firstLetter = Character.codePointAt(code, 0) - 0x41 + 0x1F1E6
            val secondLetter = Character.codePointAt(code, 1) - 0x41 + 0x1F1E6
            String(Character.toChars(firstLetter)) + String(Character.toChars(secondLetter))
        } catch (e: Exception) {
            "🏳️"
        }
    }

    fun getCountryName(countryCode: String): String {
        return when (countryCode.uppercase()) {
            "US" -> "USA"
            "DE" -> "Germany"
            "JP" -> "Japan"
            "PL" -> "Poland"
            "IN" -> "India"
            "TR" -> "Turkie"
            "NG" -> "Nigeria"
            "GB" -> "UK"
            "FR" -> "France"
            "IT" -> "Italy"
            "ES" -> "Spain"
            "CA" -> "Canada"
            "BR" -> "Brazil"
            "CN" -> "China"
            else -> countryCode
        }
    }
}
