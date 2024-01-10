package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.utils.VibrateUtil


/**
 * 主操作按钮
 */
@Composable
fun MainButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = colorWhite,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Button(
        shape = MaterialTheme.shapes.medium,
        modifier = modifier.padding(Dimen.smallPadding),
        onClick = {
            VibrateUtil(context).single()
            onClick()
        },
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        Text(
            text = text, color = color, style = textStyle
        )
    }
}

/**
 * 次操作按钮
 *
 * @param useBrush 是否使用渐变
 */
@Composable
fun SubButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    useBrush: Boolean = false,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    OutlinedButton(shape = MaterialTheme.shapes.medium,
        modifier = modifier.padding(Dimen.smallPadding),
        border = if (useBrush) {
            BorderStroke(
                width = Dimen.smallStrokeWidth,
                brush = Brush.linearGradient(
                    colors = listOf(
                        color,
                        color.copy(alpha = 0.5f),
                        color.copy(alpha = 0.5f),
                        color,
                    )
                )
            )
        } else {
            BorderStroke(width = Dimen.smallStrokeWidth, color = color)
        },
        onClick = {
            VibrateUtil(context).single()
            onClick()
        }) {
        Text(text = text, color = color, style = textStyle)
    }
}

/**
 * 带图标按钮
 */
@Composable
fun IconTextButton(
    modifier: Modifier = Modifier,
    icon: MainIconType? = null,
    text: String,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    iconSize: Dp = Dimen.textIconSize,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    maxLines: Int = 1,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(enabled = onClick != null) {
                VibrateUtil(context).single()
                if (onClick != null) {
                    onClick()
                }
            }
            .padding(Dimen.smallPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            MainIcon(
                data = icon,
                size = iconSize,
                tint = contentColor
            )
        }

        Text(
            text = text,
            color = contentColor,
            style = textStyle,
            modifier = Modifier.padding(
                start = Dimen.smallPadding,
                top = Dimen.exSmallPadding,
                bottom = Dimen.exSmallPadding
            ),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@CombinedPreviews
@Composable
fun Preview() {
    PreviewLayout {
        val text = stringResource(id = R.string.debug_short_text)
        MainButton(text = text) {}
        SubButton(text = text, color = MaterialTheme.colorScheme.primary, useBrush = false) {}
        SubButton(text = text, color = MaterialTheme.colorScheme.primary, useBrush = true) {}
        IconTextButton(text = text, icon = MainIconType.MAIN) {}
    }
}
