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
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.statusBarsHeight


/**
 * 顶部工具栏
 *
 * @param titleId 字符串资源 id
 * @param scrollState 页面滚动状态
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
            .padding(Dimen.largePadding)
            .fillMaxWidth()
            .offset(y = offset)
    ) {
        Text(
            text = stringResource(id = titleId),
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primary,
            modifier = Modifier.weight(1f)
        )
        IconCompose(
            data = MainIconType.SETTING.icon,
            tint = MaterialTheme.colors.onSurface,
            size = Dimen.fabIconSize
        ) {

        }
    }
}

/**
 * 状态栏
 */
@Composable
fun StatusBarBox(content: @Composable () -> Unit) {
    val statusBarHeight =
        rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.systemBars
        ).calculateTopPadding()
    Box(Modifier.background(MaterialTheme.colors.background)) {
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
 *
 * @param scrollState 页面滚动状态
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
