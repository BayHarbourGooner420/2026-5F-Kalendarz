package com.example.kalendarz.util

import com.example.kalendarz.R

data class ThemeConfig(
    val key: String,
    val name: String,
    val primaryColor: Long,
    val headerColor: Long,
    val backgroundColor: Long,
    val cardColor: Long,
    val textColor: Long,
    val accentColor: Long,
    val topDecorationResId: Int?,
    val bottomDecorationResId: Int?,
    val iconResId: Int
)

object ThemeManager {

    val THEMES = mapOf(
        "gregorian" to ThemeConfig(
            key = "gregorian",
            name = "Gregoriański",
            primaryColor = 0xFF00695CL,
            headerColor = 0xFF00695CL,
            backgroundColor = 0xFFE0F2F1L,
            cardColor = 0xFFFFFFFFL,
            textColor = 0xFF004D40L,
            accentColor = 0xFF26A69AL,
            topDecorationResId = null,
            bottomDecorationResId = null,
            iconResId = R.drawable.ic_calendar
        ),
        "islamic" to ThemeConfig(
            key = "islamic",
            name = "Islamski (Hijri)",
            primaryColor = 0xFF9E7A31L,
            headerColor = 0xFF9E7A31L,
            backgroundColor = 0xFFFDFBF7L,
            cardColor = 0xFFFFFFFFL,
            textColor = 0xFF5D4037L,
            accentColor = 0xFFD4AF37L,
            topDecorationResId = R.drawable.bg_oriental_top,
            bottomDecorationResId = R.drawable.bg_oriental_bottom,
            iconResId = R.drawable.ic_islamic
        ),
        "jewish" to ThemeConfig(
            key = "jewish",
            name = "Żydowski",
            primaryColor = 0xFF5E35B1L,
            headerColor = 0xFF5E35B1L,
            backgroundColor = 0xFFF3E5F5L,
            cardColor = 0xFFFFFFFFL,
            textColor = 0xFF311B92L,
            accentColor = 0xFF7E57C2L,
            topDecorationResId = null,
            bottomDecorationResId = null,
            iconResId = R.drawable.ic_jewish
        ),
        "hindu" to ThemeConfig(
            key = "hindu",
            name = "Hinduski (Saka)",
            primaryColor = 0xFF8E24AAL,
            headerColor = 0xFF8E24AAL,
            backgroundColor = 0xFFF3E5F5L,
            cardColor = 0xFFFFFFFFL,
            textColor = 0xFF4A148CL,
            accentColor = 0xFFAB47BCL,
            topDecorationResId = null,
            bottomDecorationResId = null,
            iconResId = R.drawable.ic_hindu
        ),
        "chinese" to ThemeConfig(
            key = "chinese",
            name = "Chiński",
            primaryColor = 0xFFC62828L,
            headerColor = 0xFFC62828L,
            backgroundColor = 0xFFFFEBEEL,
            cardColor = 0xFFFFFFFFL,
            textColor = 0xFF880E4FL,
            accentColor = 0xFFEF5350L,
            topDecorationResId = null,
            bottomDecorationResId = null,
            iconResId = R.drawable.ic_chinese
        )
    )

    fun getTheme(key: String): ThemeConfig {
        return THEMES[key] ?: THEMES["gregorian"]!!
    }
}
