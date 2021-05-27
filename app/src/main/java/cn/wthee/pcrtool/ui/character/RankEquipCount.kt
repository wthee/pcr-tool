package cn.wthee.pcrtool.ui.character

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentMaterial
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.CharacterIdUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
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
    val curRank = navViewModel.curRank.value
    val targetRank = navViewModel.targetRank.value
    val rank0 = remember {
        mutableStateOf(1)
    }
    val rank1 = remember {
        mutableStateOf(maxRank)
    }
    if (curRank != 0) {
        rank0.value = curRank ?: 1
    }
    if (targetRank != 0) {
        rank1.value = targetRank ?: maxRank
    }
    if (rank1.value > 0 && rank0.value > 0) {
        equipmentViewModel.getEquipByRank(unitId, rank0.value, rank1.value)
    }
    val rankEquipMaterials = equipmentViewModel.rankEquipMaterials.observeAsState()

    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    if (!state.isVisible) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabOKCilck.postValue(false)
    }
    //关闭监听
    val ok = navViewModel.fabOKCilck.observeAsState().value ?: false
    if (rankEquipMaterials.value == null) {
        navViewModel.loading.postValue(true)
    }
    val filter = navViewModel.filterEquip.observeAsState()

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            //RANK 选择
            RankSelectCompose(rank0, rank1, maxRank, coroutineScope, state, navViewModel, 1)
        }
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
                    .padding(top = Dimen.mediuPadding)
                    .fillMaxWidth()
            ) {
                FadeAnimation(visible = rankEquipMaterials.value != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        //头像
                        IconCompose(
                            data = CharacterIdUtil.getMaxIconUrl(
                                unitId,
                                MainActivity.r6Ids.contains(unitId)
                            )
                        )
                        //标题
                        Row(
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier
                                .padding(
                                    top = Dimen.largePadding,
                                    bottom = Dimen.largePadding
                                )
                                .fillMaxWidth()
                        ) {
                            MainTitleText(text = stringResource(id = R.string.cur_rank))
                            RankText(
                                rank = rank0.value,
                                style = MaterialTheme.typography.subtitle1,
                            )
                            MainTitleText(text = stringResource(id = R.string.target_rank))
                            RankText(
                                rank = rank1.value,
                                style = MaterialTheme.typography.subtitle1,
                            )
                        }
                    }
                }
                //装备素材列表
                val spanCount = 5
                filter.value?.let { filterValue ->
                    filterValue.starIds =
                        GsonUtil.fromJson(mainSP().getString(Constants.SP_STAR_EQUIP, ""))
                            ?: arrayListOf()

                    if (rankEquipMaterials.value != null) {
                        navViewModel.loading.postValue(false)
                        LazyVerticalGrid(cells = GridCells.Fixed(spanCount)) {
                            items(items = rankEquipMaterials.value!!) { item ->
                                EquipCountItem(item, filterValue, toEquipMaterial)
                            }
                            items(spanCount) {
                                CommonSpacer()
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
        modifier = Modifier.padding(Dimen.smallPadding),
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