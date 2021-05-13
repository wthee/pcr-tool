package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.theme.Dimen

/**
 * 通用悬浮按钮
 */
@Composable
fun FabCompose(iconType: MainIconType, modifier: Modifier = Modifier, onClick: () -> Unit) {

    FloatingActionButton(
        onClick = onClick,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        backgroundColor = MaterialTheme.colors.onPrimary,
        contentColor = MaterialTheme.colors.primary,
        modifier = modifier.size(Dimen.fabSize),
    ) {
        IconCompose(iconType.icon, modifier = Modifier.padding(Dimen.fabPadding))
    }
}

/**
 * 通用展开悬浮按钮
 */
@Composable
fun ExtendedFabCompose(
    modifier: Modifier = Modifier,
    iconType: MainIconType,
    text: String,
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        icon = {
            IconCompose(iconType.icon, modifier = Modifier.size(Dimen.fabIconSize))
        },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.subtitle2,
            )
        },
        onClick = onClick,
        elevation = FloatingActionButtonDefaults.elevation(Dimen.fabElevation),
        backgroundColor = MaterialTheme.colors.onPrimary,
        contentColor = MaterialTheme.colors.primary,
        modifier = modifier.height(Dimen.fabSize)
    )
}
