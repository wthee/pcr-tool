package cn.wthee.pcrtool.ui.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import cn.wthee.pcrtool.ui.MainActivity

private val DarkColorPalette = darkColorScheme(
    primary = colorPrimaryDark,
    onPrimary = Color.White,
    primaryContainer = Color.Black,
    onPrimaryContainer = colorPrimaryDark,
//    inversePrimary = ColorDarkTokens.InversePrimary,
    secondary = colorPrimaryDark,
    onSecondary = Color.Black,
    secondaryContainer = colorPrimaryDark,
    onSecondaryContainer = Color.Black,
//    tertiary = ColorDarkTokens.Tertiary,
//    onTertiary = ColorDarkTokens.OnTertiary,
//    tertiaryContainer = ColorDarkTokens.TertiaryContainer,
//    onTertiaryContainer = ColorDarkTokens.OnTertiaryContainer,
    background = Color.Black,
    onBackground = Color.White,
    surface = colorSurfaceDark,
    onSurface = colorSurface,
    surfaceVariant = colorSurfaceDark,
    onSurfaceVariant = colorSurface,
//    inverseSurface = Color.White,
//    inverseOnSurface = Color.Black,
    outline = colorGrayDark,
)

private val LightColorPalette = lightColorScheme(
    primary = colorPrimary,
    onPrimary = Color.White,
    primaryContainer = Color.White,
    onPrimaryContainer = colorPrimary,
//    inversePrimary = ColorDarkTokens.InversePrimary,
    secondary = colorPrimary,
    onSecondary = Color.White,
    secondaryContainer = colorPrimary,
    onSecondaryContainer = Color.White,
//    tertiary = ColorDarkTokens.Tertiary,
//    onTertiary = ColorDarkTokens.OnTertiary,
//    tertiaryContainer = ColorDarkTokens.TertiaryContainer,
//    onTertiaryContainer = ColorDarkTokens.OnTertiaryContainer,
    background = Color.White,
    onBackground = Color.Black,
    surface = colorSurface,
    onSurface = colorSurfaceDark,
    surfaceVariant = colorSurface,
    onSurfaceVariant = colorSurfaceDark,
//    inverseSurface = Color.Black,
//    inverseOnSurface = Color.White,
    outline = colorGray,
)

@SuppressLint("NewApi")
@Composable
fun PCRToolComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val dynamicColor =
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && MainActivity.dynamicColorOnFlag

    val colorScheme = when {
        dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
        darkTheme -> DarkColorPalette
        else -> LightColorPalette
    }
    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}