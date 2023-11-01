package cn.wthee.pcrtool.ui.equip

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.FilterEquip
import cn.wthee.pcrtool.navigation.navigateUpSheet
import cn.wthee.pcrtool.ui.components.ChipGroup
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.utils.deleteSpace
import kotlinx.coroutines.launch


/**
 * 装备筛选
 */
@Composable
fun EquipListFilterScreen(
    equipListFilterViewModel: EquipListFilterViewModel = hiltViewModel()
) {
    val uiState by equipListFilterViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()


    MainScaffold(
        mainFabIcon = MainIconType.OK,
        onMainFabClick = {
            scope.launch {
                navigateUpSheet()
            }
        }
    ) {
        EquipListFilterContent(
            filter = uiState.filter,
            colorNum = uiState.colorNum,
            updateFilter = equipListFilterViewModel::updateFilter
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun EquipListFilterContent(
    filter: FilterEquip,
    colorNum: Int,
    updateFilter: (FilterEquip) -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    val textState = remember { mutableStateOf(filter.name) }
    filter.name = textState.value

    //合成类型
    val craftIndex = remember {
        mutableIntStateOf(1)
    }
    filter.craft = craftIndex.intValue

    //收藏筛选
    val loveIndex = remember {
        mutableIntStateOf(if (filter.all) 0 else 1)
    }
    filter.all = loveIndex.intValue == 0
    //装备类型
    val typeIndex = remember {
        mutableIntStateOf(filter.colorType)
    }
    filter.colorType = typeIndex.intValue

    //更新信息
    LaunchedEffect(
        textState.value, craftIndex.intValue, loveIndex.intValue, typeIndex.intValue
    ) {
        updateFilter(filter)
    }


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
                MainIcon(
                    data = MainIconType.EQUIP,
                    size = Dimen.fabIconSize
                )
            },
            trailingIcon = {
                MainIcon(
                    data = MainIconType.SEARCH,
                    size = Dimen.fabIconSize
                ) {
                    keyboardController?.hide()
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
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
@Composable
private fun getEquipColorText(colorType: Int): String {
    return when (colorType) {
        1 -> stringResource(id = R.string.color_blue)
        2 -> stringResource(id = R.string.color_copper)
        3 -> stringResource(id = R.string.color_silver)
        4 -> stringResource(id = R.string.color_gold)
        5 -> stringResource(id = R.string.color_purple)
        6 -> stringResource(id = R.string.color_red)
        7 -> stringResource(id = R.string.color_green)
        8 -> stringResource(id = R.string.color_orange)
        9 -> stringResource(id = R.string.color_cyan)
        else -> stringResource(id = R.string.unknown)
    }
}


@CombinedPreviews
@Composable
private fun EquipListFilterContentPreview() {
    PreviewLayout {
        EquipListFilterContent(
            colorNum = 9,
            filter = FilterEquip(),
            updateFilter = {}
        )
    }
}
