package cn.wthee.pcrtool.ui.tool

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.GachaViewModel
import kotlinx.coroutines.launch

/**
 * 角色卡池页面
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@Composable
fun GachaList(
    scrollState: LazyListState,
    toCharacterDetail: (Int) -> Unit,
    gachaViewModel: GachaViewModel = hiltViewModel()
) {
    gachaViewModel.getGachaHistory()
    val gachas = gachaViewModel.gachas.observeAsState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = if (MaterialTheme.colors.isLight) R.color.bg_gray else R.color.bg_gray_dark))
    ) {
        SlideAnimation(visible = gachas.value != null) {
            gachas.value?.let { data ->
                LazyColumn(
                    state = scrollState,
                    contentPadding = PaddingValues(Dimen.mediuPadding)
                ) {
                    items(data) {
                        GachaItem(it, toCharacterDetail)
                    }
                    item {
                        CommonSpacer()
                    }
                }
            }
        }
        //回到顶部
        FabCompose(
            iconType = MainIconType.GACHA,
            text = stringResource(id = R.string.tool_gacha),
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
 * 单个卡池
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
private fun GachaItem(gachaInfo: GachaInfo, toCharacterDetail: (Int) -> Unit) {
    val today = getToday()
    val sd = gachaInfo.startTime
    val ed = gachaInfo.endTime
    val inProgress = today.hourInt(sd) > 0 && ed.hourInt(today) > 0

    val icons = gachaInfo.unitIds.intArrayList()

    //标题
    Row(modifier = Modifier.padding(bottom = Dimen.mediuPadding)) {
        MainTitleText(text = gachaInfo.startTime.formatTime().substring(0, 10))
        MainTitleText(
            text = gachaInfo.endTime.days(gachaInfo.startTime),
            modifier = Modifier.padding(start = Dimen.smallPadding)
        )
        //计时
        if (inProgress) {
            Row(
                modifier = Modifier.padding(start = Dimen.smallPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconCompose(
                    data = MainIconType.TIME_LEFT.icon,
                    size = Dimen.smallIconSize,
                )
                MainContentText(
                    text = stringResource(R.string.in_progress, gachaInfo.endTime.dates(today)),
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colors.primary
                )
            }
        }
    }

    MainCard(modifier = Modifier.padding(bottom = Dimen.largePadding)) {
        Column(modifier = Modifier.padding(Dimen.mediuPadding)) {
            //内容
            MainContentText(
                text = gachaInfo.getType(),
                modifier = Modifier.padding(bottom = Dimen.smallPadding),
                textAlign = TextAlign.Start
            )

            //图标/描述
            if (icons.isEmpty()) {
                MainContentText(
                    text = gachaInfo.getDesc(),
                    modifier = Modifier.padding(Dimen.smallPadding),
                    textAlign = TextAlign.Start
                )
            } else {
                IconListCompose(icons, toCharacterDetail)
            }

            //结束日期
            CaptionText(
                text = gachaInfo.endTime.formatTime(),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}

