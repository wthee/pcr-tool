package cn.wthee.pcrtool.ui.tool.extraequip.filter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.ExtraEquipCategoryData
import cn.wthee.pcrtool.data.enums.ExtraEquipLevelColor
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.FilterExtraEquipment
import cn.wthee.pcrtool.navigation.navigateUpSheet
import cn.wthee.pcrtool.ui.components.ChipGroup
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainInputText
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import kotlinx.coroutines.launch


/**
 * ex装备筛选
 */
@Composable
fun ExtraEquipListFilterScreen(
    equipListFilterViewModel: ExtraEquipListFilterViewModel = hiltViewModel()
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
        ExtraEquipListFilterContent(
            filter = uiState.filter,
            colorNum = uiState.colorNum,
            equipCategoryList = uiState.equipCategoryList,
            updateFilter = equipListFilterViewModel::updateFilter
        )
    }
}

@Composable
private fun ExtraEquipListFilterContent(
    filter: FilterExtraEquipment,
    equipCategoryList: List<ExtraEquipCategoryData>,
    colorNum: Int,
    updateFilter: (FilterExtraEquipment) -> Unit
) {
    //装备名
    val textState = remember { mutableStateOf(filter.name) }
    filter.name = textState.value
    //适用场景
    val flagIndex = remember {
        mutableIntStateOf(filter.flag)
    }
    filter.flag = flagIndex.intValue
    //收藏筛选
    val favoriteIndex = remember {
        mutableIntStateOf(if (filter.all) 0 else 1)
    }
    filter.all = favoriteIndex.intValue == 0
    //装备稀有度
    val rarityIndex = remember {
        mutableIntStateOf(filter.rarity)
    }
    filter.rarity = rarityIndex.intValue
    //装备类型
    val categoryIndex = remember {
        mutableIntStateOf(filter.category)
    }
    filter.category = categoryIndex.intValue

    //更新信息
    LaunchedEffect(
        textState.value, rarityIndex.intValue, favoriteIndex.intValue, categoryIndex.intValue
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
        MainInputText(
            textState = textState,
            onDone = {},
            leadingIcon = MainIconType.EXTRA_EQUIP,
            label = stringResource(id = R.string.equip_name)
        )
        //装备类型（普通、会战）
        MainText(
            text = stringResource(id = R.string.extra_equip_flag),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val flagChipData = arrayListOf(
            ChipData(stringResource(id = R.string.all)),
            ChipData(stringResource(id = R.string.extra_equip_normal)),
            ChipData(stringResource(id = R.string.extra_equip_clan)),
        )
        ChipGroup(
            flagChipData,
            flagIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //收藏
        MainText(
            text = stringResource(id = R.string.title_favorite),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val favoriteChipData = arrayListOf(
            ChipData(stringResource(id = R.string.all)),
            ChipData(stringResource(id = R.string.favorite)),
        )
        ChipGroup(
            favoriteChipData,
            favoriteIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //稀有度
        MainText(
            text = stringResource(id = R.string.extra_equip_rarity),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val rarityChipData =
            arrayListOf(ChipData(stringResource(id = R.string.all)))
        for (i in 1..colorNum) {
            val colorType = ExtraEquipLevelColor.getByType(i)
            rarityChipData.add(ChipData(text = colorType.typeName, color = colorType.color))
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
                ChipData(stringResource(id = R.string.all)),
            )
            equipCategoryList.forEach { categoryData ->
                categoryChipData.add(ChipData(categoryData.categoryName))
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


@CombinedPreviews
@Composable
private fun ExtraEquipListFilterContentPreview() {
    PreviewLayout {
        ExtraEquipListFilterContent(
            colorNum = 4,
            equipCategoryList = arrayListOf(
                ExtraEquipCategoryData(
                    1,
                    stringResource(id = R.string.debug_short_text)
                )
            ),
            filter = FilterExtraEquipment(),
            updateFilter = {}
        )
    }
}
