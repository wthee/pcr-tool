package cn.wthee.pcrtool.ui.tool

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentIdWithOdd
import cn.wthee.pcrtool.data.db.view.equipCompare
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.common.CommonSpacer
import cn.wthee.pcrtool.ui.common.FabCompose
import cn.wthee.pcrtool.ui.common.MainText
import cn.wthee.pcrtool.ui.equip.AreaItem
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.intArrayList
import cn.wthee.pcrtool.viewmodel.RandomEquipAreaViewModel
import kotlinx.coroutines.launch

/**
 * 掉落信息
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
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
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = scrollState
                ) {
                    items(areaList) {
                        val odds = arrayListOf<EquipmentIdWithOdd>()
                        it.equipIds.intArrayList.forEach { id ->
                            odds.add(EquipmentIdWithOdd(id, 0))
                        }
                        odds.sortWith(equipCompare())

                        AreaItem(
                            ImageResourceHelper.UNKNOWN_EQUIP_ID,
                            odds,
                            "区域 ${it.area}",
                            colorResource(id = R.color.color_rank_21)
                        )
                    }
                    item {
                        CommonSpacer()
                    }
                }
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
                        colorResource(id = R.color.color_rank_21)
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
                    try {
                        scrollState.scrollToItem(0)
                    } catch (e: Exception) {
                    }
                } catch (e: Exception) {
                }
            }
        }
    }


}