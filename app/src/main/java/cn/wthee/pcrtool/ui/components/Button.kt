package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.theme.Dimen
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
 */
@Composable
fun SubButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    OutlinedButton(shape = MaterialTheme.shapes.medium,
        modifier = modifier.padding(Dimen.smallPadding),
        border = BorderStroke(Dimen.divLineHeight, color = color),
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
    icon: MainIconType,
    text: String,
    contentColor: Color = MaterialTheme.colorScheme.primary,
    iconSize: Dp = Dimen.textIconSize,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    maxLines: Int = 1,
    onClick: (() -> Unit)? = null
) {
    val context = LocalContext.current

    Row(modifier = modifier
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
        MainIcon(
            data = icon,
            size = iconSize,
            tint = contentColor
        )
        Text(
            text = text,
            color = contentColor,
            style = textStyle,
            modifier = Modifier.padding(start = Dimen.smallPadding),
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}