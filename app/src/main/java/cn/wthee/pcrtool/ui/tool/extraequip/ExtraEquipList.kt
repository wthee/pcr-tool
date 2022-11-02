package cn.wthee.pcrtool.ui.tool.extraequip

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import cn.wthee.pcrtool.data.db.view.ExtraEquipmentBasicInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.ExtraEquipGroupData
import cn.wthee.pcrtool.data.model.FilterExtraEquipment
import cn.wthee.pcrtool.data.model.isFilter
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.*
import cn.wthee.pcrtool.viewmodel.ExtraEquipmentViewModel
import kotlinx.coroutines.launch

/**
 * ex装备列表
 */
@OptIn(
    ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class
)
@Composable
fun ExtraEquipList(
    scrollState: LazyListState,
    viewModel: ExtraEquipmentViewModel = hiltViewModel(),
    toEquipDetail: (Int) -> Unit
) {
    //筛选状态
    val filter = navViewModel.filterExtraEquip.observeAsState()
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
            GsonUtil.fromJson(sp.getString(Constants.SP_STAR_EXTRA_EQUIP, "")) ?: arrayListOf()

        val equips by viewModel.getEquips(filterValue).collectAsState(initial = arrayListOf())
        //分组
        val equipGroupList = arrayListOf<ExtraEquipGroupData>()
        equips.forEach { equip ->
            var group = equipGroupList.find {
                it.rarity == equip.rarity && it.categoryName == equip.categoryName
            }
            if (group == null) {
                group = ExtraEquipGroupData(equip.rarity, equip.categoryName)
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
                FilterExtraEquipSheet(colorNum, state, viewModel)
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                FadeAnimation(visible = equips.isNotEmpty()) {
                    LazyColumn(state = scrollState) {
                        equipGroupList.forEach { equipGroupData ->
                            //分组标题
                            item {
                                Row(
                                    modifier = Modifier
                                        .padding(Dimen.largePadding)
                                        .fillMaxWidth()
                                        .background(
                                            getEquipColor(equipGroupData.rarity),
                                            shape = MaterialTheme.shapes.extraSmall
                                        )
                                        .padding(horizontal = Dimen.mediumPadding)
                                ) {
                                    Subtitle2(
                                        text = stringResource(
                                            id = R.string.extra_equip_rarity_and_type,
                                            equipGroupData.rarity,
                                            equipGroupData.categoryName
                                        ),
                                        color = colorWhite
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Subtitle2(
                                        text = "${equipGroupData.equipIdList.size}",
                                        color = colorWhite
                                    )
                                }
                            }
                            //分组内容
                            item {
                                VerticalGrid(
                                    spanCount = equipSpanCount,
                                    modifier = Modifier.padding(
                                        bottom = Dimen.largePadding,
                                        start = Dimen.commonItemPadding,
                                        end = Dimen.commonItemPadding
                                    ),
                                ) {
                                    equipGroupData.equipIdList.forEach { equip ->
                                        ExtraEquipItem(
                                            filterValue,
                                            equip,
                                            toEquipDetail
                                        )
                                    }
                                }
                            }
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
                        iconType = MainIconType.EXTRA_EQUIP,
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
 * ex装备
 */
@Composable
private fun ExtraEquipItem(
    filter: FilterExtraEquipment,
    equip: ExtraEquipmentBasicInfo,
    toEquipDetail: (Int) -> Unit
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
                    data = ImageResourceHelper.getInstance()
                        .getExtraEquipPic(equipState.equipmentId)
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
            .padding(horizontal = Dimen.smallPadding, vertical = Dimen.mediumPadding)
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.extraSmall)
            .clickable {
                VibrateUtil(context).single()
                toEquipDetail(equipState.equipmentId)
            }
            .padding(Dimen.smallPadding)
    ) {
        equipIcon()
        equipName()
    }
}

/**
 * ex装备筛选
 */
@OptIn(
    ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class
)
@Composable
private fun FilterExtraEquipSheet(
    colorNum: Int,
    sheetState: ModalBottomSheetState,
    extraEquipmentViewModel: ExtraEquipmentViewModel
) {
    val filter = navViewModel.filterExtraEquip.value ?: FilterExtraEquipment()

    val textState = remember { mutableStateOf(filter.name) }
    filter.name = textState.value
    //适用场景
    val flagIndex = remember {
        mutableStateOf(filter.flag)
    }
    filter.flag = flagIndex.value
    //收藏筛选
    val loveIndex = remember {
        mutableStateOf(if (filter.all) 0 else 1)
    }
    filter.all = loveIndex.value == 0
    //装备稀有度
    val rarityIndex = remember {
        mutableStateOf(filter.rarity)
    }
    filter.rarity = rarityIndex.value
    //装备类型
    val equipCategoryList by extraEquipmentViewModel.getEquipCategoryList()
        .collectAsState(initial = arrayListOf())
    val categoryIndex = remember {
        mutableStateOf(filter.category)
    }
    filter.category = categoryIndex.value


    //确认操作
    val ok = navViewModel.fabOKCilck.observeAsState().value ?: false
    val reset = navViewModel.resetClick.observeAsState().value ?: false

    //重置或确认
    LaunchedEffect(sheetState.currentValue, reset, ok) {
        if (reset) {
            textState.value = ""
            loveIndex.value = 0
            rarityIndex.value = 0
            flagIndex.value = 0
            navViewModel.resetClick.postValue(false)
            navViewModel.filterExtraEquip.postValue(FilterExtraEquipment())
        }
        if (ok) {
            sheetState.hide()
            navViewModel.filterExtraEquip.postValue(filter)
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
        //装备类型（普通、会战）
        MainText(
            text = stringResource(id = R.string.extra_equip_flag),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val flagChipData = arrayListOf(
            ChipData(0, stringResource(id = R.string.all)),
            ChipData(1, stringResource(id = R.string.extra_equip_normal)),
            ChipData(2, stringResource(id = R.string.extra_equip_clan)),
        )
        ChipGroup(
            flagChipData,
            flagIndex,
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
        //稀有度
        MainText(
            text = stringResource(id = R.string.extra_equip_rarity),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val rarityChipData =
            arrayListOf(ChipData(0, stringResource(id = R.string.all)))
        for (i in 1..colorNum) {
            rarityChipData.add(ChipData(i, getEquipColorText(i)))
        }
        ChipGroup(
            rarityChipData,
            rarityIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //装备类型
        if (equipCategoryList.isNotEmpty()) {
            MainText(
                text = stringResource(id = R.string.extra_equip_category),
                modifier = Modifier.padding(top = Dimen.largePadding)
            )
            val categoryChipData = arrayListOf(
                ChipData(0, stringResource(id = R.string.all)),
            )
            equipCategoryList.forEachIndexed { index, categoryData ->
                categoryChipData.add(ChipData(index + 1, categoryData.categoryName))
            }
            ChipGroup(
                categoryChipData,
                categoryIndex,
                modifier = Modifier.padding(Dimen.smallPadding),
            )
            CommonSpacer()
        }

        CommonSpacer()
    }
}

/**
 * 装备品级颜色名
 */
private fun getEquipColorText(colorType: Int): String {
    return when (colorType) {
        1 -> "★1"
        2 -> "★2"
        3 -> "★3"
        4 -> "★4"
        5 -> "★5"
        else -> Constants.UNKNOWN
    }
}

/**
 * 装备品级颜色
 */
private fun getEquipColor(colorType: Int): Color {
    return when (colorType) {
        1 -> colorCopper
        2 -> colorSilver
        3 -> colorGold
        4 -> colorPink
        else -> colorGray
    }
}
