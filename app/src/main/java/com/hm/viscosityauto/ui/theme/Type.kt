package com.hm.viscosityauto.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.hm.viscosityauto.R

// Set of Material typography styles to start with
val Typography = Typography(
    headlineLarge =TextStyle(
        fontFamily = FontFamily( Font(R.font.source_han_serif_cn_bold)),
        fontSize = 82.sp,
        color = textColorBlue,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),

    displayLarge = TextStyle(
        fontFamily = FontFamily( Font(R.font.source_han_serif_cn_bold)),
        fontSize = 70.sp,
        brush = Brush.verticalGradient(colors = listOf(buttonStart, buttonEnd)),
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),

    displaySmall = TextStyle(
        fontFamily = FontFamily( Font(R.font.source_han_serif_cn_bold)),
        fontSize = 44.sp,
        color = textColor,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),

    titleLarge = TextStyle(
        fontFamily = FontFamily( Font(R.font.source_han_serif_cn_bold)),
        fontSize = 33.sp,
        color = textColor,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),

    titleMedium = TextStyle(
        fontFamily = FontFamily( Font(R.font.source_han_serif_cn_bold)),
        fontSize = 27.sp,
        color = textColor,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),

    titleSmall = TextStyle(
        fontFamily = FontFamily( Font(R.font.source_han_serif_cn_bold)),
        fontSize = 22.sp,
        color = textColor,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),

    bodyLarge = TextStyle(
        fontFamily = FontFamily( Font(R.font.source_han_serif_cn_semibold)),
        fontSize = 20.sp,
        color = textColor,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),

    bodyMedium = TextStyle(
        fontFamily = FontFamily( Font(R.font.source_han_serif_cn_semibold)),
        fontSize = 17.sp,
        color = textColor,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),

    bodySmall = TextStyle(
        fontFamily = FontFamily( Font(R.font.source_han_serif_cn_semibold)),
        fontSize = 13.sp,
        color = textColor,
        platformStyle = PlatformTextStyle(includeFontPadding = false)
    ),

    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )

)

//
//SourceHanSerifCN-Heavy      76sp
//SourceHanSerifCN-Bold	    33sp  27sp
//SourceHanSansCN-Normal      35sp
//SourceHanSerifCN-SemiBold   22sp  17sp 44sp  20sp
//SourceHanSerifCN-Medium     20sp
//SourceHanSansCN-Regular     14sp
