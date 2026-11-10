package dev.techietitan.raahsaathi.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Scale from docs/09 section 4.2. Sizes are sp so they follow the system font setting.
// No letter spacing anywhere: tracking breaks Indic conjuncts.
// TODO(1.x): add roughly +10% line height for Indic scripts (body 18/30 sp or more).
private val Display = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 40.sp,
    lineHeight = 48.sp,
    letterSpacing = 0.sp,
)
private val Headline = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Bold,
    fontSize = 28.sp,
    lineHeight = 36.sp,
    letterSpacing = 0.sp,
)
private val Title = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 22.sp,
    lineHeight = 30.sp,
    letterSpacing = 0.sp,
)
private val Body = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Normal,
    fontSize = 18.sp,
    lineHeight = 28.sp,
    letterSpacing = 0.sp,
)
private val Label = TextStyle(
    fontFamily = FontFamily.Default,
    fontWeight = FontWeight.Medium,
    fontSize = 16.sp,
    lineHeight = 24.sp,
    letterSpacing = 0.sp,
)

// Material roles are mapped onto the five app roles; essential text is never below 16sp.
val Typography = Typography(
    displayLarge = Display,
    displayMedium = Display,
    displaySmall = Display,
    headlineLarge = Headline,
    headlineMedium = Headline,
    headlineSmall = Headline,
    titleLarge = Title,
    titleMedium = Title,
    titleSmall = Label,
    bodyLarge = Body,
    bodyMedium = Body,
    bodySmall = Label,
    labelLarge = Label,
    labelMedium = Label,
    labelSmall = Label.copy(fontSize = 14.sp, lineHeight = 20.sp),
)
