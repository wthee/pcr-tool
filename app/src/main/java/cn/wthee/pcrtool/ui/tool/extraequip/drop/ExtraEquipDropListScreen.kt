package cn.wthee.pcrtool.ui.tool.extraequip.drop

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ExtraEquipQuestData
import cn.wthee.pcrtool.data.db.view.ExtraEquipSubRewardData
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainTabRow
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.TabData
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.tool.extratravel.TravelQuestItem

/**
 * ex装备掉落信息
 */
@Composable
fun ExtraEquipDropListScreen(
    equipId: Int,
    extraEquipDropViewModel: ExtraEquipDropViewModel = hiltViewModel()
) {
    val uiState by extraEquipDropViewModel.uiState.collectAsStateWithLifecycle()


    MainScaffold {
        StateBox(
            stateType = uiState.loadState,
            noDataContent = {
                CenterTipText(text = stringResource(id = R.string.extra_equip_no_drop_quest))
            }
        ) {
            if (uiState.dropList != null) {
                ExtraEquipDropListContent(
                    equipId = equipId,
                    dropList = uiState.dropList!!,
                    subRewardListMap = uiState.subRewardListMap
                )
            }
        }
    }

}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun ExtraEquipDropListContent(
    equipId: Int,
    dropList: List<ExtraEquipQuestData>,
    subRewardListMap: HashMap<Int, List<ExtraEquipSubRewardData>>?
) {
    Column {
        val pagerState = rememberPagerState { dropList.size }
        val tabs = arrayListOf<TabData>()
        dropList.forEach {
            tabs.add(
                TabData(
                    tab = stringResource(
                        id = R.string.extra_area_quest,
                        it.getAreaOrder(),
                        it.getQuestName()
                    )
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
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) {
            val questData = dropList[pagerState.currentPage]
            TravelQuestItem(
                selectedId = equipId,
                questData = questData,
                subRewardList = subRewardListMap?.get(questData.travelQuestId)
            )
        }
    }
}


@CombinedPreviews
@Composable
private fun ExtraEquipDropListContentPreview() {
    PreviewLayout {
        val data = hashMapOf<Int, List<ExtraEquipSubRewardData>>()
        data[1] = arrayListOf(
            ExtraEquipSubRewardData(
                travelQuestId = 1,
                category = 1,
                categoryName = stringResource(id = R.string.debug_short_text),
                subRewardIds = "1-2-3",
                subRewardDrops = "1234-2323-4567"
            )
        )
        val questData = ExtraEquipQuestData(
            travelQuestId = 1,
            travelAreaId = 1,
            travelQuestName = stringResource(id = R.string.debug_short_text),
            limitUnitNum = 10,
            travelTime = 1000,
            travelTimeDecreaseLimit = 2000,
            travelDecreaseFlag = 1,
            needPower = 1,
            iconId = 1
        )

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ExtraEquipDropListContent(
                dropList = arrayListOf(
                    questData,
                    questData,
                    questData
                ),
                equipId = 1,
                subRewardListMap = data
            )
        }
    }
}