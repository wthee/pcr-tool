package cn.wthee.pcrtool.ui.tool.extraequip

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.extratravel.TravelQuestItem
import cn.wthee.pcrtool.viewmodel.ExtraEquipmentViewModel

/**
 * ex装备掉落信息
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExtraEquipDropList(
    equipId: Int,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel()
) {
    //掉落区域信息
    val dropListFlow = remember {
        extraEquipmentViewModel.getExtraDropQuestList(equipId)
    }
    val dropList by dropListFlow.collectAsState(initial = arrayListOf())
    val pagerState = rememberPagerState { dropList.size }

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (dropList.isNotEmpty()) {
            val tabs = arrayListOf<String>()
            dropList.forEach {
                tabs.add(
                    stringResource(
                        id = R.string.extra_area_quest,
                        it.getAreaOrder(),
                        it.getQuestName()
                    )
                )
            }
            MainTabRow(
                pagerState = pagerState,
                tabs = tabs,
                scrollable = true,
                modifier = Modifier
                    .padding(top = Dimen.mediumPadding)
                    .align(Alignment.CenterHorizontally)
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) {
                TravelQuestItem(
                    equipId,
                    dropList[pagerState.currentPage]
                )
            }
        } else {
            Column(
                modifier = Modifier.defaultMinSize(minHeight = Dimen.minSheetHeight),
            ) {
                CenterTipText(text = stringResource(id = R.string.extra_equip_no_drop_quest))
            }
        }
    }
}
