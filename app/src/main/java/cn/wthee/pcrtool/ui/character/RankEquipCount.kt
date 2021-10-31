package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentMaterial
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shape
import cn.wthee.pcrtool.ui.theme.noShape
import cn.wthee.pcrtool.utils.CharacterIdUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import kotlinx.coroutines.launch

/**
 * rank 范围装备素材数量统计
 *
 * @param unitId 角色编号
 * @param maxRank 角色最大rank
 */
@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun RankEquipCount(
    unitId: Int,
    maxRank: Int,
    toEquipMaterial: (Int) -> Unit,
    navViewModel: NavViewModel,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    val curRank = navViewModel.curRank1.value ?: 0
    val targetRank = navViewModel.targetRank1.value ?: 0
    val rank0 = remember {
        mutableStateOf(1)
    }
    val rank1 = remember {
        mutableStateOf(maxRank)
    }
    if (curRank != 0 && targetRank != 0) {
        rank0.value = curRank
        rank1.value = targetRank
    }
    val rankEquipMaterials =
        equipmentViewModel.getEquipByRank(unitId, rank0.value, rank1.value).collectAsState(
            initial = arrayListOf()
        ).value

    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    if (!state.isVisible && !state.isAnimationRunning) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabOKCilck.postValue(false)
    }
    //关闭监听
    val ok = navViewModel.fabOKCilck.observeAsState().value ?: false
    if (rankEquipMaterials.isEmpty()) {
        navViewModel.loading.postValue(true)
    }
    val filter = navViewModel.filterEquip.observeAsState()

    ModalBottomSheetLayout(
        sheetState = state,
        scrimColor = colorResource(id = if (isSystemInDarkTheme()) R.color.alpha_black else R.color.alpha_white),
        sheetElevation = Dimen.sheetElevation,
        sheetShape = if (state.offset.value == 0f) {
            noShape
        } else {
            Shape.large
        },
        sheetContent = {
            //RANK 选择
            RankSelectCompose(rank0, rank1, maxRank, coroutineScope, state, navViewModel, 1)
        },
        sheetBackgroundColor = MaterialTheme.colorScheme.surface
    ) {

        if (ok) {
            coroutineScope.launch {
                state.hide()
            }
            navViewModel.fabOKCilck.postValue(false)
        }

        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(top = Dimen.largePadding)
                    .fillMaxWidth()
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = Dimen.largePadding)
                    ) {
                        //头像
                        IconCompose(
                            data = CharacterIdUtil.getMaxIconUrl(
                                unitId,
                                MainActivity.r6Ids.contains(unitId)
                            ),
                            size = Dimen.largeIconSize
                        )
                        //标题
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            MainTitleText(text = stringResource(id = R.string.cur_rank))
                            RankText(
                                rank = rank0.value,
                                style = MaterialTheme.typography.titleMedium,
                            )
                            MainTitleText(text = stringResource(id = R.string.target_rank))
                            RankText(
                                rank = rank1.value,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }
                    }
                }
                //装备素材列表
                Card(
                    shape = CardTopShape,
                    elevation = Dimen.cardElevation,
                    modifier = Modifier
                        .padding(top = Dimen.largePadding)
                        .fillMaxSize(),
                    backgroundColor = MaterialTheme.colorScheme.surface
                ) {
                    val spanCount = 5
                    filter.value?.let { filterValue ->
                        filterValue.starIds =
                            GsonUtil.fromJson(mainSP().getString(Constants.SP_STAR_EQUIP, ""))
                                ?: arrayListOf()
                        if (rankEquipMaterials.isNotEmpty()) {
                            navViewModel.loading.postValue(false)
                            LazyVerticalGrid(
                                cells = GridCells.Fixed(spanCount),
                                contentPadding = PaddingValues(Dimen.mediumPadding)
                            ) {
                                items(items = rankEquipMaterials) { item ->
                                    EquipCountItem(item, filterValue, toEquipMaterial)
                                }
                                items(spanCount) {
                                    CommonSpacer()
                                }
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
                coroutineScope.launch {
                    if (state.isVisible) {
                        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
                        state.hide()
                    } else {
                        navViewModel.fabMainIcon.postValue(MainIconType.OK)
                        state.show()
                    }
                }
            }
        }
    }

}

@Composable
private fun EquipCountItem(
    item: EquipmentMaterial,
    filter: FilterEquipment,
    toEquipMaterial: (Int) -> Unit
) {
    val loved = filter.starIds.contains(item.id)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(Dimen.mediumPadding),
    ) {
        IconCompose(data = getEquipIconUrl(item.id)) {
            toEquipMaterial(item.id)
        }
        SelectText(
            selected = loved,
            text = item.count.toString()
        )
    }
}

@ExperimentalFoundationApi
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
            cells = GridCells.Fixed(spanCount),
            contentPadding = PaddingValues(Dimen.mediumPadding)
        ) {
            items(items = rankEquipMaterials) { item ->
                EquipCountItem(item, FilterEquipment()) { }
            }
        }
    }
}