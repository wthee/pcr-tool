package cn.wthee.pcrtool.ui.theme

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorPalette = darkColorScheme(
    primary = colorPrimaryDark,
    secondary = colorPrimaryDark,
    surfaceVariant = colorAccentDark,
    surface = colorPrimaryDark,
    background = Color.Black
)

private val LightColorPalette = lightColorScheme(
    primary = colorPrimary,
    secondary = colorPrimary,
    surfaceVariant = colorAccent,
    surface = colorPrimary,
    background = Color.White
)

@SuppressLint("NewApi")
@Composable
fun PCRToolComposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
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