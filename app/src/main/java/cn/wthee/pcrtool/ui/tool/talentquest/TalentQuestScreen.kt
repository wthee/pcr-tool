package cn.wthee.pcrtool.ui.tool.talentquest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.TalentQuestData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.TalentType
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.SelectTypeFab
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.components.placeholder
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.ImageRequestHelper.Companion.ICON_UNIT
import kotlinx.coroutines.launch

/**
 * 深域关卡
 */
@Composable
fun TalentQuestScreen(
    toEnemyDetail: (Int) -> Unit,
    talentQuestViewModel: TalentQuestViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val uiState by talentQuestViewModel.uiState.collectAsStateWithLifecycle()
    //列表状态
    val scrollState = rememberLazyGridState()
    //天赋类型
    val talentTabs = arrayListOf<String>()
    TalentType.entries.forEachIndexed { _, talentType ->
        if (talentType != TalentType.ALL) {
            talentTabs.add(
                stringResource(id = talentType.typeNameId)
            )
        }
    }

    MainScaffold(
        enableClickClose = uiState.openTalentDialog,
        onCloseClick = {
            talentQuestViewModel.changeTalentDialog(false)
        },
        fab = {

            if (uiState.loadState == LoadState.Success) {
                SelectTypeFab(
                    icon = MainIconType.TALENT,
                    tabs = talentTabs,
                    //下标 -1 对应所选择的类型
                    selectedIndex = uiState.talentType.type - 1,
                    openDialog = uiState.openTalentDialog,
                    changeDialog = talentQuestViewModel::changeTalentDialog,
                    changeSelect = talentQuestViewModel::changeTalentSelect,
                    selectedColor = uiState.talentType.color,
                    noPadding = true
                )
            }

            MainSmallFab(
                iconType = MainIconType.TALENT_QUEST,
                text = stringResource(id = R.string.talent_quest),
                onClick = {
                    coroutineScope.launch {
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
            TalentQuestContent(
                questDataList = uiState.questDataList!!,
                color = uiState.talentType.color,
                scrollState = scrollState,
                toEnemyDetail = toEnemyDetail
            )
        }
    }

}

@Composable
private fun TalentQuestContent(
    questDataList: List<TalentQuestData>,
    color: Color,
    scrollState: LazyGridState,
    toEnemyDetail: (Int) -> Unit,
) {
    LazyVerticalGrid(
        state = scrollState,
        columns = GridCells.Adaptive(getItemWidth())
    ) {
        items(
            items = questDataList,
            key = {
                it.questId
            }
        ) {
            TalentQuestItem(
                questData = it,
                color = color,
                toEnemyDetail = toEnemyDetail

            )
        }
        item {
            CommonSpacer()
        }
    }
}

/**
 * 关卡怪物图标列表
 * @param questData 关卡数据
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TalentQuestItem(
    questData: TalentQuestData,
    color: Color,
    toEnemyDetail: (Int) -> Unit,
) {
    val placeholder = questData.questId == -1
    val enemyIdList = questData.getEnemyIdList()
    val unitIdList = questData.getUnitIdList()

    Column(
        modifier = Modifier
            .padding(
                horizontal = Dimen.largePadding,
                vertical = Dimen.mediumPadding
            )
    ) {
        FlowRow(
            modifier = Modifier.padding(bottom = Dimen.mediumPadding)
        ) {
            //关卡名称
            MainTitleText(
                text = questData.questName,
                backgroundColor = color,
                modifier = Modifier
                    .placeholder(visible = placeholder)
                    .align(Alignment.CenterVertically)
            )
            //星素碎片
            MainTitleText(
                text = stringResource(R.string.talent_item_1, questData.rewardNum2),
                backgroundColor = color,
                modifier = Modifier
                    .padding(start = Dimen.smallPadding)
                    .placeholder(visible = placeholder)
            )
            //星素水晶球
            MainTitleText(
                text = stringResource(R.string.talnet_item_2, questData.rewardNum3),
                backgroundColor = color,
                modifier = Modifier
                    .padding(start = Dimen.smallPadding)
                    .placeholder(visible = placeholder)
            )
        }


        MainCard(
            modifier = Modifier.placeholder(visible = placeholder),
        ) {
            //图标
            Row(
                modifier = Modifier
                    .padding(Dimen.mediumPadding)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                enemyIdList.forEachIndexed { index, enemyId ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            MainIcon(
                                data = ImageRequestHelper.getInstance()
                                    .getUrl(ICON_UNIT, unitIdList[index]),
                                onClick = {
                                    if (!placeholder) {
                                        toEnemyDetail(enemyId)
                                    }
                                }
                            )
                        }
                    }
                }
                if (placeholder) {
                    MainIcon(
                        data = R.drawable.unknown_gray
                    )
                }
            }
        }
    }

}


@CombinedPreviews
@Composable
private fun TalentQuestRewardContentPreview() {
    PreviewLayout {
        TalentQuestItem(
            questData = TalentQuestData(
                questId = 1,
                questName = stringResource(R.string.debug_name),
                rewardNum2 = 223,
                rewardNum3 = 234,
                enemyId1 = 1111,
                unitId1 = 23324
            ),
            color = MaterialTheme.colors.primary,
            toEnemyDetail = {}
        )
    }
}