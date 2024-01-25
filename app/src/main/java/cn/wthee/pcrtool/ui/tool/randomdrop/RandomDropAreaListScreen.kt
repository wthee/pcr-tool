package cn.wthee.pcrtool.ui.tool.randomdrop

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.EquipmentIdWithOdds
import cn.wthee.pcrtool.data.model.RandomEquipDropArea
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.ExpandableHeader
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.tool.quest.AreaItem
import cn.wthee.pcrtool.utils.intArrayList
import kotlinx.coroutines.launch

/**
 * 额外掉落区域列表
 * @param equipId 0：查看全部、非0：仅查看掉落该装备的区域
 */
@Composable
fun RandomDropAreaListScreen(
    equipId: Int,
    randomDropAreaListViewModel: RandomDropAreaListViewModel = hiltViewModel()
) {
    val uiState by randomDropAreaListViewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberLazyListState()
    val scope = rememberCoroutineScope()


    MainScaffold(
        fab = {
            //回到顶部
            MainSmallFab(
                iconType = MainIconType.RANDOM_AREA,
                text = stringResource(id = R.string.random_area),
                loading = uiState.loadState == LoadState.Loading,
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
        Column {
            ExpandableHeader(
                scrollState = scrollState,
                title = stringResource(id = R.string.random_drop_source),
                startText = stringResource(id = R.string.random_drop_tip),
                url = stringResource(id = R.string.random_drop_source_url)
            )

            StateBox(
                stateType = uiState.loadState,
                loadingContent = {
                    Column {
                        for (i in 0..10) {
                            AreaItem(
                                -1,
                                arrayListOf(EquipmentIdWithOdds()),
                                "",
                                arrayListOf(),
                                colorGreen
                            )
                        }
                    }
                }
            ) {
                uiState.randomDropList?.let {
                    RandomDropAreaContent(
                        selectId = equipId,
                        scrollState = scrollState,
                        areaList = it
                    )
                }
            }

        }
    }

}

/**
 * 随机掉落区域列表
 * @see [cn.wthee.pcrtool.ui.tool.quest.AreaItem]
 */
@Composable
fun RandomDropAreaContent(
    selectId: Int,
    scrollState: LazyListState = rememberLazyListState(),
    areaList: List<RandomEquipDropArea>,
    searchEquipIdList: List<Int> = arrayListOf()
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = scrollState
    ) {

        items(
            items = areaList,
            key = {
                "${it.area}-${it.type}"
            }
        ) {
            val odds = arrayListOf<EquipmentIdWithOdds>()
            it.equipIds.intArrayList.forEach { id ->
                odds.add(EquipmentIdWithOdds(id, 0))
            }

            AreaItem(
                selectedId = selectId,
                odds = odds,
                num = stringResource(
                    id = R.string.random_drop_area_title,
                    it.area
                ) + when (it.type) {
                    1 -> stringResource(id = R.string.random_drop_area_1)
                    2 -> stringResource(id = R.string.random_drop_area_2)
                    else -> ""
                },
                searchEquipIdList = searchEquipIdList,
                color = colorGreen
            )
        }
        item {
            CommonSpacer()
        }
    }
}