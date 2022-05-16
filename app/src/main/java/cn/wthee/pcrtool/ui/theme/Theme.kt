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
    secondary = colorPrimaryDark,
    surfaceVariant = colorAccentDark,
    background = Color.Black,
    onBackground = Color.White
)

private val LightColorPalette = lightColorScheme(
    primary = colorPrimary,
    onPrimary = Color.White,
    primaryContainer = Color.White,
    onPrimaryContainer = colorPrimary,
    secondary = colorPrimary,
    surfaceVariant = colorAccent,
    background = Color.White,
    onBackground = Color.Black
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