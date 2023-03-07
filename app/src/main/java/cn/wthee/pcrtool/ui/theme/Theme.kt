package cn.wthee.pcrtool.ui.theme

import android.annotation.SuppressLint
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import cn.wthee.pcrtool.ui.MainActivity

/**
 * 默认暗色主题
 */
private val DarkColorPalette = darkColorScheme(
    primary = colorPrimaryDark,
    onPrimary = colorWhite,
    primaryContainer = colorBlack,
    onPrimaryContainer = colorPrimaryDark,
    secondary = colorPrimaryDark,
    onSecondary = colorBlack,
    secondaryContainer = colorPrimaryDark,
    onSecondaryContainer = colorBlack,
//    tertiary = colorPrimaryDark,
//    onTertiary = colorBlack,
//    tertiaryContainer = colorBlack,
//    onTertiaryContainer = colorPrimaryDark,
    background = colorBlack,
    onBackground = colorWhite,
    surface = colorSurfaceDark,
    onSurface = colorSurface,
    surfaceVariant = colorSurfaceDark,
    onSurfaceVariant = colorSurface,
//    outline = colorGrayDark,
)

/**
 * 默认亮色主题
 */
private val LightColorPalette = lightColorScheme(
    primary = colorPrimary,
    onPrimary = colorWhite,
    primaryContainer = colorWhite,
    onPrimaryContainer = colorPrimary,
    secondary = colorPrimary,
    onSecondary = colorWhite,
    secondaryContainer = colorPrimary,
    onSecondaryContainer = colorWhite,
//    tertiary = colorPrimary,
//    onTertiary = colorWhite,
//    tertiaryContainer = colorWhite,
//    onTertiaryContainer = colorPrimary,
    background = colorWhite,
    onBackground = colorBlack,
    surface = colorSurface,
    onSurface = colorSurfaceDark,
    surfaceVariant = colorSurface,
    onSurfaceVariant = colorSurfaceDark,
//    outline = colorGray,
)

@SuppressLint("NewApi")
@Composable
fun PCRToolComposeTheme(
    shapes: Shapes = MaterialTheme.shapes,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    //启用动态色彩判断
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
        content = content,
        shapes = shapes
    )
}

/**
 * 预览正常、深色模式
 */
@Preview(
    name = "dark theme",
    group = "themes",
    uiMode = UI_MODE_NIGHT_YES,
    showBackground = true
)
@Preview(
    name = "normal",
    group = "themes",
    showBackground = true
)
annotation class CombinedPreviews

/**
 * 预览
 */
@Composable
fun PreviewLayout(content: @Composable () -> Unit) {
    Column {
        //正常主题
        MaterialTheme(if (isSystemInDarkTheme()) DarkColorPalette else LightColorPalette) {
            Column {
                content()
            }
        }
        //动态色彩主题
        MaterialTheme(
            if (isSystemInDarkTheme()) {
                dynamicDarkColorScheme(LocalContext.current)
            } else {
                dynamicLightColorScheme(LocalContext.current)
            }
        ) {
            Column {
                content()
            }
        }
    }

}