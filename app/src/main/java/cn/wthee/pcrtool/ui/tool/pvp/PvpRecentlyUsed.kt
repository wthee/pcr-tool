package cn.wthee.pcrtool.ui.tool.pvp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.ui.components.CommonSpacer
import kotlin.math.max

/**
 * 最近最多使用过的角色
 */
@Composable
fun PvpRecentlyUsedList(
    spanCount: Int,
    usedListState: LazyGridState,
    selectedIds: ArrayList<PvpCharacterData>,
    floatWindow: Boolean,
    pvpViewModel: PvpViewModel,
) {
    val uiState by pvpViewModel.uiState.collectAsStateWithLifecycle()

    //常用角色
    val recentlyUsedUnitList = uiState.recentlyUsedUnitList


    //常用角色一览
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //角色图标列表
        LazyVerticalGrid(
            columns = GridCells.Fixed(max(5, spanCount)),
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
            items(5) {
                CommonSpacer()
            }
        }
    }
}