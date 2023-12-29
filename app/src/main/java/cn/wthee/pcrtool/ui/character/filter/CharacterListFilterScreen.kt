package cn.wthee.pcrtool.ui.character.filter

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
import cn.wthee.pcrtool.data.db.view.GuildData
import cn.wthee.pcrtool.data.enums.AtkType
import cn.wthee.pcrtool.data.enums.CharacterLimitType
import cn.wthee.pcrtool.data.enums.CharacterSortType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.PositionType
import cn.wthee.pcrtool.data.enums.getSortType
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.navigation.navigateUpSheet
import cn.wthee.pcrtool.ui.components.ChipGroup
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorPink
import cn.wthee.pcrtool.utils.deleteSpace
import kotlinx.coroutines.launch


/**
 * 角色筛选
 */
@Composable
fun CharacterListFilterScreen(
    characterListFilterViewModel: CharacterListFilterViewModel = hiltViewModel()
) {
    val uiState by characterListFilterViewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()

    MainScaffold(
        mainFabIcon = MainIconType.OK,
        onMainFabClick = {
            scope.launch {
                navigateUpSheet()
            }
        }
    ) {
        CharacterListFilterContent(
            filter = uiState.filter,
            raceList = uiState.raceList,
            guildList = uiState.guildList,
            updateFilter = characterListFilterViewModel::updateFilter
        )
    }
}

