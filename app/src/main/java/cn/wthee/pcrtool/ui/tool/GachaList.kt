package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.compose.ExtendedFabCompose
import cn.wthee.pcrtool.ui.compose.MainContentText
import cn.wthee.pcrtool.ui.compose.MainTitleText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.days
import cn.wthee.pcrtool.utils.daysInt
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.GachaViewModel
import kotlinx.coroutines.launch

/**
 * 角色卡池页面
 */
@Composable
fun GachaList(
    toCharacterDetail: (Int) -> Unit,
    gachaViewModel: GachaViewModel = hiltNavGraphViewModel()
) {
    gachaViewModel.getGachaHistory()
    val gachas = gachaViewModel.gachas.observeAsState()
    val state = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.bg_gray))
    ) {
        gachas.value?.let { data ->
            LazyColumn(state = state) {
                items(data) {
                    GachaItem(it, toCharacterDetail)
                }
                item {
                    Spacer(modifier = Modifier.height(Dimen.sheetMarginBottom))
                }
            }
        }
        //回到顶部
        ExtendedFabCompose(
            iconType = MainIconType.GACHA,
            text = stringResource(id = R.string.tool_gacha),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                state.scrollToItem(0)
            }
        }
    }


}

/**
 * 单个卡池
 */
@Composable
private fun GachaItem(gachaInfo: GachaInfo, toCharacterDetail: (Int) -> Unit) {
    val today = getToday()
    val sd = gachaInfo.startTime
    val ed = gachaInfo.endTime
    val inProgress = today.daysInt(sd) >= 0 && ed.daysInt(today) >= 0
    Column(
        modifier = Modifier
            .padding(Dimen.mediuPadding)
            .fillMaxWidth()
    ) {
        //标题
        Row(modifier = Modifier.padding(bottom = Dimen.mediuPadding)) {
            MainTitleText(text = gachaInfo.getDate())
            if (inProgress) {
                MainTitleText(
                    text = stringResource(R.string.in_progress, gachaInfo.endTime.days(today)),
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    backgroundColor = colorResource(id = R.color.news_update)
                )
            } else {
                MainTitleText(
                    text = gachaInfo.endTime.days(gachaInfo.startTime),
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
        }
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = Dimen.cardElevation, shape = Shapes.large, clip = true)
        ) {
            Column(modifier = Modifier.padding(Dimen.mediuPadding)) {
                //内容
                MainContentText(
                    text = gachaInfo.getType(),
                    modifier = Modifier.padding(
                        top = Dimen.smallPadding,
                        bottom = Dimen.smallPadding
                    ),
                    textAlign = TextAlign.Start
                )
                //图标/描述
                val icons = gachaInfo.unitIds.intArrayList()
                if (icons.isEmpty()) {
                    MainContentText(
                        text = gachaInfo.getDesc(),
                        modifier = Modifier.padding(Dimen.smallPadding),
                        textAlign = TextAlign.Start
                    )
                } else {
                    IconListCompose(icons, toCharacterDetail)
                }
            }
        }
    }
}

