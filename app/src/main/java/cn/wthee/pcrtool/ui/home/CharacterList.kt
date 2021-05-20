package cn.wthee.pcrtool.ui.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.getSortType
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.isFilter
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.compose.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.CardTopShape
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.CharacterIdUtil
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 角色列表
 */
@ExperimentalComposeUiApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalFoundationApi
@Composable
fun CharacterList(
    toDetail: (Int) -> Unit,
    navViewModel: NavViewModel,
    viewModel: CharacterViewModel = hiltViewModel(),
) {
    val list = viewModel.characterList.observeAsState()
    //筛选状态
    val filter = navViewModel.filterCharacter.observeAsState()
    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    //滚动监听
    val scrollState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    //关闭时监听
    if (!state.isVisible) {
        navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
        navViewModel.fabOKCilck.postValue(false)
        navViewModel.resetClick.postValue(false)
        keyboardController?.hide()
    }

    filter.value?.let { filterValue ->
        filterValue.starIds =
            GsonUtil.fromJson(mainSP().getString(Constants.SP_STAR_CHARACTER, ""))
                ?: arrayListOf()
        viewModel.getCharacters(filterValue)
    }

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            FilterCharacterSheet(navViewModel, coroutineScope, state)
        }
    ) {
        val marginTop: Dp = marginTopBar(scrollState)
        coroutineScope.launch {
            val r6Ids = viewModel.getR6Ids()
            navViewModel.r6Ids.postValue(r6Ids)
        }
        Box(modifier = Modifier.fillMaxSize()) {
            TopBarCompose(scrollState = scrollState)
            SlideAnimation(visible = list.value != null && filter.value != null) {
                LazyVerticalGrid(
                    cells = GridCells.Fixed(2),
                    state = scrollState,
                    modifier = Modifier
                        .padding(top = marginTop)
                        .fillMaxSize()
                        .background(color = MaterialTheme.colors.background, shape = CardTopShape)
                ) {
                    items(list.value ?: arrayListOf()) {
                        CharacterItem(it, filter.value!!, toDetail)
                    }
                    items(2) {
                        CommonSpacer()
                    }
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin),
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
                val count = list.value?.size ?: 0
                // 数量显示&筛选按钮
                FabCompose(
                    iconType = MainIconType.CHARACTER,
                    text = "$count"
                ) {
                    coroutineScope.launch {
                        if (state.isVisible) {
                            navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
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
 * 角色列表项
 */
@Composable
private fun CharacterItem(
    character: CharacterInfo,
    filter: FilterCharacter,
    toDetail: (Int) -> Unit,
) {
    val loved = filter.starIds.contains(character.id)
    val nameColor = if (loved) {
        MaterialTheme.colors.primary
    } else {
        Color.Unspecified
    }

    MainCard(
        modifier = Modifier.padding(Dimen.mediuPadding),
        onClick = {
            //跳转至详情
            toDetail(character.id)
        }) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            //图片
            var id = character.id
            id += if (MainActivity.r6Ids.contains(character.id)) 60 else 30
            CharacterCard(
                CharacterIdUtil.getMaxCardUrl(
                    character.id,
                    MainActivity.r6Ids.contains(character.id)
                ),
                true
            )
            //名字、位置
            Row(
                modifier = Modifier.padding(Dimen.smallPadding),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = character.getNameF(),
                    style = MaterialTheme.typography.subtitle2,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    color = nameColor
                )
                PositionIcon(character.position)
            }
            //其它属性
            Row(
                modifier = Modifier
                    .padding(Dimen.smallPadding)
                    .fillMaxWidth()
            ) {
                CharacterNumberText(character.getFixedAge())
                CharacterNumberText(character.getFixedHeight() + "CM")
                CharacterNumberText(character.getFixedWeight() + "KG")
                CharacterNumberText(character.position.toString(), modifier = Modifier.weight(1f))
            }
        }
    }
}

/**
 * 蓝色字体
 */
@Composable
private fun CharacterNumberText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.primaryVariant,
        style = MaterialTheme.typography.caption,
        modifier = modifier.padding(end = Dimen.smallPadding)
    )
}

/**
 * 角色筛选
 */
@ExperimentalComposeUiApi
@ExperimentalMaterialApi
@Composable
private fun FilterCharacterSheet(
    navViewModel: NavViewModel,
    coroutineScope: CoroutineScope,
    sheetState: ModalBottomSheetState,
    characterViewModel: CharacterViewModel = hiltViewModel()
) {
    val filter = navViewModel.filterCharacter.value ?: FilterCharacter()

    val textState = remember { mutableStateOf(TextFieldValue(text = filter.name)) }
    filter.name = textState.value.text
    //排序类型筛选s
    val sortTypeIndex = remember {
        mutableStateOf(filter.sortType.type)
    }
    filter.sortType = getSortType(sortTypeIndex.value)

    //排序方式筛选
    val sortAscIndex = remember {
        mutableStateOf(if (filter.asc) 0 else 1)
    }
    filter.asc = sortAscIndex.value == 0

    //收藏筛选
    val loveIndex = remember {
        mutableStateOf(if (filter.all) 0 else 1)
    }
    filter.all = loveIndex.value == 0

    //六星筛选
    val r6Index = remember {
        mutableStateOf(if (filter.r6) 1 else 0)
    }
    filter.r6 = r6Index.value == 1

    //位置筛选
    val positionIndex = remember {
        mutableStateOf(filter.positon)
    }
    filter.positon = positionIndex.value

    //攻击类型
    val atkIndex = remember {
        mutableStateOf(filter.atk)
    }
    filter.atk = atkIndex.value

    //公会
    characterViewModel.getGuilds()
    val guildList = characterViewModel.guilds.observeAsState().value ?: arrayListOf()
    val guildIndex = remember {
        mutableStateOf(filter.guild)
    }
    if (!filter.isFilter()) {
        guildIndex.value = 0
    }
    filter.guild = guildIndex.value

    //确认操作
    val ok = navViewModel.fabOKCilck.observeAsState().value ?: false
    val reset = navViewModel.resetClick.observeAsState().value ?: false

    //选择状态
    Column(
        modifier = Modifier
            .clip(CardTopShape)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        //点击重置
        if (reset) {
            textState.value = TextFieldValue(text = "")
            sortTypeIndex.value = 0
            sortAscIndex.value = 1
            loveIndex.value = 0
            r6Index.value = 0
            positionIndex.value = 0
            atkIndex.value = 0
            guildIndex.value = 0
            navViewModel.resetClick.postValue(false)
            navViewModel.filterCharacter.postValue(FilterCharacter())
        }
        //点击确认
        if (ok) {
            coroutineScope.launch {
                sheetState.hide()
            }
            navViewModel.filterCharacter.postValue(filter)
            navViewModel.fabOKCilck.postValue(false)
            navViewModel.fabMainIcon.postValue(MainIconType.MAIN)
        }
        //角色名搜索
        val keyboardController = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            textStyle = MaterialTheme.typography.button,
            leadingIcon = {
                IconCompose(
                    data = MainIconType.CHARACTER.icon,
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
                    text = stringResource(id = R.string.character_name),
                    style = MaterialTheme.typography.button
                )
            },
            modifier = Modifier
                .padding(Dimen.largePadding)
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
        //攻击类型
        MainText(
            text = stringResource(id = R.string.atk_type),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val atkChipData = arrayListOf(
            ChipData(0, stringResource(id = R.string.all)),
            ChipData(1, stringResource(id = R.string.physical)),
            ChipData(2, stringResource(id = R.string.magic)),
        )
        ChipGroup(
            atkChipData,
            atkIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
        //公会名
        MainText(
            text = stringResource(id = R.string.title_guild),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )

        val guildChipData = arrayListOf(ChipData(0, stringResource(id = R.string.all)))
        guildList.forEachIndexed { index, guildData ->
            guildChipData.add(ChipData(index + 1, guildData.guildName))
        }
        ChipGroup(
            guildChipData,
            guildIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )
    }
}

