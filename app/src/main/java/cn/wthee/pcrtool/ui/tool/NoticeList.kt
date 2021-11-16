package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.AppNotice
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.openWebView
import cn.wthee.pcrtool.viewmodel.NoticeViewModel
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.launch

/**
 * 通知列表
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun NoticeList(
    scrollState: LazyListState,
    noticeViewModel: NoticeViewModel = hiltViewModel()
) {
    val noticeList = noticeViewModel.getNotice().collectAsState(initial = arrayListOf()).value
    val coroutineScope = rememberCoroutineScope()

    val updateApp = noticeViewModel.updateApp.observeAsState().value ?: false
    val icon = if (updateApp == 1) MainIconType.APP_UPDATE else MainIconType.NOTICE


    Box(modifier = Modifier.fillMaxSize()) {
        val visible = noticeList.isNotEmpty()
        LazyVerticalGrid(state = scrollState, cells = GridCells.Adaptive(getItemWidth())) {
            if (visible) {
                items(noticeList) {
                    NoticeItem(it)
                }
            } else {
                items(12) {
                    NoticeItem(AppNotice())
                }
            }
            //查看更多
            item {
                NoticeItem(
                    AppNotice(
                        "",
                        "",
                        -2,
                        "",
                        message = stringResource(R.string.visit_more_log),
                        title = stringResource(R.string.update_log),
                        -1,
                        stringResource(R.string.readme)
                    )
                )
            }
            item {
                CommonSpacer()
            }
        }
        //回到顶部
        FabCompose(
            iconType = icon,
            text = stringResource(id = R.string.app_notice),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                try {
                    scrollState.scrollToItem(0)
                } catch (e: Exception) {
                }
            }
        }
    }


}


/**
 * 通知
 */
@ExperimentalMaterialApi
@Composable
private fun NoticeItem(data: AppNotice) {
    val placeholder = data.id == -1
    var exTitle = ""
    var exTitleColor = colorResource(id = R.color.news_update)
    var newVersion = false

    if (data.type != -1) {
        val itemVersion = data.title.replace(".", "").toInt()
        if (data.type == 0) {
            exTitle = if (itemVersion > BuildConfig.VERSION_CODE) {
                newVersion = true
                exTitleColor = colorResource(id = R.color.color_rank_21)
                "版本更新"
            } else {
                "当前版本"
            }
        } else if (data.type == 1 && itemVersion == BuildConfig.VERSION_CODE) {
            exTitle = "当前版本"
        }
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(
            horizontal = Dimen.largePadding,
            vertical = Dimen.mediumPadding
        )
    ) {
        Row(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainTitleText(
                text = data.title,
                modifier = Modifier.placeholder(
                    visible = placeholder,
                    highlight = PlaceholderHighlight.shimmer()
                )
            )
            if (data.date != "") {
                MainTitleText(
                    text = data.date.formatTime.substring(0, 10),
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
            if (exTitle != "") {
                MainTitleText(
                    text = exTitle,
                    backgroundColor = exTitleColor,
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
        }

        MainCard(modifier = Modifier
            .placeholder(
                visible = placeholder,
                highlight = PlaceholderHighlight.shimmer()
            ),
            onClick = {
                if (data.type == 0 || data.type == -1) {
                    if (!placeholder) {
                        openWebView(context, data.url)
                    }
                }
            }
        ) {
            Column(modifier = Modifier.padding(Dimen.largePadding)) {
                //内容
                MainContentText(
                    text = data.message,
                    textAlign = TextAlign.Start
                )
                if (data.type == 0 && newVersion) {
                    MainContentText(
                        text = stringResource(id = R.string.to_update),
                        color = colorResource(id = R.color.color_rank_21),
                        modifier = Modifier.padding(top = Dimen.mediumPadding)
                    )
                }
            }
        }
    }

}

@Preview
@ExperimentalMaterialApi
@Composable
private fun NoticeItemPreview() {
    PreviewBox {
        Column {
            NoticeItem(data = AppNotice(id = 0))
        }
    }
}
