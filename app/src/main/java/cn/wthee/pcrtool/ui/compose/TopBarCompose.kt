package cn.wthee.pcrtool.ui.compose

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.statusBarsHeight
import com.google.accompanist.insets.toPaddingValues


/**
 * 顶部工具栏
 */
@Composable
fun TopBarCompose(
    @StringRes titleId: Int = R.string.app_name,
    scrollState: LazyListState
) {
    var offset: Dp
    if (scrollState.firstVisibleItemIndex == 0) {
        offset = -scrollState.firstVisibleItemScrollOffset.dp
        if (offset > Dimen.topBarHeight) offset = -Dimen.topBarHeight
    } else {
        offset = -Dimen.topBarHeight
    }

    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = Dimen.smallPadding)
            .fillMaxWidth()
            .offset(y = offset)
    ) {
        IconCompose(R.mipmap.ic_launcher, size = Dimen.topBarIconSize, fade = true)
        Text(
            text = stringResource(id = titleId),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.padding(start = Dimen.smallPadding)
        )
    }
}

//状态栏
@Composable
fun StatusBarBox(content: @Composable () -> Unit) {
    val statusBarHeight =
        LocalWindowInsets.current.systemBars.toPaddingValues().calculateTopPadding()
    Box(Modifier.background(MaterialTheme.colors.primary)) {
        Card(
            shape = CardTopShape,
            content = content,
            modifier = Modifier.padding(top = statusBarHeight)
        )
        Spacer(
            Modifier
                .fillMaxWidth()
                .statusBarsHeight()
        )
    }
}

/**
 * 距离屏幕顶部的距离
 */
@Composable
fun marginTopBar(scrollState: LazyListState): Dp {
    var marginTop: Dp

    if (scrollState.firstVisibleItemIndex == 0) {
        marginTop =
            Dimen.topBarHeight - scrollState.firstVisibleItemScrollOffset.dp - 8.dp
        if (marginTop < 0.dp) marginTop = 0.dp
    } else {
        marginTop = 0.dp
    }
    return marginTop
}
