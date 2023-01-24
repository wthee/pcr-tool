package cn.wthee.pcrtool.ui.character

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel

/**
 * rank 范围装备素材数量统计
 *
 * @param unitId 角色编号
 * @param maxRank 角色最大rank
 */
@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterialApi::class, ExperimentalLayoutApi::class)
@Composable
fun RankEquipCount(
    unitId: Int,
    maxRank: Int,
    toEquipMaterial: (Int) -> Unit,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
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

    val dialogState = remember {
        mutableStateOf(false)
    }

    val starIds = remember {
        mutableStateOf(arrayListOf<Int>())
    }
    LaunchedEffect(MainActivity.navSheetState.currentValue) {
        starIds.value = FilterEquipment.getStarIdList()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(top = Dimen.mediumPadding)
                .fillMaxWidth()
        ) {
            //标题
            FlowRow(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimen.mediumPadding)
            ) {
                Row(
                    modifier = Modifier
                        .padding(top = Dimen.mediumPadding, end = Dimen.mediumPadding)
                ) {
                    MainTitleText(text = stringResource(id = R.string.cur_rank))
                    RankText(
                        rank = rank0.value,
                        modifier = Modifier.padding(start = Dimen.smallPadding)
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(top = Dimen.mediumPadding)
                ) {
                    MainTitleText(text = stringResource(id = R.string.target_rank))
                    RankText(
                        rank = rank1.value,
                        modifier = Modifier.padding(start = Dimen.smallPadding)
                    )
                }

            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(Dimen.iconSize + Dimen.mediumPadding),
                contentPadding = PaddingValues(horizontal = Dimen.mediumPadding)
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
                items(5) {
                    CommonSpacer()
                }
            }
        }

        //选择
        FabCompose(
            iconType = MainIconType.RANK_SELECT,
            text = stringResource(id = R.string.rank_select),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
        ) {
            dialogState.value = true
        }

        //RANK 选择
        if (dialogState.value) {
            RankSelectCompose(
                rank0,
                rank1,
                maxRank,
                dialogState,
                type = RankSelectType.LIMIT
            )
        }
    }

}

@Composable
private fun EquipCountItem(
    item: EquipmentMaterial,
    loved: Boolean,
    toEquipMaterial: (Int) -> Unit
) {
    val placeholder = item.id == ImageResourceHelper.UNKNOWN_EQUIP_ID
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
                data = ImageResourceHelper.getInstance().getEquipPic(dataState.id),
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