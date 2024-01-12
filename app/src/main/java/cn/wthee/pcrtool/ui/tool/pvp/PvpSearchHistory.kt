package cn.wthee.pcrtool.ui.tool.pvp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.PvpHistoryData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.utils.toDate


/**
 * 搜索历史
 */
@Composable
fun PvpSearchHistory(
    historyList: List<PvpHistoryData>,
    historyListState: LazyGridState,
    toCharacter: (Int) -> Unit,
    floatWindow: Boolean,
    searchByDefs: (List<Int>) -> Unit
) {
    val itemWidth = getItemWidth(floatWindow)


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        if (historyList.isNotEmpty()) {
            LazyVerticalGrid(
                state = historyListState, columns = GridCells.Adaptive(itemWidth)
            ) {
                items(historyList) { data ->
                    PvpHistoryItem(
                        itemData = data,
                        floatWindow = floatWindow,
                        toCharacter = toCharacter,
                        searchByDefs = searchByDefs
                    )
                }
                item {
                    CommonSpacer()
                }
            }
        } else {
            CenterTipText(
                text = stringResource(id = R.string.pvp_no_history)
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
    searchByDefs: (List<Int>) -> Unit
) {
    val largePadding = if (floatWindow) Dimen.mediumPadding else Dimen.largePadding
    val mediumPadding = if (floatWindow) Dimen.smallPadding else Dimen.mediumPadding


    Column(
        modifier = Modifier.padding(
            horizontal = largePadding, vertical = mediumPadding
        )
    ) {
        Row(
            modifier = Modifier.padding(bottom = mediumPadding),
            verticalAlignment = Alignment.Bottom
        ) {
            //日期
            MainTitleText(
                text = itemData.date.formatTime.toDate
            )
            Spacer(modifier = Modifier.weight(1f))
            MainIcon(
                data = MainIconType.PVP_SEARCH, size = Dimen.fabIconSize
            ) {
                searchByDefs(itemData.getDefIds())
            }
        }
        //防守队伍角色图标
        MainCard {
            PvpUnitIconLine(
                modifier = Modifier.padding(top = mediumPadding, bottom = mediumPadding),
                ids = itemData.getDefIds(),
                floatWindow = floatWindow,
                toCharacter = toCharacter
            )
        }
    }

}


@CombinedPreviews
@Composable
private fun PvpHistoryItemPreview() {
    val data = PvpHistoryData(
        "id", "2@1-2-3-4-5", "2020/01/01 00:00:00"
    )
    PreviewLayout {
        PvpHistoryItem(
            itemData = data,
            floatWindow = false,
            toCharacter = { },
            searchByDefs = {},
        )
        PvpHistoryItem(
            itemData = data,
            floatWindow = true,
            toCharacter = { },
            searchByDefs = {},
        )
    }
}