package cn.wthee.pcrtool.ui.tool

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.GachaInfo
import cn.wthee.pcrtool.ui.compose.ExtendedFabCompose
import cn.wthee.pcrtool.ui.compose.MainContentText
import cn.wthee.pcrtool.ui.compose.MainTitleText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.days
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

    Box(modifier = Modifier.fillMaxSize()) {
        gachas.value?.let { data ->
            LazyColumn(state = state) {
                items(data) {
                    GachaItem(it, toCharacterDetail)
                }
            }
        }
        //回到顶部
        ExtendedFabCompose(
            icon = painterResource(id = R.drawable.ic_gacha),
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
    Card(
        modifier = Modifier
            .padding(Dimen.mediuPadding)
            .fillMaxWidth()
            .shadow(elevation = Dimen.cardElevation, shape = Shapes.large, clip = true)
    ) {
        Column(modifier = Modifier.padding(Dimen.mediuPadding)) {
            //标题
            Row {
                MainTitleText(text = gachaInfo.getDate())
                MainTitleText(
                    text = gachaInfo.endTime.days(gachaInfo.startTime),
                    modifier = Modifier.padding(start = Dimen.smallPadding)
                )
            }
            //内容
            MainContentText(
                text = gachaInfo.getType(),
                modifier = Modifier.padding(top = Dimen.smallPadding, bottom = Dimen.mediuPadding),
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

