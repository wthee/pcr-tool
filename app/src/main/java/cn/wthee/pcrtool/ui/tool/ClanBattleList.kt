package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ClanBattleInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.getMultipleIcon
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.compose.ExtendedFabCompose
import cn.wthee.pcrtool.ui.compose.IconCompose
import cn.wthee.pcrtool.ui.compose.MainTitleText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.getZhNumberText
import cn.wthee.pcrtool.viewmodel.ClanViewModel
import kotlinx.coroutines.launch

/**
 * 每月 BOSS 信息列表
 */
@Composable
fun ClanBattleList(clanViewModel: ClanViewModel = hiltNavGraphViewModel()) {
    clanViewModel.getAllClanBattleData()
    val clanList = clanViewModel.clanInfo.observeAsState()
    val state = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    navViewModel.loading.postValue(true)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.bg_gray))
    ) {
        clanList.value?.let { data ->
            navViewModel.loading.postValue(false)
            LazyColumn(state = state) {
                items(data) {
                    ClanBattleItem(it)
                }
                item {
                    Spacer(modifier = Modifier.height(Dimen.sheetMarginBottom))
                }
            }
        }
        //回到顶部
        ExtendedFabCompose(
            iconType = MainIconType.CLAN,
            text = stringResource(id = R.string.tool_clan),
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

@Composable
private fun ClanBattleItem(clanInfo: ClanBattleInfo) {
    val section = clanInfo.getAllBossInfo().size
    Column(
        modifier = Modifier
            .padding(Dimen.mediuPadding)
            .fillMaxWidth()
    ) {
        //标题
        Row(modifier = Modifier.padding(bottom = Dimen.mediuPadding)) {
            MainTitleText(text = clanInfo.getDate())
            MainTitleText(
                text = stringResource(
                    id = R.string.section,
                    getZhNumberText(section)
                ),
                backgroundColor = getSectionTextColor(section),
                modifier = Modifier.padding(start = Dimen.smallPadding),
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = Dimen.cardElevation, shape = Shapes.large, clip = true)
        ) {
            //图标
            Row(
                modifier = Modifier.padding(Dimen.mediuPadding),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                clanInfo.getUnitIdList(1).forEach {
                    Box {
                        IconCompose(data = Constants.UNIT_ICON_URL + it.unitId + Constants.WEBP) {
                            //TODO 跳转至详情
                        }
                        if (it.targetCount > 1) {
                            Icon(
                                getMultipleIcon(it.targetCount - 1),
                                contentDescription = null,
                                tint = colorResource(id = R.color.color_rank_18),
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}


/**
 * 获取团队战阶段字体颜色
 */
@Composable
private fun getSectionTextColor(section: Int): Color {
    val color = when (section) {
        1 -> R.color.color_rank_2_3
        2 -> R.color.color_rank_4_6
        3 -> R.color.color_rank_7_10
        4 -> R.color.color_rank_11_17
        else -> R.color.color_rank_18
    }
    return colorResource(id = color)
}

