package cn.wthee.pcrtool.ui.character

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RankSelectType
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.data.model.getStarEquipIdList
import cn.wthee.pcrtool.ui.components.*
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
        mutableIntStateOf(maxRank)
    }
    val rank1 = remember {
        mutableIntStateOf(maxRank)
    }

    val rankEquipMaterialsFlow = remember(unitId, rank0.intValue, rank1.intValue) {
        equipmentViewModel.getEquipByRank(unitId, rank0.intValue, rank1.intValue)
    }
    val rankEquipMaterials by rankEquipMaterialsFlow.collectAsState(initial = arrayListOf())
    //收藏装备列表
    val starIds = getStarEquipIdList()


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
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
                    rank = rank0.intValue,
                    modifier = Modifier.padding(start = Dimen.mediumPadding)
                )
                MainContentText(
                    text = stringResource(id = R.string.to),
                    modifier = Modifier.padding(horizontal = Dimen.smallPadding)
                )
                RankText(
                    rank = rank1.intValue
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
                            starIds.contains(item.id),
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
        MainSmallFab(
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(Dimen.mediumPadding)
    ) {
        MainIcon(
            data = ImageRequestHelper.getInstance().getEquipPic(item.id),
            modifier = Modifier.commonPlaceholder(placeholder)
        ) {
            toEquipMaterial(item.id)
        }
        SelectText(
            selected = loved,
            text = item.count.toString(),
        )
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