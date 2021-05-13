package cn.wthee.pcrtool.ui.equip

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.EquipmentMaxData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
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
    val filter = navViewModel.filterEquip.observeAsState()
    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val sp = context.getSharedPreferences("main", Context.MODE_PRIVATE)

    //关闭时监听
    if (!state.isVisible) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabOK.postValue(false)
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
            Box(modifier = Modifier.fillMaxSize()) {
                LazyVerticalGrid(
                    cells = GridCells.Fixed(4),
                    modifier = Modifier
                        .padding(Dimen.mediuPadding)
                        .background(color = MaterialTheme.colors.background, shape = CardTopShape)
                ) {
                    items(equips) { equip ->
                        EquipItem(filterValue, equip, toEquipDetail)
                    }
                    items(4) {
                        CommonSpacer()
                    }
                }
                val count = equips.size
                // 数量显示&筛选按钮
                ExtendedFabCompose(
                    modifier = Modifier
                        .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                        .align(Alignment.BottomEnd),
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.smallPadding),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconCompose(data = getEquipIconUrl(equip.equipmentId)) {
            toEquipDetail(equip.equipmentId)
        }
        //装备名称
        Text(
            text = equip.equipmentName,
            style = MaterialTheme.typography.caption,
            maxLines = 1,
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
@ExperimentalMaterialApi
@Composable
private fun FilterEquipSheet(
    navViewModel: NavViewModel,
    coroutineScope: CoroutineScope,
    sheetState: ModalBottomSheetState,
    equipmentViewModel: EquipmentViewModel = hiltNavGraphViewModel()
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

