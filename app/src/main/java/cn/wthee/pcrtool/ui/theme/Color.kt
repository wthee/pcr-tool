package cn.wthee.pcrtool.ui.theme

import androidx.compose.ui.graphics.Color
import cn.wthee.pcrtool.BuildConfig

val colorPrimary = if (!BuildConfig.DEBUG) Color(0xFF5690EF) else Color(0xFFD85280)
val colorPrimaryDark = if (!BuildConfig.DEBUG) Color(0xFF3F6BB3) else Color(0xFFB93E69)

//黑白
val colorWhite = Color(0xFFFFFCFE)
val colorBlack = Color(0xFF0D0F0F)


//outline
val colorGray = Color(0xFF9C9C9C)

//遮罩颜色
const val maskAlpha = 0.7f
const val maskStartAlpha = 0.3f

//其它颜色
/**
 * 浅蓝、Rank 1
 */
val colorBlue = Color(0xFFB9E3F3)

/**
 * 铜、Rank 2 ~ 3
 */
val colorCopper = Color(0xFFC28662)

/**
 * 银、Rank 4 ~ 6
 */
val colorSilver = Color(0xFFB9C8DA)

/**
 * 金、Rank 7 ~ 10
 */
val colorGold = Color(0xFFEBA827)

/**
 * 紫、Rank 11 ~ 17
 */
val colorPurple = Color(0xFFb86ada)

/**
 * 红、Rank 18 ~ 20
 */
val colorRed = Color(0xFFe65d49)

/**
 * 绿、Rank 21 ~ 23
 */
val colorGreen = Color(0xFF5EB56D)

/**
 * 橙、Rank 24 ~ 27
 */
val colorOrange = Color(0xFFFF883E)

/**
 * 青、Rank 28 ~ 31
 */
val colorCyan = Color(0xFF65B0FA)

/**
 * 粉、ex装备4星、Rank 32 ~ ？
 */
val colorPink = Color(0xFFFFB6C1)