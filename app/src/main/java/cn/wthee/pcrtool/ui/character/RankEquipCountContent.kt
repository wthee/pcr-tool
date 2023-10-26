package cn.wthee.pcrtool.ui.character

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RankSelectType
import cn.wthee.pcrtool.data.model.EquipmentMaterial
import cn.wthee.pcrtool.data.model.getStarEquipIdList
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.components.*
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
fun RankEquipCountContent(
    toEquipMaterial: (Int) -> Unit,
    rankEquipCountViewModel: RankEquipCountViewModel = hiltViewModel()
) {
    val uiState by rankEquipCountViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(uiState.maxRank) {
        if (uiState.maxRank == 0) {
            rankEquipCountViewModel.loadData()
        }
    }

    val openDialog = remember {
        mutableStateOf(false)
    }

    //返回监听
    val scope = rememberCoroutineScope()
    BackHandler(!openDialog.value) {
        scope.launch {
            navigateUp()
        }
    }

    //收藏装备列表
    val starIds = getStarEquipIdList()

    val scrollState = rememberLazyGridState()

    MainScaffold(
        modifier = Modifier.padding(top = Dimen.largePadding),
        floatingActionButton = {
            //回到顶部
            var allCount = 0
            uiState.equipmentMaterialList?.forEach {
                allCount += it.count
            }
            MainSmallFab(
                iconType = MainIconType.EQUIP_CALC,
                text = "${uiState.equipmentMaterialList?.size ?: 0} · $allCount",
            ) {
                scope.launch {
                    scrollState.scrollToItem(0)
                }
            }
        },
        secondLineFloatingActionButton = {
            //RANK 选择
            RankRangePickerCompose(
                rank0 = uiState.rank0,
                rank1 = uiState.rank1,
                maxRank = uiState.maxRank,
                openDialog = openDialog,
                updateRank = rankEquipCountViewModel::updateRank,
                type = RankSelectType.LIMIT
            )
        },
        onMainFabClick = {
            scope.launch {
                if (openDialog.value) {
                    openDialog.value = false
                } else {
                    navigateUp()
                }
            }
        },
        enableClickClose = openDialog.value,
        onCloseClick = {
            openDialog.value = false
        }
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
                MainTitleText(text = stringResource(id = if (uiState.isAllUnit) R.string.all_unit_calc_equip else R.string.calc_equip_count))
                RankText(
                    rank = uiState.rank0,
                    modifier = Modifier.padding(start = Dimen.mediumPadding)
                )
                MainContentText(
                    text = stringResource(id = R.string.to),
                    modifier = Modifier.padding(horizontal = Dimen.smallPadding)
                )
                RankText(
                    rank = uiState.rank1
                )
            }


            LazyVerticalGrid(
                columns = GridCells.Adaptive(Dimen.iconSize + Dimen.mediumPadding),
                contentPadding = PaddingValues(horizontal = Dimen.mediumPadding),
                state = scrollState
            ) {
                when (uiState.loadingState) {
                    LoadingState.Success -> {
                        items(
                            items = uiState.equipmentMaterialList!!,
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
                    }

                    LoadingState.Loading -> {
                        items(count = 10) {
                            EquipCountItem(EquipmentMaterial(), false, toEquipMaterial)
                        }
                    }

                    LoadingState.Error -> {
                        items(count = 10) {
                            EquipCountItem(EquipmentMaterial(-1), false, toEquipMaterial)
                        }
                    }

                    LoadingState.NoData -> {}
                }
                items(10) {
                    CommonSpacer()
                }
            }
        }

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