@Composable
@OptIn(ExperimentalComposeUiApi::class)
private fun CharacterListFilterContent(
    filter: FilterCharacter,
    raceList: List<String>,
    guildList: List<GuildData>,
    updateFilter: (FilterCharacter) -> Unit
) {
    //名称
    val textState = remember { mutableStateOf(filter.name) }
    filter.name = textState.value

    //排序类型筛选
    val sortTypeIndex = remember {
        mutableIntStateOf(filter.sortType.type)
    }
    filter.sortType = getSortType(sortTypeIndex.intValue)

    //排序方式筛选
    val sortAscIndex = remember {
        mutableIntStateOf(if (filter.asc) 0 else 1)
    }
    filter.asc = sortAscIndex.intValue == 0

    //收藏筛选
    val loveIndex = remember {
        mutableIntStateOf(if (filter.all) 0 else 1)
    }
    filter.all = loveIndex.intValue == 0

    //六星筛选
    val r6Index = remember {
        mutableIntStateOf(filter.r6)
    }
    filter.r6 = r6Index.intValue

    //位置筛选
    val positionIndex = remember {
        mutableIntStateOf(filter.position)
    }
    filter.position = positionIndex.intValue

    //攻击类型
    val atkIndex = remember {
        mutableIntStateOf(filter.atk)
    }
    filter.atk = atkIndex.intValue

    //公会
    val guildIndex = remember {
        mutableIntStateOf(filter.guild)
    }
    filter.guild = guildIndex.intValue

    //种族
    val raceIndex = remember {
        mutableIntStateOf(filter.race)
    }
    filter.race = raceIndex.intValue

    //限定类型
    val typeIndex = remember {
        mutableIntStateOf(filter.type)
    }
    filter.type = typeIndex.intValue


    //更新信息
    LaunchedEffect(
        textState.value, sortTypeIndex.intValue, sortAscIndex.intValue, loveIndex.intValue,
        r6Index.intValue, positionIndex.intValue, atkIndex.intValue, guildIndex.intValue,
        raceIndex.intValue, typeIndex.intValue
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
        //角色名搜索
        val keyboardController = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            value = textState.value,
            shape = MaterialTheme.shapes.medium,
            onValueChange = { textState.value = it.deleteSpace },
            textStyle = MaterialTheme.typography.labelLarge,
            leadingIcon = {
                MainIcon(
                    data = MainIconType.CHARACTER,
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
                    text = stringResource(id = R.string.character_name),
                    style = MaterialTheme.typography.labelLarge
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
        //排序类型
        MainText(
            text = stringResource(id = R.string.title_sort),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val sortChipData = arrayListOf(
            ChipData(stringResource(id = R.string.sort_date)),
            ChipData(stringResource(id = R.string.age)),
            ChipData(stringResource(id = R.string.title_height)),
            ChipData(stringResource(id = R.string.title_weight)),
            ChipData(stringResource(id = R.string.title_position)),
            ChipData(stringResource(id = R.string.title_birth)),
            ChipData(stringResource(id = R.string.title_unlock_6))
        )
        ChipGroup(
            items = sortChipData,
            selectIndex = sortTypeIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //排序方式
        MainText(
            text = stringResource(id = R.string.sort_asc_desc),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val sortAscChipData = arrayListOf(
            ChipData(stringResource(id = R.string.sort_asc)),
            ChipData(stringResource(id = R.string.sort_desc)),
        )
        ChipGroup(
            items = sortAscChipData,
            selectIndex = sortAscIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //收藏
        MainText(
            text = stringResource(id = R.string.title_love),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val loveChipData = arrayListOf(
            ChipData(stringResource(id = R.string.all)),
            ChipData(stringResource(id = R.string.loved)),
        )
        ChipGroup(
            items = loveChipData,
            selectIndex = loveIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //类型
        MainText(
            text = stringResource(id = R.string.title_type),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val typeChipData = arrayListOf(
            ChipData(text = stringResource(id = R.string.all)),
            ChipData(
                text = stringResource(id = CharacterLimitType.NORMAL.typeNameId),
                color = CharacterLimitType.NORMAL.color
            ),
            ChipData(
                text = stringResource(id = CharacterLimitType.LIMIT.typeNameId),
                color = CharacterLimitType.LIMIT.color
            ),
            ChipData(
                text = stringResource(id = CharacterLimitType.EVENT.typeNameId),
                color = CharacterLimitType.EVENT.color
            ),
            ChipData(
                text = stringResource(id = CharacterLimitType.EXTRA.typeNameId),
                color = CharacterLimitType.EXTRA.color
            ),
        )
        ChipGroup(
            items = typeChipData,
            selectIndex = typeIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )

        //六星
        val r6ChipData = arrayListOf(
            ChipData(stringResource(id = R.string.all)),
            ChipData(text = stringResource(id = R.string.six_star), color = colorPink),
            ChipData(stringResource(id = R.string.six_locked)),
        )
        //是否选择了六星解放排序
        val isUnlock6SortType = sortTypeIndex.intValue == CharacterSortType.SORT_UNLOCK_6.type
        //未选择六星解放排序时，显示星级筛选
        ExpandAnimation(visible = !isUnlock6SortType) {
            Column {
                MainText(
                    text = stringResource(id = R.string.title_rarity),
                    modifier = Modifier.padding(top = Dimen.largePadding)
                )
                ChipGroup(
                    items = r6ChipData,
                    selectIndex = r6Index,
                    modifier = Modifier.padding(Dimen.smallPadding)
                )
            }
        }

        //位置
        MainText(
            text = stringResource(id = R.string.title_position),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val positionChipData = arrayListOf(
            ChipData(stringResource(id = R.string.all)),
            ChipData(
                text = stringResource(id = PositionType.POSITION_FRONT.typeNameId),
                color = PositionType.POSITION_FRONT.color
            ),
            ChipData(
                text = stringResource(id = PositionType.POSITION_MIDDLE.typeNameId),
                color = PositionType.POSITION_MIDDLE.color
            ),
            ChipData(
                text = stringResource(id = PositionType.POSITION_BACK.typeNameId),
                color = PositionType.POSITION_BACK.color
            ),
        )
        ChipGroup(
            items = positionChipData,
            selectIndex = positionIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //攻击类型
        MainText(
            text = stringResource(id = R.string.atk_type),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val atkChipData = arrayListOf(
            ChipData(stringResource(id = R.string.all)),
            ChipData(
                text = stringResource(id = AtkType.PHYSICAL.typeNameId),
                color = AtkType.PHYSICAL.color
            ),
            ChipData(
                text = stringResource(id = AtkType.MAGIC.typeNameId),
                color = AtkType.MAGIC.color
            ),
        )
        ChipGroup(
            items = atkChipData,
            selectIndex = atkIndex,
            modifier = Modifier.padding(Dimen.smallPadding)
        )
        //种族
        if (raceList.isNotEmpty()) {
            MainText(
                text = stringResource(id = R.string.title_race),
                modifier = Modifier.padding(top = Dimen.largePadding)
            )
            val raceChipData = arrayListOf(
                ChipData(stringResource(id = R.string.all)),
                ChipData(stringResource(id = R.string.title_race_multiple)),
            )
            raceList.forEach { raceData ->
                raceChipData.add(ChipData(raceData))
            }
            ChipGroup(
                items = raceChipData,
                selectIndex = raceIndex,
                modifier = Modifier.padding(Dimen.smallPadding),
            )
        }
        //公会名
        if (guildList.isNotEmpty()) {
            MainText(
                text = stringResource(id = R.string.title_guild),
                modifier = Modifier.padding(top = Dimen.largePadding)
            )
            val guildChipData = arrayListOf(
                ChipData(stringResource(id = R.string.all)),
                ChipData(stringResource(id = R.string.no_guild)),
            )
            guildList.forEach { guildData ->
                guildChipData.add(ChipData(guildData.guildName))
            }
            ChipGroup(
                items = guildChipData,
                selectIndex = guildIndex,
                modifier = Modifier.padding(Dimen.smallPadding),
            )
            CommonSpacer()
        }
    }
}


@CombinedPreviews
@Composable
private fun CharacterListFilterContentPreview() {
    PreviewLayout {
        CharacterListFilterContent(
            filter = FilterCharacter(),
            updateFilter = {},
            guildList = emptyList(),
            raceList = emptyList(),
        )
    }
}
