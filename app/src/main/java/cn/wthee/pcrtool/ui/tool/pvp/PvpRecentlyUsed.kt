package cn.wthee.pcrtool.ui.tool.pvp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.ui.common.CommonSpacer
import cn.wthee.pcrtool.viewmodel.PvpViewModel

/**
 * 最近最多使用过的角色
 */
@Composable
fun PvpRecentlyUsedList(
    spanCount: Int,
    usedListState: LazyGridState,
    selectedIds: ArrayList<PvpCharacterData>,
    floatWindow: Boolean,
    characterDataList: List<PvpCharacterData>,
    pvpViewModel: PvpViewModel,
) {
    //常用角色
    val recentlyUsedUnitList =
        pvpViewModel.getRecentlyUsedUnitList(characterDataList)
            .collectAsState(initial = arrayListOf()).value


    //常用角色一览
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //角色图标列表
        LazyVerticalGrid(
            columns = GridCells.Fixed(spanCount),
            state = usedListState,
            verticalArrangement = Arrangement.Center
        ) {
            items(
                items = recentlyUsedUnitList.sortedBy { it.position },
                key = {
                    it.unitId
                }
            ) {
                PvpIconItem(selectedIds, it, floatWindow)
            }
            items(spanCount) {
                CommonSpacer()
            }
        }
    }
}