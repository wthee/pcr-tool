package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.AppNotice
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.openWebView
import cn.wthee.pcrtool.viewmodel.NoticeViewModel
import kotlinx.coroutines.launch

/**
 * 通知列表
 */
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun NoticeList(
    scrollState: LazyListState,
    noticeViewModel: NoticeViewModel = hiltViewModel()
) {
    noticeViewModel.getNotice()
    val noticeList = noticeViewModel.notice.observeAsState()
    val coroutineScope = rememberCoroutineScope()
    MainActivity.navViewModel.loading.postValue(true)

    val updateApp = noticeViewModel.updateApp.observeAsState().value ?: false
    val icon = if (updateApp == 1) MainIconType.APP_UPDATE else MainIconType.NOTICE


    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        noticeList.value?.let { data ->
            MainActivity.navViewModel.loading.postValue(false)
            LazyColumn(state = scrollState, contentPadding = PaddingValues(Dimen.mediuPadding)) {
                data.data?.let { list ->
                    items(list) {
                        NoticeItem(it)
                    }
                }
                //查看更多
                item {
                    NoticeItem(
                        AppNotice(
                            "",
                            "",
                            -1,
                            "",
                            stringResource(R.string.visit_more_log),
                            stringResource(R.string.update_log),
                            -1,
                            stringResource(R.string.readme)
                        )
                    )
                }
                item {
                    CommonSpacer()
                }
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
                scrollState.scrollToItem(0)
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
    var exTitle = ""
    var exTitleColor = colorResource(id = R.color.news_update)
    var newVersion = false
    if (data.type == 0) {
        val remoteVersion = data.title.replace(".", "").toInt()
        exTitle = if (remoteVersion > BuildConfig.VERSION_CODE) {
            newVersion = true
            "版本更新"
        } else {
            "当前版本"
        }
        exTitleColor = colorResource(id = R.color.news_update)
    } else if (data.type == 1) {
        exTitleColor = colorResource(id = R.color.news_system)
    }
    val context = LocalContext.current

    Row(
        modifier = Modifier.padding(bottom = Dimen.mediuPadding)
    ) {
        MainTitleText(text = data.title)
        if (exTitle != "") {
            MainTitleText(
                text = exTitle,
                backgroundColor = exTitleColor,
                modifier = Modifier.padding(start = Dimen.smallPadding)
            )
        }
    }

    MainCard(modifier = Modifier.padding(bottom = Dimen.largePadding), onClick = {
        if (data.type == 0 || data.type == -1) {
            openWebView(context, data.url)
        }
    }) {
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
                    modifier = Modifier.padding(top = Dimen.mediuPadding)
                )
            }
        }

    }
}
