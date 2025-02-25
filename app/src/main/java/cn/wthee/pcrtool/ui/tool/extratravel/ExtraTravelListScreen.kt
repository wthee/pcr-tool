package cn.wthee.pcrtool.ui.tool.extratravel

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ExtraEquipQuestData
import cn.wthee.pcrtool.data.db.view.ExtraTravelData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonGroupTitle
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.CommonTitleContentText
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.VerticalGridList
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.shared.SharedElementKey
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.ICON_EXTRA_EQUIPMENT_TRAVEL_MAP
import cn.wthee.pcrtool.utils.toTimeText
import kotlinx.coroutines.launch

/**
 * ex冒险区域
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ExtraTravelListScreen(
    animatedVisibilityScope: AnimatedVisibilityScope,
    toExtraEquipTravelAreaDetail: (Int) -> Unit,
    extraTravelListViewModel: ExtraTravelListViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()
    val uiState by extraTravelListViewModel.uiState.collectAsStateWithLifecycle()

    MainScaffold(
        fab = {
            //回到顶部
            MainSmallFab(
                iconType = MainIconType.EXTRA_EQUIP_DROP,
                text = stringResource(id = R.string.tool_travel),
                onClick = {
                    scope.launch {
                        try {
                            scrollState.scrollToItem(0)
                        } catch (_: Exception) {
                        }
                    }
                }
            )
        }
    ) {
        StateBox(
            stateType = uiState.loadState,
            errorContent = {
                CenterTipText(text = stringResource(R.string.not_installed))
            }
        ) {
            uiState.areaList?.let { areaList ->
                LazyColumn(state = scrollState) {
                    items(areaList) {
                        TravelItem(
                            animatedVisibilityScope = animatedVisibilityScope,
                            travelData = it,
                            toExtraEquipTravelAreaDetail = toExtraEquipTravelAreaDetail
                        )
                    }
                    item {
                        CommonSpacer()
                    }
                }
            }
        }
    }

}

/**
 * 冒险区域item
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.TravelItem(
    animatedVisibilityScope: AnimatedVisibilityScope,
    travelData: ExtraTravelData,
    toExtraEquipTravelAreaDetail: (Int) -> Unit
) {

    //area
    CommonGroupTitle(
        iconData = ImageRequestHelper.getInstance()
            .getUrl(ICON_EXTRA_EQUIPMENT_TRAVEL_MAP, travelData.travelAreaId),
        iconSize = Dimen.menuIconSize,
        titleStart = travelData.travelAreaName,
        titleEnd = travelData.questCount.toString(),
        modifier = Modifier.padding(Dimen.mediumPadding)
    )

    //quest列表
    VerticalGridList(
        itemWidth = getItemWidth() / 2,
        itemCount = travelData.questList.size,
        contentPadding = Dimen.mediumPadding,
        modifier = Modifier.padding(horizontal = Dimen.commonItemPadding),
    ) {
        val questData = travelData.questList[it]
        MainCard(
            onClick = {
                toExtraEquipTravelAreaDetail(questData.travelQuestId)
            }
        ) {
            TravelQuestHeader(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                animatedVisibilityScope = animatedVisibilityScope,
                questData = questData
            )
        }
    }
}

/**
 * ex冒险区域公用头部布局
 * @param showExtraContent false 查看掉落列表时，不显示其它额外信息
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.TravelQuestHeader(
    modifier: Modifier = Modifier,
    animatedVisibilityScope: AnimatedVisibilityScope,
    questData: ExtraEquipQuestData,
    showExtraContent: Boolean = true
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .padding(vertical = Dimen.mediumPadding)
            .then(
                if (MainActivity.animOnFlag) {
                    Modifier.sharedElement(
                        state = rememberSharedContentState(
                            key = "${SharedElementKey.TRAVEL}${questData.travelQuestId}"
                        ),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                } else {
                    Modifier
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //图标
        MainIcon(
            data = ImageRequestHelper.getInstance()
                .getUrl(ICON_EXTRA_EQUIPMENT_TRAVEL_MAP, questData.travelQuestId),
        )
        //标题
        Subtitle1(
            text = questData.getTitle(),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = Dimen.smallPadding)
        )
        if (showExtraContent) {
            //其它参数
            val titleList = arrayListOf(
                stringResource(id = R.string.travel_limit_unit_num),
                stringResource(id = R.string.travel_need_power),
                stringResource(id = R.string.travel_time),
                stringResource(id = R.string.travel_time_decrease_limit)
            )
            val contentList = arrayListOf(
                questData.limitUnitNum.toString(),
                stringResource(id = R.string.value_10_k, questData.needPower / 10000),
                toTimeText(questData.travelTime * 1000, context),
                toTimeText(questData.travelTimeDecreaseLimit * 1000, context)
            )
            VerticalGridList(
                itemCount = titleList.size,
                itemWidth = getItemWidth() / 2
            ) {
                CommonTitleContentText(
                    title = titleList[it],
                    content = contentList[it]
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@CombinedPreviews
@Composable
private fun TravelItemPreview() {
    PreviewLayout {
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                val quest = ExtraEquipQuestData(
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

                Column {
                    TravelItem(
                        animatedVisibilityScope = this@AnimatedVisibility,
                        travelData = ExtraTravelData(
                            travelAreaId = 1,
                            travelAreaName = stringResource(id = R.string.debug_short_text),
                            questCount = 1,
                            questList = arrayListOf(
                                quest, quest, quest
                            )
                        ),
                        toExtraEquipTravelAreaDetail = {}
                    )
                }

            }
        }
    }
}

