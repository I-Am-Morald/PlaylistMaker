package com.example.playlistmaker.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.R

val YSDisplayRegular = FontFamily(
    Font(R.font.ys_display_regular, FontWeight.Normal)
)

val YSDisplayMedium = FontFamily(
    Font(R.font.ys_display_medium, FontWeight.Medium)
)

val PlaylistMakerTypography = Typography(

    titleLarge = TextStyle(
        fontFamily = YSDisplayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp
    ),

    titleMedium = TextStyle(
        fontFamily = YSDisplayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp
    ),

    bodyMedium = TextStyle(
        fontFamily = YSDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),

    bodySmall = TextStyle(
        fontFamily = YSDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    ),

    labelLarge = TextStyle(
        fontFamily = YSDisplayMedium,
        fontWeight = FontWeight.Medium,
        fontSize = 19.sp
    ),

    labelMedium = TextStyle(
        fontFamily = YSDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp
    ),
    labelSmall = TextStyle(
        fontFamily = YSDisplayRegular,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    )
)