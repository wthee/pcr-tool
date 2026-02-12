package cn.wthee.pcrtool.ui.tool.extraequip.drop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ExtraEquipQuestData
import cn.wthee.pcrtool.data.db.view.ExtraEquipSubRewardData
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.MainHorizontalPagerIndicator
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.tool.extratravel.TravelQuestItem

/**
 * ex装备掉落信息
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ExtraEquipDropListScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
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
                    animatedVisibilityScope = animatedVisibilityScope,
                    equipId = equipId,
                    dropList = uiState.dropList!!,
                    subRewardListMap = uiState.subRewardListMap
                )
            }
        }
    }

}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.ExtraEquipDropListContent(
    animatedVisibilityScope: AnimatedVisibilityScope,
    equipId: Int,
    dropList: List<ExtraEquipQuestData>,
    subRewardListMap: HashMap<Int, List<ExtraEquipSubRewardData>>?
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val pagerState = rememberPagerState { dropList.size }

        MainHorizontalPagerIndicator(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            pagerState = pagerState,
            pageCount = dropList.size
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.Top
        ) {
            val questData = dropList[pagerState.currentPage]
            TravelQuestItem(
                animatedVisibilityScope = animatedVisibilityScope,
                selectedId = equipId,
                questData = questData,
                subRewardList = subRewardListMap?.get(questData.travelQuestId)
            )
        }
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@CombinedPreviews
@Composable
private fun ExtraEquipDropListContentPreview() {
    PreviewLayout {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
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
                        animatedVisibilityScope = this@AnimatedVisibility,
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
    }
}