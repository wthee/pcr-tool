package cn.wthee.pcrtool.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.ui.theme.Dimen


/**
 * 通用悬浮按钮
 */
@Composable
fun FabCompose(@DrawableRes iconId: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {

    FloatingActionButton(
        onClick = onClick,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        backgroundColor = MaterialTheme.colors.onPrimary,
        contentColor = MaterialTheme.colors.primary,
        modifier = modifier.size(Dimen.fabSize),
    ) {
        val icon =
            painterResource(iconId)
        Icon(icon, "", modifier = Modifier.padding(Dimen.fabPadding))
    }
}

/**
 * 通用展开悬浮按钮
 */
@Composable
fun ExtendedFabCompose(
    modifier: Modifier = Modifier,
    icon: Painter,
    text: String,
    textWidth: Dp = Dimen.getWordWidth(2f),
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        icon = { Icon(icon, null, modifier = Modifier.size(Dimen.fabIconSize)) },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.requiredWidth(textWidth)
            )
        },
        onClick = onClick,
        elevation = FloatingActionButtonDefaults.elevation(Dimen.fabElevation),
        backgroundColor = MaterialTheme.colors.onPrimary,
        contentColor = MaterialTheme.colors.primary,
        modifier = modifier.height(Dimen.fabSize)
    )
}
