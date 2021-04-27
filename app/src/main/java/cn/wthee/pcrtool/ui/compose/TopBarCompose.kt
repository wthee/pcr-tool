package cn.wthee.pcrtool.ui.compose

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.Dimen


@Composable
fun TopBarCompose(
    @DrawableRes iconId: Int = R.mipmap.ic_logo,
    @StringRes titleId: Int = R.string.app_name,
    scrollState: LazyListState
) {
    var offset = 0.dp
    if (scrollState.firstVisibleItemIndex == 0) {
        offset = -scrollState.firstVisibleItemScrollOffset.dp
        if (offset > Dimen.topBarHeight) offset = Dimen.topBarHeight
    } else {
        offset = Dimen.topBarHeight
    }
    TopAppBar(
        backgroundColor = MaterialTheme.colors.primary,
        modifier = Modifier
            .offset(y = offset)
            .height(Dimen.topBarHeight)
    ) {
        Icon(painter = painterResource(id = iconId), contentDescription = null)
        Text(
            text = stringResource(id = titleId),
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onPrimary
        )
    }
}