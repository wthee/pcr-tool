package cn.wthee.pcrtool.ui.character

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RankSelectType
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import kotlinx.coroutines.launch

/**
 * rank 范围装备素材数量统计
 *
 * @param unitId 角色编号，传 0 返回所有角色
 * @param maxRank 角色最大rank
 */
@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun RankEquipCount(
    unitId: Int,
    maxRank: Int,
    toEquipMaterial: (Int) -> Unit,
    isAllUnit: Boolean = false,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberLazyGridState()

    val rank0 = remember {
        mutableStateOf(maxRank)
    }
    val rank1 = remember {
        mutableStateOf(maxRank)
    }

    val rankEquipMaterials =
        equipmentViewModel.getEquipByRank(unitId, rank0.value, rank1.value).collectAsState(
            initial = arrayListOf()
        ).value


    val starIds = remember {
        mutableStateOf(arrayListOf<Int>())
    }
    LaunchedEffect(MainActivity.navSheetState.isVisible) {
        starIds.value = FilterEquipment.getStarIdList()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            //标题
            Row(
                modifier = Modifier.padding(
                    horizontal = Dimen.largePadding,
                    vertical = Dimen.mediumPadding
                ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                MainTitleText(text = stringResource(id = if (isAllUnit) R.string.all_unit_calc_equip else R.string.calc_equip_count))
                RankText(
                    rank = rank0.value,
                    modifier = Modifier.padding(start = Dimen.mediumPadding)
                )
                MainContentText(
                    text = stringResource(id = R.string.to),
                    modifier = Modifier.padding(horizontal = Dimen.smallPadding)
                )
                RankText(
                    rank = rank1.value
                )
            }


            LazyVerticalGrid(
                columns = GridCells.Adaptive(Dimen.iconSize + Dimen.mediumPadding),
                contentPadding = PaddingValues(horizontal = Dimen.mediumPadding),
                state = scrollState
            ) {
                if (rankEquipMaterials.isNotEmpty()) {
                    items(
                        items = rankEquipMaterials,
                        key = {
                            it.id
                        }
                    ) { item ->
                        EquipCountItem(
                            item,
                            starIds.value.contains(item.id),
                            toEquipMaterial
                        )
                    }
                } else {
                    items(count = 10) {
                        EquipCountItem(EquipmentMaterial(), false, toEquipMaterial)
                    }
                }
                items(10) {
                    CommonSpacer()
                }
            }
        }


        //回到顶部
        var allCount = 0
        rankEquipMaterials.forEach {
            allCount += it.count
        }
        FabCompose(
            iconType = MainIconType.EQUIP_CALC,
            text = "${rankEquipMaterials.size} · $allCount",
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin),
            extraContent = if (rankEquipMaterials.isEmpty()) {
                //加载提示
                {
                    CircularProgressCompose()
                }
            } else {
                null
            }
        ) {
            scope.launch {
                scrollState.scrollToItem(0)
            }
        }

        //RANK 选择
        RankRangePickerCompose(
            rank0,
            rank1,
            maxRank,
            type = RankSelectType.LIMIT
        )
    }

}

@Composable
private fun EquipCountItem(
    item: EquipmentMaterial,
    loved: Boolean,
    toEquipMaterial: (Int) -> Unit
) {
    val placeholder = item.id == ImageRequestHelper.UNKNOWN_EQUIP_ID
    var dataState by remember {
        mutableStateOf(item)
    }
    if (dataState != item) {
        dataState = item
    }

    var lovedState by remember { mutableStateOf(loved) }
    if (lovedState != loved) {
        lovedState = loved
    }

    val equipIcon: @Composable () -> Unit by remember {
        mutableStateOf({
            IconCompose(
                data = ImageRequestHelper.getInstance().getEquipPic(dataState.id),
                modifier = Modifier.commonPlaceholder(placeholder)
            ) {
                toEquipMaterial(dataState.id)
            }
        })
    }
    val count: @Composable () -> Unit by remember {
        mutableStateOf({
            SelectText(
                selected = lovedState,
                text = dataState.count.toString(),
            )
        })
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(Dimen.mediumPadding)
    ) {
        equipIcon()
        count()
    }
}

@CombinedPreviews
@Composable
private fun EquipCountItemPreview() {
    PreviewLayout {
        EquipCountItem(EquipmentMaterial(), false) { }
        EquipCountItem(EquipmentMaterial(), true) { }
    }
}