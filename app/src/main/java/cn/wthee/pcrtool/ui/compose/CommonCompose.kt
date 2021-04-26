package cn.wthee.pcrtool.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes

/**
 * 蓝底白字
 */
@Composable
fun MainTitleText(text: String, small: Boolean = false, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.onPrimary,
        style = if (small) MaterialTheme.typography.caption else MaterialTheme.typography.body2,
        modifier = modifier
            .background(color = MaterialTheme.colors.primary, shape = Shapes.small)
            .padding(start = Dimen.mediuPadding, end = Dimen.mediuPadding)
    )
}

/**
 * 蓝底白字
 */
@Composable
fun MainContentText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.End
) {
    Text(
        text = text,
        color = MaterialTheme.colors.onBackground,
        textAlign = textAlign,
        style = MaterialTheme.typography.body2,
        modifier = modifier
    )
}

/**
 * 蓝色加粗标题
 */
@Composable
fun MainText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.subtitle1,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(start = Dimen.mediuPadding, end = Dimen.mediuPadding)
    )
}

/**
 * 蓝色加粗标题
 */
@Composable
fun SpaceCompose(modifier: Modifier) {
    Spacer(
        modifier = modifier
            .padding(Dimen.smallPadding)
            .width(Dimen.lineWidth)
            .height(Dimen.lineHeight)
            .background(MaterialTheme.colors.primary)
    )
}


@Composable
fun FabCompose(@DrawableRes iconId: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {

    FloatingActionButton(
        onClick = (onClick),
        backgroundColor = MaterialTheme.colors.background,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        contentColor = MaterialTheme.colors.primary,
        modifier = modifier.size(Dimen.fabSize),
    ) {
        val icon =
            painterResource(iconId)
        Icon(icon, "", modifier = Modifier.padding(Dimen.fabPadding))
    }
}