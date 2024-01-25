package cn.wthee.pcrtool.ui.character.equipcount

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RankSelectType
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.LifecycleEffect
import cn.wthee.pcrtool.ui.components.MainContentText
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainTitleText
import cn.wthee.pcrtool.ui.components.RankRangePickerCompose
import cn.wthee.pcrtool.ui.components.RankText
import cn.wthee.pcrtool.ui.components.SelectText
import cn.wthee.pcrtool.ui.components.placeholder
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.ImageRequestHelper
import kotlinx.coroutines.launch

/**
 * rank 范围装备素材数量统计
 */
@SuppressLint("MutableCollectionMutableState")
@Composable
fun RankEquipCountScreen(
    toEquipMaterial: (Int, String) -> Unit,
    rankEquipCountViewModel: RankEquipCountViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val uiState by rankEquipCountViewModel.uiState.collectAsStateWithLifecycle()

    //初始收藏信息
    LifecycleEffect(Lifecycle.Event.ON_RESUME) {
        rankEquipCountViewModel.reloadFavoriteList()
    }

    val scrollState = rememberLazyGridState()

    MainScaffold(
        fab = {
            //回到顶部
            var allCount = 0
            uiState.equipmentMaterialList?.forEach {
                allCount += it.count
            }
            MainSmallFab(
                iconType = MainIconType.EQUIP_CALC,
                text = "${uiState.equipmentMaterialList?.size ?: 0} · $allCount",
                onClick = {
                    scope.launch {
                        scrollState.scrollToItem(0)
                    }
                }
            )
        },
        secondLineFab = {
            //RANK 选择
            RankRangePickerCompose(
                rank0 = uiState.rank0,
                rank1 = uiState.rank1,
                maxRank = uiState.maxRank,
                openDialog = uiState.openDialog,
                updateRank = rankEquipCountViewModel::updateRank,
                changeDialog = rankEquipCountViewModel::changeDialog,
                type = RankSelectType.LIMIT
            )
        },
        onMainFabClick = {
            scope.launch {
                if (uiState.openDialog) {
                    rankEquipCountViewModel.changeDialog(false)
                } else {
                    navigateUp()
                }
            }
        },
        mainFabIcon = if (uiState.openDialog) MainIconType.CLOSE else MainIconType.BACK,
        enableClickClose = uiState.openDialog,
        onCloseClick = {
            rankEquipCountViewModel.changeDialog(false)
        }
    ) {
        RankEquipCountContent(
            rank0 = uiState.rank0,
            rank1 = uiState.rank1,
            isAllUnit = uiState.isAllUnit,
            loadState = uiState.loadState,
            equipmentMaterialList = uiState.equipmentMaterialList,
            scrollState = scrollState,
            favoriteIdList = uiState.favoriteIdList,
            toEquipMaterial = toEquipMaterial
        )
    }

}

@Composable
private fun RankEquipCountContent(
    rank0: Int,
    rank1: Int,
    isAllUnit: Boolean,
    loadState: LoadState,
    equipmentMaterialList: List<EquipmentMaterial>?,
    scrollState: LazyGridState,
    favoriteIdList: List<Int>,
    toEquipMaterial: (Int, String) -> Unit,
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
                rank = rank0,
                modifier = Modifier.padding(start = Dimen.mediumPadding)
            )
            MainContentText(
                text = stringResource(id = R.string.to),
                modifier = Modifier.padding(horizontal = Dimen.smallPadding)
            )
            RankText(
                rank = rank1
            )
        }


        LazyVerticalGrid(
            columns = GridCells.Adaptive(Dimen.iconSize + Dimen.mediumPadding),
            contentPadding = PaddingValues(horizontal = Dimen.mediumPadding),
            state = scrollState
        ) {
            when (loadState) {
                LoadState.Success -> {
                    items(
                        items = equipmentMaterialList!!,
                        key = {
                            it.id
                        }
                    ) { item ->
                        EquipCountItem(
                            item = item,
                            favorite = favoriteIdList.contains(item.id),
                            toEquipMaterial = toEquipMaterial
                        )
                    }
                }

                LoadState.Loading -> {
                    items(count = 10) {
                        EquipCountItem(
                            item = EquipmentMaterial(),
                            favorite = false,
                            toEquipMaterial = toEquipMaterial
                        )
                    }
                }

                LoadState.Error -> {
                    items(count = 10) {
                        EquipCountItem(
                            item = EquipmentMaterial(-1),
                            favorite = false,
                            toEquipMaterial = toEquipMaterial
                        )
                    }
                }

                LoadState.NoData -> {}
            }
            items(10) {
                CommonSpacer()
            }
        }
    }
}

@Composable
private fun EquipCountItem(
    item: EquipmentMaterial,
    favorite: Boolean,
    toEquipMaterial: (Int, String) -> Unit,
) {
    val placeholder = item.id == ImageRequestHelper.UNKNOWN_EQUIP_ID

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(Dimen.mediumPadding)
    ) {
        MainIcon(
            data = ImageRequestHelper.getInstance()
                .getUrl(ImageRequestHelper.ICON_EQUIPMENT, item.id),
            modifier = Modifier.placeholder(placeholder),
            onClick = {
                toEquipMaterial(item.id, item.name)
            }
        )
        SelectText(
            selected = favorite,
            text = item.count.toString(),
        )
    }
}


@CombinedPreviews
@Composable
private fun RankEquipCountContentPreview() {
    PreviewLayout {
        RankEquipCountContent(
            rank0 = 1,
            rank1 = 22,
            isAllUnit = false,
            loadState = LoadState.Success,
            equipmentMaterialList = arrayListOf(EquipmentMaterial(1), EquipmentMaterial(2)),
            scrollState = rememberLazyGridState(),
            favoriteIdList = arrayListOf(1),
            toEquipMaterial = { _, _ -> }
        )
    }
}