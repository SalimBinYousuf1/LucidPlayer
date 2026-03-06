package com.lucid.player.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val LucidTypography = Typography(
    displayLarge  = TextStyle(fontWeight = FontWeight.Black,     fontSize = 56.sp, lineHeight = 64.sp,  letterSpacing = (-1).sp),
    displayMedium = TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 44.sp, lineHeight = 52.sp,  letterSpacing = (-0.5).sp),
    headlineLarge = TextStyle(fontWeight = FontWeight.Bold,      fontSize = 32.sp, lineHeight = 40.sp,  letterSpacing = (-0.3).sp),
    headlineMedium= TextStyle(fontWeight = FontWeight.Bold,      fontSize = 26.sp, lineHeight = 34.sp,  letterSpacing = (-0.2).sp),
    headlineSmall = TextStyle(fontWeight = FontWeight.SemiBold,  fontSize = 22.sp, lineHeight = 30.sp),
    titleLarge    = TextStyle(fontWeight = FontWeight.SemiBold,  fontSize = 20.sp, lineHeight = 28.sp,  letterSpacing = (-0.1).sp),
    titleMedium   = TextStyle(fontWeight = FontWeight.Medium,    fontSize = 16.sp, lineHeight = 24.sp,  letterSpacing = 0.1.sp),
    titleSmall    = TextStyle(fontWeight = FontWeight.Medium,    fontSize = 14.sp, lineHeight = 20.sp,  letterSpacing = 0.1.sp),
    bodyLarge     = TextStyle(fontWeight = FontWeight.Normal,    fontSize = 16.sp, lineHeight = 24.sp,  letterSpacing = 0.15.sp),
    bodyMedium    = TextStyle(fontWeight = FontWeight.Normal,    fontSize = 14.sp, lineHeight = 20.sp,  letterSpacing = 0.1.sp),
    bodySmall     = TextStyle(fontWeight = FontWeight.Normal,    fontSize = 12.sp, lineHeight = 16.sp,  letterSpacing = 0.2.sp),
    labelLarge    = TextStyle(fontWeight = FontWeight.SemiBold,  fontSize = 14.sp, lineHeight = 20.sp,  letterSpacing = 0.1.sp),
    labelMedium   = TextStyle(fontWeight = FontWeight.Medium,    fontSize = 12.sp, lineHeight = 16.sp,  letterSpacing = 0.5.sp),
    labelSmall    = TextStyle(fontWeight = FontWeight.Medium,    fontSize = 11.sp, lineHeight = 16.sp,  letterSpacing = 0.5.sp),
)
