package cn.wthee.pcrtool.ui.theme

import androidx.compose.material.SwitchDefaults
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


val colorPrimary = Color(0xFF5690EF)
val colorAccent = Color(0xFF91BEF0)
val white = Color(0xFFFFFFFF)

val colorPrimaryDark = Color(0xFF3F6BB3)
val colorAccentDark = Color(0xFF5B84B3)

/**
 * 输入框颜色适配
 */
@Composable
fun outlinedTextFieldColors() = TextFieldDefaults.outlinedTextFieldColors(
    focusedLabelColor = MaterialTheme.colorScheme.primary,
    focusedBorderColor = MaterialTheme.colorScheme.primary,
    cursorColor = MaterialTheme.colorScheme.primary,
)

/**
 * 开关颜色适配
 */
@Composable
fun switchColors() = SwitchDefaults.colors(
    checkedThumbColor = MaterialTheme.colorScheme.primary,
)
