package cn.wthee.pcrtool.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = colorPrimaryDark,
    primaryVariant = colorAccentDark,
    secondary = colorPrimaryDark,
)

private val LightColorPalette = lightColors(
    primary = colorPrimary,
    primaryVariant = colorAccentDark,
    secondary = colorPrimary,
)

@Composable
fun PcrtoolcomposeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}