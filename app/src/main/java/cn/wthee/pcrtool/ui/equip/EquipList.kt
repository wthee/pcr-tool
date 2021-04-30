package cn.wthee.pcrtool.ui.equip

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 装备列表
 */
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun EquipList(
    navViewModel: NavViewModel,
    viewModel: EquipmentViewModel = hiltNavGraphViewModel(),
    toEquipDetail: (Int) -> Unit
) {
    val equips = viewModel.equips.observeAsState().value ?: listOf()
    //筛选状态
    val filter = viewModel.filter.observeAsState()
    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    //滚动监听
    val scrollState = rememberLazyListState()

    filter.value?.let {
        viewModel.getEquips(it)
    }

    //关闭时监听
    if (!state.isVisible) {
        navViewModel.fabMainIcon.postValue(R.drawable.ic_back)
        navViewModel.fabOK.postValue(false)
    }

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            FilterEquipSheet(navViewModel, coroutineScope, state)
        }
    ) {
        val marginTop: Dp = marginTopBar(scrollState)
        Box(modifier = Modifier.fillMaxSize()) {
            TopBarCompose(
                titleId = R.string.tool_equip,
                iconId = R.drawable.ic_equip,
                scrollState = scrollState
            )
            LazyVerticalGrid(
                cells = GridCells.Fixed(5),
                state = scrollState,
                modifier = Modifier
                    .padding(top = marginTop)
                    .fillMaxSize()
                    .background(color = MaterialTheme.colors.background, shape = CardTopShape)
            ) {
                items(equips) { equip ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(Dimen.smallPadding),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconCompose(
                            data = getEquipIconUrl(equip.equipmentId),
                            modifier = Modifier
                                .size(Dimen.iconSize)
                                .clickable {
                                    toEquipDetail(equip.equipmentId)
                                }
                        )
                        //装备名称
                        Text(
                            text = equip.equipmentName,
                            style = MaterialTheme.typography.caption,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(Dimen.smallPadding)
                        )
                    }

                }
            }
            val count = equips.size
            // 数量显示&筛选按钮
            ExtendedFabCompose(
                modifier = Modifier
                    .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                    .align(Alignment.BottomEnd),
                iconId = R.drawable.ic_equip,
                text = "$count"
            ) {
                coroutineScope.launch {
                    if (state.isVisible) {
                        navViewModel.fabMainIcon.postValue(R.drawable.ic_back)
                        state.hide()
                    } else {
                        navViewModel.fabMainIcon.postValue(R.drawable.ic_ok)
                        state.show()
                    }
                }
            }
        }

    }


}

/**
 * 装备筛选
 */
@ExperimentalMaterialApi
@Composable
private fun FilterEquipSheet(
    navViewModel: NavViewModel,
    coroutineScope: CoroutineScope,
    sheetState: ModalBottomSheetState,
    equipmentViewModel: EquipmentViewModel = hiltNavGraphViewModel()
) {
    val textState = remember { mutableStateOf(TextFieldValue()) }
    val newFilter = FilterEquipment()
    newFilter.name = textState.value.text
    //公会
    equipmentViewModel.getTypes()
    val typeList = equipmentViewModel.equipTypes.observeAsState().value ?: arrayListOf()
    val typeIndex = remember {
        mutableStateOf(0)
    }
    if (typeList.isNotEmpty()) {
        newFilter.type = if (typeIndex.value == 0) {
            "全部"
        } else {
            typeList[typeIndex.value - 1]
        }
    }

    //确认操作
    val ok = navViewModel.fabOK.observeAsState().value ?: false

    //选择状态
    Column(
        modifier = Modifier
            .clip(CardTopShape)
            .fillMaxWidth()
            .padding(Dimen.mediuPadding)
            .verticalScroll(rememberScrollState())
    ) {
        if (ok) {
            coroutineScope.launch {
                sheetState.hide()
            }
            navViewModel.fabOK.postValue(false)
            navViewModel.fabMainIcon.postValue(R.drawable.ic_function)
            equipmentViewModel.filter.postValue(newFilter)
        }
        //装备名搜索
        OutlinedTextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            textStyle = MaterialTheme.typography.button,
            singleLine = false,
            label = {
                Text(
                    text = stringResource(id = R.string.equip_name),
                    style = MaterialTheme.typography.button
                )
            },
            modifier = Modifier
                .padding(Dimen.largePadding)
                .fillMaxWidth()
        )
        //类型
        MainText(
            text = stringResource(id = R.string.title_guild),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val typeChipData =
            arrayListOf(ChipData(0, stringResource(id = R.string.all)))
        typeList.forEachIndexed { index, type ->
            typeChipData.add(ChipData(index + 1, type))
        }
        ChipGroup(
            typeChipData,
            typeIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
    }
}

