package cn.wthee.pcrtool.ui.tool.extraequip

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.common.CenterTipText
import cn.wthee.pcrtool.ui.common.MainTabRow
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.extratravel.TravelQuestItem
import cn.wthee.pcrtool.viewmodel.ExtraEquipmentViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState

/**
 * ex装备掉落信息
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ExtraEquipDropList(
    equipId: Int,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel()
) {
    val dropList = extraEquipmentViewModel.getExtraDropQuestList(equipId)
        .collectAsState(initial = arrayListOf()).value
    val pagerState = rememberPagerState()

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
                count = dropList.size,
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
