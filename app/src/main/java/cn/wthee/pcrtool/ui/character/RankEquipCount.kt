package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RankSelectType
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi

/**
 * rank 范围装备素材数量统计
 *
 * @param unitId 角色编号
 * @param maxRank 角色最大rank
 */
@OptIn(ExperimentalMaterialNavigationApi::class)
@Composable
fun RankEquipCount(
    unitId: Int,
    maxRank: Int,
    toEquipMaterial: (Int) -> Unit,
    navViewModel: NavViewModel,
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

    val filter = navViewModel.filterEquip.observeAsState()

    val dialogState = remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .padding(top = Dimen.largePadding)
                .fillMaxWidth()
        ) {
            filter.value?.let { filterValue ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = Dimen.largePadding)
                ) {
                    //标题
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MainTitleText(text = stringResource(id = R.string.cur_rank))
                        RankText(
                            rank = rank0.value,
                        )
                        MainTitleText(text = stringResource(id = R.string.target_rank))
                        RankText(
                            rank = rank1.value,
                        )
                    }
                }
                //装备素材列表
                filterValue.starIds =
                    GsonUtil.fromJson(mainSP().getString(Constants.SP_STAR_EQUIP, ""))
                        ?: arrayListOf()
                if (rankEquipMaterials.isNotEmpty()) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(Dimen.iconSize + Dimen.mediumPadding * 2),
                        contentPadding = PaddingValues(Dimen.mediumPadding)
                    ) {
                        items(
                            items = rankEquipMaterials,
                            key = {
                                it.id
                            }
                        ) { item ->
                            EquipCountItem(item, filterValue, toEquipMaterial)
                        }
                        items(5) {
                            CommonSpacer()
                        }
                    }
                } else {
                    //加载中
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(Dimen.iconSize + Dimen.mediumPadding * 2),
                        contentPadding = PaddingValues(Dimen.mediumPadding)
                    ) {
                        items(count =10) {
                            EquipCountItem(EquipmentMaterial(), filterValue, toEquipMaterial)
                        }
                    }
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
    filter: FilterEquipment,
    toEquipMaterial: (Int) -> Unit
) {
    val placeholder = item.id == ImageResourceHelper.UNKNOWN_EQUIP_ID
    val loved = filter.starIds.contains(item.id)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(Dimen.mediumPadding)
    ) {
        IconCompose(
            modifier = Modifier.commonPlaceholder(visible = placeholder),
            data = ImageResourceHelper.getInstance().getEquipPic(item.id)
        ) {
            toEquipMaterial(item.id)
        }
        SelectText(
            selected = loved,
            text = item.count.toString()
        )
    }
}

@Preview
@Composable
private fun EquipCountItemPreview() {
    val spanCount = 5
    val rankEquipMaterials = arrayListOf<EquipmentMaterial>()
    for (i in 0..11) {
        rankEquipMaterials.add(EquipmentMaterial())
    }
    PreviewBox {
        LazyVerticalGrid(
            columns = GridCells.Fixed(spanCount),
            contentPadding = PaddingValues(Dimen.mediumPadding)
        ) {
            items(items = rankEquipMaterials) { item ->
                EquipCountItem(item, FilterEquipment()) { }
            }
        }
    }
}