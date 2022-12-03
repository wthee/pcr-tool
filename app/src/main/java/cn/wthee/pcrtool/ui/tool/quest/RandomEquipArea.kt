package cn.wthee.pcrtool.ui.tool.quest

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.EquipmentIdWithOdd
import cn.wthee.pcrtool.data.model.RandomEquipDropArea
import cn.wthee.pcrtool.ui.common.CommonSpacer
import cn.wthee.pcrtool.ui.common.FabCompose
import cn.wthee.pcrtool.ui.common.MainText
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.RandomEquipAreaViewModel
import kotlinx.coroutines.launch

/**
 * 额外掉落区域列表
 * @param equipId 0：查看全部、非0：仅查看掉落该装备的区域
 */
@Composable
fun RandomEquipArea(
    equipId: Int,
    scrollState: LazyListState,
    randomEquipAreaViewModel: RandomEquipAreaViewModel = hiltViewModel()
) {
    val areaList =
        randomEquipAreaViewModel.getEquipArea(equipId).collectAsState(initial = null).value
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (areaList != null) {
            if (areaList.isNotEmpty()) {
                RandomDropAreaList(
                    selectId = equipId,
                    scrollState = scrollState,
                    areaList = areaList
                )
            } else {
                MainText(
                    text = stringResource(R.string.tip_random_drop),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val odds = arrayListOf<EquipmentIdWithOdd>()
            for (i in 0..9) {
                odds.add(EquipmentIdWithOdd())
            }

            LazyColumn(
                state = scrollState
            ) {
                items(10) {
                    AreaItem(
                        -1,
                        odds,
                        "区域 xx",
                        colorGreen
                    )
                }
                item {
                    CommonSpacer()
                }
            }
        }

        //回到顶部
        FabCompose(
            iconType = MainIconType.RANDOM_AREA,
            text = stringResource(id = R.string.random_area),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            coroutineScope.launch {
                try {
                    scrollState.scrollToItem(0)
                } catch (_: Exception) {
                }
            }
        }
    }


}

/**
 * 随机掉落区域列表
 */
@Composable
fun RandomDropAreaList(
    selectId: Int,
    scrollState: LazyListState = rememberLazyListState(),
    areaList: List<RandomEquipDropArea>
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
            val odds = arrayListOf<EquipmentIdWithOdd>()
            it.equipIds.intArrayList.forEach { id ->
                odds.add(EquipmentIdWithOdd(id, 0))
            }

            AreaItem(
                selectId,
                odds,
                stringResource(id = R.string.random_drop_area_title, it.area) + when (it.type) {
                    1 -> stringResource(id = R.string.random_drop_area_1)
                    2 -> stringResource(id = R.string.random_drop_area_2)
                    else -> ""
                },
                colorGreen
            )
        }
        item {
            CommonSpacer()
        }
    }
}