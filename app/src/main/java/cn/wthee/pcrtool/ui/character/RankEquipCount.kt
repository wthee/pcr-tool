package cn.wthee.pcrtool.ui.character

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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentMaterial
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import kotlinx.coroutines.launch

@ExperimentalFoundationApi
@ExperimentalMaterialApi
@Composable
fun RankEquipCount(
    unitId: Int,
    maxRank: Int,
    toEquipDetail: (Int) -> Unit,
    navViewModel: NavViewModel,
    equipmentViewModel: EquipmentViewModel = hiltNavGraphViewModel()
) {
    val rank0 = remember {
        mutableStateOf(1)
    }
    val rank1 = remember {
        mutableStateOf(maxRank)
    }
    equipmentViewModel.getEquipByRank(unitId, rank0.value, rank1.value)
    val rankEquipMaterials = equipmentViewModel.rankEquipMaterials.observeAsState()

    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    if (!state.isVisible) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabOK.postValue(false)
    }
    //关闭监听
    val ok = navViewModel.fabOK.observeAsState().value ?: false

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
            navViewModel.fabOK.postValue(false)
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = Dimen.largePadding)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = Dimen.largePadding)
            ) {
                //标题
                Row(
                    horizontalArrangement = Arrangement.SpaceAround,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimen.largePadding)
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
                //装备素材列表
                val spanCount = 5
                val placeholder = arrayListOf<EquipmentMaterial>()
                for (i in 0 until spanCount) {
                    placeholder.add(EquipmentMaterial())
                }
                if (rankEquipMaterials.value != null) {
                    navViewModel.loading.postValue(false)
                    LazyVerticalGrid(cells = GridCells.Fixed(spanCount)) {
                        //额外添加一行占位，防止遮挡
                        val list = arrayListOf<EquipmentMaterial>()
                        list.addAll(rankEquipMaterials.value!!)
                        list.addAll(placeholder)
                        items(items = list) { item ->
                            val alpha = if (item.id == Constants.UNKNOWN_EQUIP_ID) 0f else 1f
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.alpha(alpha)
                            ) {
                                IconCompose(data = getEquipIconUrl(item.id)) {
                                    toEquipDetail(item.id)
                                }
                                MainContentText(
                                    text = item.count.toString(),
                                    modifier = Modifier.padding(bottom = Dimen.mediuPadding)
                                )
                            }
                        }
                    }
                }
            }

            if (rankEquipMaterials.value == null) {
                navViewModel.loading.postValue(true)
            }
            //选择
            ExtendedFabCompose(
                iconType = MainIconType.RANK_SELECT,
                text = stringResource(id = R.string.rank_select),
                textWidth = Dimen.getWordWidth(5f),
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