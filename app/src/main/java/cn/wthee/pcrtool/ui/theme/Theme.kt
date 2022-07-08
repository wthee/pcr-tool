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
    onPrimary = Color.Black,
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
    surface = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Color.Black,
    onSurfaceVariant = Color.White,
//    surfaceTint = primary,
    inverseSurface = Color.White,
    inverseOnSurface = Color.Black,
//    error = ColorDarkTokens.Error,
//    onError = ColorDarkTokens.OnError,
//    errorContainer = ColorDarkTokens.ErrorContainer,
//    onErrorContainer = ColorDarkTokens.OnErrorContainer,
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
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Color.White,
    onSurfaceVariant = Color.Black,
//    surfaceTint = primary,
    inverseSurface = Color.Black,
    inverseOnSurface = Color.White,
//    error = ColorDarkTokens.Error,
//    onError = ColorDarkTokens.OnError,
//    errorContainer = ColorDarkTokens.ErrorContainer,
//    onErrorContainer = ColorDarkTokens.OnErrorContainer,
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