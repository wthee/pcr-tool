package cn.wthee.pcrtool.ui.tool.extraequip

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.common.CenterTipText
import cn.wthee.pcrtool.ui.common.Subtitle1
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.travel.TravelQuestItem
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.viewmodel.ExtraEquipmentViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

/**
 * ex装备掉落信息
 */
@OptIn(ExperimentalPagerApi::class)
@Composable
fun ExtraEquipDropList(
    equipId: Int,
    extraEquipmentViewModel: ExtraEquipmentViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dropList = extraEquipmentViewModel.getDropQuestList(equipId)
        .collectAsState(initial = arrayListOf()).value
    val pagerState = rememberPagerState()

    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        if (dropList.isNotEmpty()) {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = Dimen.mediumPadding)
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.33f * (dropList.size % 4))
            ) {
                dropList.forEachIndexed { index, quest ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                VibrateUtil(context).single()
                                pagerState.scrollToPage(index)
                            }
                        },
                        modifier = Modifier.padding(horizontal = Dimen.mediumPadding)
                    ) {
                        Subtitle1(
                            text = quest.getQuestName(),
                            modifier = Modifier.padding(Dimen.smallPadding),
                            color = if (pagerState.currentPage == index) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                            maxLines = 1
                        )
                    }
                }
            }

            HorizontalPager(count = dropList.size, state = pagerState) {
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
