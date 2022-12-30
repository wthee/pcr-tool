package cn.wthee.pcrtool.ui.tool.pvp

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpHistoryData
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import cn.wthee.pcrtool.viewmodel.PvpViewModel
import kotlinx.coroutines.launch


/**
 * 搜索历史
 */
@Composable
fun PvpSearchHistory(
    historyListState: LazyGridState,
    toCharacter: (Int) -> Unit,
    floatWindow: Boolean,
    pvpViewModel: PvpViewModel
) {
    val historyDataList = pvpViewModel.getHistory().collectAsState(initial = arrayListOf()).value
    val itemWidth = getItemWidth(floatWindow)

    Box(modifier = Modifier.fillMaxSize()) {
        if (historyDataList.isNotEmpty()) {
            LazyVerticalGrid(
                state = historyListState,
                columns = GridCells.Adaptive(itemWidth)
            ) {
                items(historyDataList) { data ->
                    PvpHistoryItem(
                        data,
                        floatWindow,
                        toCharacter,
                        pvpViewModel
                    )
                }
                item {
                    CommonSpacer()
                }
            }
        } else {
            MainText(
                text = stringResource(id = R.string.pvp_no_history),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

/**
 * 搜索历史项
 */
@Composable
private fun PvpHistoryItem(
    itemData: PvpHistoryData,
    floatWindow: Boolean,
    toCharacter: (Int) -> Unit,
    pvpViewModel: PvpViewModel?,
    characterViewModel: CharacterViewModel? = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val largePadding = if (floatWindow) Dimen.mediumPadding else Dimen.largePadding
    val mediumPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediumPadding


    Column(
        modifier = Modifier.padding(
            horizontal = largePadding,
            vertical = mediumPadding
        )
    ) {
        Row(
            modifier = Modifier.padding(bottom = mediumPadding),
            verticalAlignment = Alignment.Bottom
        ) {
            //日期
            MainTitleText(
                text = itemData.date.formatTime.substring(0, 10)
            )
            Spacer(modifier = Modifier.weight(1f))
            IconCompose(
                data = MainIconType.PVP_SEARCH,
                size = Dimen.fabIconSize
            ) {
                scope.launch {
                    pvpViewModel?.pvpResult?.postValue(null)
                    val selectedData =
                        characterViewModel?.getPvpCharacterByIds(itemData.getDefIds())
                    val selectedIds = selectedData as ArrayList<PvpCharacterData>?
                    selectedIds?.sortByDescending { it.position }
                    navViewModel.selectedPvpData.postValue(selectedIds)
                    navViewModel.showResult.postValue(true)
                }
            }
        }
        MainCard {
            //队伍角色图标
            Column(
                modifier = Modifier
                    .padding(top = mediumPadding, bottom = mediumPadding)
            ) {
                //防守
                PvpUnitIconLine(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    itemData.getDefIds(),
                    floatWindow,
                    toCharacter
                )
            }
        }
    }

}


@CombinedPreviews
@Composable
private fun PvpHistoryItemPreview() {
    val data = PvpHistoryData(
        "id",
        "2@1-2-3-4-5",
        "2020/01/01 00:00:00"
    )
    PreviewLayout {
        PvpHistoryItem(
            data,
            false,
            { },
            null,
            null
        )
        PvpHistoryItem(
            data,
            true,
            { },
            null,
            null
        )
    }
}