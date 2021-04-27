package cn.wthee.pcrtool.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.getSortType
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.view.CharacterInfo
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 角色列表
 * fixme item 动画
 */
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun CharacterList(
    toDetail: (Int, Int) -> Unit,
    navViewModel: NavViewModel,
    viewModel: CharacterViewModel = hiltNavGraphViewModel(),
) {
    val list = viewModel.characterList.observeAsState()
    //筛选状态
    val filter = remember {
        mutableStateOf(FilterCharacter())
    }
    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()

    //FabMain 是否显示
    if (state.direction == -1f) {
        navViewModel.fabShow.postValue(false)
    } else if (state.direction == 1f) {
        navViewModel.fabShow.postValue(true)
    }

    viewModel.getCharacters(filter.value)

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            FilterCharacterSheet(coroutineScope, filter, state)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyVerticalGrid(cells = GridCells.Fixed(2)) {
                items(list.value ?: arrayListOf()) {
                    AnimatedVisibility(visible = true) {
                        CharacterItem(it, toDetail)
                    }
                }
            }
            val count = list.value?.size ?: 0
            // 数量显示&筛选按钮
            ExtendedFabCompose(
                modifier = Modifier
                    .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin)
                    .align(Alignment.BottomEnd),
                iconId = R.drawable.ic_character,
                text = "$count"
            ) {
                coroutineScope.launch {
                    if (state.isVisible) {
                        state.hide()
                    } else {
                        state.show()
                    }
                }
            }
        }
    }
}

/**
 * 角色列表项
 */
@Composable
private fun CharacterItem(
    character: CharacterInfo,
    toDetail: (Int, Int) -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(Dimen.mediuPadding)
            .shadow(elevation = Dimen.cardElevation, shape = Shapes.large, clip = true)
            .clickable {
                //跳转至详情
                toDetail(character.id, character.r6Id)
            }) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            //图片
            var id = character.id
            id += if (character.r6Id != 0) 60 else 30
            CharacterCard(Constants.CHARACTER_FULL_URL + id + Constants.WEBP, true)
            //名字、位置
            Row(
                modifier = Modifier.padding(Dimen.smallPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = character.getNameF(),
                    style = MaterialTheme.typography.subtitle2,
                    modifier = Modifier.weight(1f)
                )
                PositionIcon(character.position, 18.dp)
            }
            //其它属性
            Row(
                modifier = Modifier.padding(
                    start = Dimen.smallPadding,
                    bottom = Dimen.smallPadding
                )
            ) {
                CharacterNumberText(character.getFixedAge())
                CharacterNumberText(character.getFixedHeight() + "CM")
                CharacterNumberText(character.getFixedWeight() + "KG")
                CharacterNumberText(character.position.toString())
            }
        }
    }
}

/**
 * 蓝色字体
 */
@Composable
private fun CharacterNumberText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colors.primaryVariant,
        style = MaterialTheme.typography.caption,
        modifier = Modifier.padding(end = Dimen.smallPadding)
    )
}

@ExperimentalMaterialApi
@Composable
private fun FilterCharacterSheet(
    coroutineScope: CoroutineScope,
    filter: MutableState<FilterCharacter>,
    sheetState: ModalBottomSheetState
) {
    val textState = remember { mutableStateOf(TextFieldValue()) }
    val newFilter = FilterCharacter()
    newFilter.name = textState.value.text
    //排序类型筛选
    val sortTypeIndex = remember {
        mutableStateOf(0)
    }
    newFilter.sortType = getSortType(sortTypeIndex.value)
    //排序方式筛选
    val sortAscIndex = remember {
        mutableStateOf(1)
    }
    newFilter.asc = sortAscIndex.value == 0
    //六星筛选
    val r6Index = remember {
        mutableStateOf(0)
    }
    newFilter.r6 = r6Index.value == 1
    //六星筛选
    val positionIndex = remember {
        mutableStateOf(0)
    }
    newFilter.positon = positionIndex.value


    //选择状态
    Column(
        modifier = Modifier
            .clip(CardTopShape)
            .fillMaxWidth()
            .clickable {}
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.mediuPadding)
        ) {
            //重置
            SubButton(stringResource(id = R.string.reset)) {
                coroutineScope.launch {
                    sheetState.hide()
                }
                sortTypeIndex.value = 0
                sortAscIndex.value = 1
                r6Index.value = 0
                positionIndex.value = 0
                filter.value = FilterCharacter()
            }
            //确认
            MainButton(stringResource(id = R.string.ok)) {
                coroutineScope.launch {
                    sheetState.hide()
                }
                filter.value = newFilter
            }
        }
        //角色名搜索
        OutlinedTextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            textStyle = MaterialTheme.typography.button,
            singleLine = false,
            label = {
                Text(
                    text = stringResource(id = R.string.character_name),
                    style = MaterialTheme.typography.button
                )
            },
            modifier = Modifier
                .padding(Dimen.mediuPadding)
                .fillMaxWidth()

        )
        //排序类型
        MainText(
            text = stringResource(id = R.string.title_sort),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val sortChipData = arrayListOf(
            ChipData(0, stringResource(id = R.string.sort_date)),
            ChipData(1, stringResource(id = R.string.age)),
            ChipData(2, stringResource(id = R.string.title_height)),
            ChipData(3, stringResource(id = R.string.title_weight)),
            ChipData(4, stringResource(id = R.string.title_position)),
        )
        ChipGroup(
            sortChipData,
            sortTypeIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //排序方式
        MainText(
            text = stringResource(id = R.string.sort_asc_desc),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val sortAscChipData = arrayListOf(
            ChipData(0, stringResource(id = R.string.sort_asc)),
            ChipData(1, stringResource(id = R.string.sort_desc)),
        )
        ChipGroup(
            sortAscChipData,
            sortAscIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //六星
        MainText(
            text = stringResource(id = R.string.title_rarity),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val r6ChipData = arrayListOf(
            ChipData(0, stringResource(id = R.string.all)),
            ChipData(1, stringResource(id = R.string.six_unlock)),
        )
        ChipGroup(
            r6ChipData,
            r6Index,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //位置
        MainText(
            text = stringResource(id = R.string.title_position),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val positionChipData = arrayListOf(
            ChipData(0, stringResource(id = R.string.all)),
            ChipData(1, stringResource(id = R.string.position_1)),
            ChipData(2, stringResource(id = R.string.position_2)),
            ChipData(3, stringResource(id = R.string.position_3)),
        )
        ChipGroup(
            positionChipData,
            positionIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
    }
}

