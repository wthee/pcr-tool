package cn.wthee.pcrtool.ui.tool.pvp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.ui.common.CommonSpacer

/**
 * 最近最多使用过的角色
 */
@Composable
fun PvpRecentlyUsedList(
    spanCount: Int,
    usedListState: LazyGridState,
    selectedIds: ArrayList<PvpCharacterData>,
    floatWindow: Boolean,
    recentlyUsedUnitList: List<PvpCharacterData>,
) {
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
                items = recentlyUsedUnitList,
                key = {
                    it.unitId
                }
            ) {
                PvpIconItem(selectedIds, it, floatWindow, true)
            }
            items(spanCount) {
                CommonSpacer()
            }
        }
    }
}