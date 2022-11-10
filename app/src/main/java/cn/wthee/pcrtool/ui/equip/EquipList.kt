package cn.wthee.pcrtool.ui.equip

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentBasicInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.EquipGroupData
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.data.model.isFilter
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.EquipmentViewModel
import kotlinx.coroutines.launch

/**
 * 装备列表
 */
@OptIn(
    ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class
)
@Composable
fun EquipList(
    scrollState: LazyListState,
    viewModel: EquipmentViewModel = hiltViewModel(),
    toEquipDetail: (Int) -> Unit,
    toEquipMaterial: (Int) -> Unit,
) {
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
    if (!state.isVisible && !state.isAnimationRunning) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabOKCilck.postValue(false)
        keyboardController?.hide()
    }

    val colorNum by viewModel.getEquipColorNum().collectAsState(initial = 0)
    val equipSpanCount =
        ScreenUtil.getWidth() / (Dimen.iconSize * 3 + Dimen.largePadding * 2).value.dp2px

    filter.value?.let { filterValue ->
        filterValue.starIds =
            GsonUtil.fromJson(sp.getString(Constants.SP_STAR_EQUIP, "")) ?: arrayListOf()

        val equips by viewModel.getEquips(filterValue).collectAsState(initial = arrayListOf())
        //分组
        val equipGroupList = arrayListOf<EquipGroupData>()
        equips.forEach { equip ->
            var group = equipGroupList.find {
                it.promotionLevel == equip.promotionLevel && it.requireLevel == equip.requireLevel
            }
            if (group == null) {
                group = EquipGroupData(equip.promotionLevel, equip.requireLevel)
                equipGroupList.add(group)
            }
            group.equipIdList.add(equip)
        }

        ModalBottomSheetLayout(
            sheetState = state,
            scrimColor = if (isSystemInDarkTheme()) colorAlphaBlack else colorAlphaWhite,
            sheetBackgroundColor = MaterialTheme.colorScheme.surface,
            sheetShape = shapeTop(),
            sheetContent = {
                FilterEquipSheet(colorNum, state)
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                FadeAnimation(visible = equips.isNotEmpty()) {
                    LazyColumn(state = scrollState) {
                        items(items = equipGroupList,
                            key = {
                                "${it.requireLevel}-${it.promotionLevel}"
                            }) { equipGroupData ->
                            EquipGroup(
                                equipGroupData,
                                equipSpanCount,
                                filterValue,
                                toEquipDetail,
                                toEquipMaterial
                            )
                        }
                        item {
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
                    //回到顶部
                    FabCompose(
                        iconType = MainIconType.TOP
                    ) {
                        coroutineScope.launch {
                            scrollState.scrollToItem(0)
                        }
                    }
                    //重置筛选
                    if (filter.value != null && filter.value!!.isFilter()) {
                        FabCompose(
                            iconType = MainIconType.RESET
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
                            navViewModel.fabMainIcon.postValue(MainIconType.OK)
                            state.show()
                        }
                    }
                }

            }

        }
    }


}

/**
 * 装备分组
 */
@Composable
private fun EquipGroup(
    equipGroupData: EquipGroupData,
    equipSpanCount: Int,
    filterValue: FilterEquipment,
    toEquipDetail: (Int) -> Unit,
    toEquipMaterial: (Int) -> Unit
) {
    //分组标题
    CommonGroupTitle(
        titleStart = stringResource(
            id = R.string.equip_require_level,
            equipGroupData.requireLevel
        ),
        titleEnd = equipGroupData.equipIdList.size.toString(),
        modifier = Modifier.padding(Dimen.largePadding)
    )

    //分组内容
    VerticalGrid(
        spanCount = equipSpanCount,
        modifier = Modifier.padding(
            bottom = Dimen.largePadding,
            start = Dimen.commonItemPadding,
            end = Dimen.commonItemPadding
        ),
    ) {
        equipGroupData.equipIdList.forEach { equip ->
            EquipItem(
                filterValue,
                equip,
                toEquipDetail,
                toEquipMaterial
            )
        }
    }
}

/**
 * 装备
 */
@Composable
private fun EquipItem(
    filter: FilterEquipment,
    equip: EquipmentBasicInfo,
    toEquipDetail: (Int) -> Unit,
    toEquipMaterial: (Int) -> Unit,
) {
    val context = LocalContext.current

    var equipState by remember { mutableStateOf(equip) }
    if (equipState != equip) {
        equipState = equip
    }
    var filterState by remember { mutableStateOf(filter) }
    if (filterState != filter) {
        filterState = filter
    }


    val equipIcon: @Composable () -> Unit by remember {
        mutableStateOf(
            {
                IconCompose(
                    data = ImageResourceHelper.getInstance().getEquipPic(equipState.equipmentId)
                )
            }
        )
    }
    val equipName: @Composable () -> Unit by remember {
        mutableStateOf(
            {
                MainContentText(
                    text = equipState.equipmentName,
                    textAlign = TextAlign.Start,
                    maxLines = 2,
                    selectable = true,
                    modifier = Modifier.padding(start = Dimen.smallPadding),
                    color = if (filterState.starIds.contains(equipState.equipmentId)) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        )
    }

    Row(
        modifier = Modifier
            .padding(
                start = Dimen.smallPadding,
                end = Dimen.smallPadding,
                bottom = Dimen.mediumPadding
            )
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable {
                VibrateUtil(context).single()
                if (equip.craftFlg == 1) {
                    toEquipDetail(equipState.equipmentId)
                } else {
                    toEquipMaterial(equipState.equipmentId)
                }
            }
            .padding(Dimen.smallPadding)
    ) {
        equipIcon()
        equipName()
    }
}

/**
 * 装备筛选
 */
@OptIn(
    ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
private fun FilterEquipSheet(
    colorNum: Int,
    sheetState: ModalBottomSheetState
) {
    val filter = navViewModel.filterEquip.value ?: FilterEquipment()

    val textState = remember { mutableStateOf(filter.name) }
    filter.name = textState.value
    //合成类型
    val craftIndex = remember {
        mutableStateOf(filter.craft)
    }
    filter.craft = craftIndex.value
    //收藏筛选
    val loveIndex = remember {
        mutableStateOf(if (filter.all) 0 else 1)
    }
    filter.all = loveIndex.value == 0
    //装备类型
    val typeIndex = remember {
        mutableStateOf(filter.colorType)
    }
    filter.colorType = typeIndex.value

    //确认操作
    val ok = navViewModel.fabOKCilck.observeAsState().value ?: false
    val reset = navViewModel.resetClick.observeAsState().value ?: false

    //重置或确认
    LaunchedEffect(sheetState.currentValue, reset, ok) {
        if (reset) {
            textState.value = ""
            loveIndex.value = 0
            typeIndex.value = 0
            craftIndex.value = 1
            navViewModel.resetClick.postValue(false)
            navViewModel.filterEquip.postValue(FilterEquipment())
        }
        if (ok) {
            sheetState.hide()
            navViewModel.filterEquip.postValue(filter)
            navViewModel.fabOKCilck.postValue(false)
            navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        }
    }

    val keyboardController = LocalSoftwareKeyboardController.current


    //选择状态
    Column(
        modifier = Modifier
            .padding(start = Dimen.largePadding, end = Dimen.largePadding)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        //装备名搜索
        OutlinedTextField(
            value = textState.value,
            shape = MaterialTheme.shapes.medium,
            onValueChange = { textState.value = it.deleteSpace },
            textStyle = MaterialTheme.typography.labelLarge,
            leadingIcon = {
                IconCompose(
                    data = MainIconType.EQUIP,
                    size = Dimen.fabIconSize
                )
            },
            trailingIcon = {
                IconCompose(
                    data = MainIconType.SEARCH,
                    size = Dimen.fabIconSize
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
            maxLines = 1,
            singleLine = true,
            label = {
                Text(
                    text = stringResource(id = R.string.equip_name),
                    style = MaterialTheme.typography.labelLarge
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        //装备类型
        MainText(
            text = stringResource(id = R.string.equip_craft),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val craftChipData = arrayListOf(
            ChipData(0, stringResource(id = R.string.uncraft)),
            ChipData(1, stringResource(id = R.string.craft)),
        )
        ChipGroup(
            craftChipData,
            craftIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
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
        //品级
        MainText(
            text = stringResource(id = R.string.equip_level_color),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val colorChipData =
            arrayListOf(ChipData(0, stringResource(id = R.string.all)))
        for (i in 1..colorNum) {
            colorChipData.add(ChipData(i, getEquipColorText(i)))
        }
        ChipGroup(
            colorChipData,
            typeIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        CommonSpacer()
    }
}

/**
 * 装备品级颜色名
 */
private fun getEquipColorText(colorType: Int): String {
    return when (colorType) {
        1 -> "蓝"
        2 -> "铜"
        3 -> "银"
        4 -> "金"
        5 -> "紫"
        6 -> "红"
        7 -> "绿"
        8 -> "橙"
        else -> Constants.UNKNOWN
    }
}

/**
 * 装备品级颜色
 */
private fun getEquipColor(colorType: Int): Color {
    return when (colorType) {
        1 -> colorBlue
        2 -> colorCopper
        3 -> colorSilver
        4 -> colorGold
        5 -> colorPurple
        6 -> colorRed
        7 -> colorGreen
        8 -> colorOrange
        else -> colorGray
    }
}
