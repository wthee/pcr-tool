package cn.wthee.pcrtool.ui.equip

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.data.model.isFilter
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 装备列表
 */
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun EquipList(
    navViewModel: NavViewModel,
    viewModel: EquipmentViewModel = hiltViewModel(),
    toEquipDetail: (Int) -> Unit
) {
    val equips = viewModel.equips.observeAsState().value ?: listOf()
    //筛选状态
    val filter = navViewModel.filterEquip.observeAsState()
    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    val sp = mainSP()
    val keyboardController = LocalSoftwareKeyboardController.current

    //关闭时监听
    if (!state.isVisible) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabOKCilck.postValue(false)
        keyboardController?.hide()
    }
    filter.value?.let { filterValue ->
        filterValue.starIds =
            GsonUtil.fromJson(sp.getString(Constants.SP_STAR_EQUIP, "")) ?: arrayListOf()
        viewModel.getEquips(filterValue)

        ModalBottomSheetLayout(
            sheetState = state,
            sheetContent = {
                FilterEquipSheet(navViewModel, coroutineScope, state)
            }
        ) {
            Box(
                modifier = Modifier
                    .background(color = MaterialTheme.colors.background)
                    .fillMaxSize()
            ) {
                val spanCount = 5
                SlideAnimation(visible = equips.isNotEmpty()) {
                    LazyVerticalGrid(cells = GridCells.Fixed(spanCount)) {
                        items(equips) { equip ->
                            EquipItem(filterValue, equip, toEquipDetail)
                        }
                        items(spanCount) {
                            CommonSpacer()
                        }
                    }

                }
                Row(
                    modifier = Modifier
                        .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                        .align(Alignment.BottomEnd),
                    horizontalArrangement = Arrangement.End
                ) {
                    //重置筛选
                    if (filter.value != null && filter.value!!.isFilter()) {
                        FabCompose(
                            iconType = MainIconType.RESET,
                            modifier = Modifier.padding(end = Dimen.fabSmallMarginEnd)
                        ) {
                            coroutineScope.launch {
                                state.hide()
                            }
                            navViewModel.resetClick.postValue(true)
                        }
                    }
                    val count = equips.size
                    // 数量显示&筛选按钮
                    FabCompose(
                        iconType = MainIconType.EQUIP,
                        text = "$count"
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
    }


}

/**
 * 装备
 */
@Composable
private fun EquipItem(
    filter: FilterEquipment,
    equip: EquipmentMaxData,
    toEquipDetail: (Int) -> Unit
) {
    val loved = filter.starIds.contains(equip.equipmentId)
    val nameColor = if (loved) {
        MaterialTheme.colors.primary
    } else {
        Color.Unspecified
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconCompose(
            data = getEquipIconUrl(equip.equipmentId),
            modifier = Modifier.padding(top = Dimen.mediuPadding)
        ) {
            toEquipDetail(equip.equipmentId)
        }
        //装备名称
        Text(
            text = equip.equipmentName,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Bold,
            color = nameColor,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(Dimen.smallPadding)
        )
    }
}

/**
 * 装备筛选
 */
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
private fun FilterEquipSheet(
    navViewModel: NavViewModel,
    coroutineScope: CoroutineScope,
    sheetState: ModalBottomSheetState,
    equipmentViewModel: EquipmentViewModel = hiltViewModel()
) {
    val filter = navViewModel.filterEquip.value ?: FilterEquipment()

    val textState = remember { mutableStateOf(TextFieldValue(text = filter.name)) }
    filter.name = textState.value.text
    //收藏筛选
    val loveIndex = remember {
        mutableStateOf(if (filter.all) 0 else 1)
    }
    filter.all = loveIndex.value == 0
    //装备类型
    equipmentViewModel.getTypes()
    val typeList = equipmentViewModel.equipTypes.observeAsState().value ?: arrayListOf()
    val typeIndex = remember {
        mutableStateOf(filter.type)
    }
    filter.type = typeIndex.value

    //确认操作
    val ok = navViewModel.fabOKCilck.observeAsState().value ?: false
    val reset = navViewModel.resetClick.observeAsState().value ?: false

    //选择状态
    Column(
        modifier = Modifier
            .clip(CardTopShape)
            .fillMaxWidth()
            .padding(Dimen.mediuPadding)
            .verticalScroll(rememberScrollState())
    ) {
        if (reset) {
            coroutineScope.launch {
                sheetState.hide()
            }
            textState.value = TextFieldValue(text = "")
            loveIndex.value = 0
            typeIndex.value = 0
            navViewModel.resetClick.postValue(false)
            navViewModel.filterEquip.postValue(FilterEquipment())
        }
        if (ok) {
            coroutineScope.launch {
                sheetState.hide()
            }
            navViewModel.fabOKCilck.postValue(false)
        }
        //装备名搜索
        val keyboardController = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            textStyle = MaterialTheme.typography.button,
            leadingIcon = {
                IconCompose(
                    data = MainIconType.EQUIP.icon,
                    modifier = Modifier.size(Dimen.fabIconSize)
                )
            },
            trailingIcon = {
                IconCompose(
                    data = MainIconType.SEARCH.icon,
                    modifier = Modifier.size(Dimen.fabIconSize)
                ) {
                    keyboardController?.hide()
                    navViewModel.fabOKCilck.postValue(true)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    navViewModel.fabOKCilck.postValue(true)
                }
            ),
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
        //收藏
        MainText(
            text = stringResource(id = R.string.title_love),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val loveChipData = arrayListOf(
            ChipData(0, stringResource(id = R.string.all)),
            ChipData(1, stringResource(id = R.string.loved)),
        )
        ChipGroup(
            loveChipData,
            loveIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //类型
        MainText(
            text = stringResource(id = R.string.equip_type),
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

