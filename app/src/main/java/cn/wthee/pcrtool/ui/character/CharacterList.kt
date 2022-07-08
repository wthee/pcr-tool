package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.palette.graphics.Palette
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.getFixed
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.PositionType
import cn.wthee.pcrtool.data.enums.getSortType
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.isFilter
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.NavViewModel
import cn.wthee.pcrtool.ui.common.*
import cn.wthee.pcrtool.ui.mainSP
import cn.wthee.pcrtool.ui.theme.*
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.GsonUtil
import cn.wthee.pcrtool.utils.ImageResourceHelper
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 角色列表
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun CharacterList(
    scrollState: LazyGridState,
    toDetail: (Int) -> Unit,
    viewModel: CharacterViewModel = hiltViewModel(),
) {

    //筛选状态
    val filter = navViewModel.filterCharacter.observeAsState()
    // dialog 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    //关闭时监听
    if (!state.isVisible && !state.isAnimationRunning) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabOKCilck.postValue(false)
        navViewModel.resetClick.postValue(false)
        keyboardController?.hide()
    }

    filter.value?.let { filterValue ->
        filterValue.starIds =
            GsonUtil.fromJson(mainSP().getString(Constants.SP_STAR_CHARACTER, ""))
                ?: arrayListOf()
    }
    val list = viewModel.getCharacters(filter.value).collectAsState(initial = arrayListOf()).value

    ModalBottomSheetLayout(
        sheetState = state,
        scrimColor = if (isSystemInDarkTheme()) colorAlphaBlack else colorAlphaWhite,
        sheetElevation = Dimen.sheetElevation,
        sheetShape = if (state.offset.value == 0f) {
            noShape
        } else {
            Shape.large
        },
        sheetContent = {
            FilterCharacterSheet(navViewModel, coroutineScope, state)
        },
        sheetBackgroundColor = MaterialTheme.colorScheme.surface
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (list.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(getItemWidth()),
                    state = scrollState,
                    contentPadding = PaddingValues(Dimen.mediumPadding)
                ) {
                    items(
                        items = list,
                        key = {
                            it.id
                        }
                    ) {
                        CharacterItem(
                            character = it,
                            loved = filter.value!!.starIds.contains(it.id),
                            modifier = Modifier.padding(Dimen.mediumPadding),
                        ) {
                            toDetail(it.id)
                        }
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
                val count = list.size
                // 数量显示&筛选按钮
                FabCompose(
                    iconType = MainIconType.CHARACTER,
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
 * 角色列表项
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CharacterItem(
    character: CharacterInfo,
    loved: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    //图片是否加载成功
    var loadSuccess by remember {
        mutableStateOf(false)
    }
    //位置信息
    var positionText = ""
    val pos = when (PositionType.getPositionType(character.position)) {
        PositionType.POSITION_0_299 -> stringResource(id = R.string.position_0)
        PositionType.POSITION_300_599 -> stringResource(id = R.string.position_1)
        PositionType.POSITION_600_999 -> stringResource(id = R.string.position_2)
        PositionType.UNKNOWN -> Constants.UNKNOWN
    }
    if (pos != Constants.UNKNOWN) {
        positionText = "$pos ${character.position}"
    }
    //获取方式
    val limitColor: Color
    val limitType: String
    when (character.isLimited) {
        1 -> {
            if (character.rarity == 1) {
                limitType = stringResource(id = R.string.type_event_limit)
                limitColor = colorGreen
            } else {
                limitType = stringResource(id = R.string.type_limit)
                limitColor = colorRed
            }
        }
        else -> {
            limitType = stringResource(id = R.string.type_normal)
            limitColor = colorGold
        }
    }
    //主色
    val initColor = MaterialTheme.colorScheme.onPrimary
    var cardMainColor by remember {
        mutableStateOf(initColor)
    }
    //主要字体颜色暂时使用白色字体 fixme 不同背景下变更字体颜色时，可能导致字体不明显
    val textColor = if (loadSuccess) Color.White else Color.Black

    MainCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Box(modifier = Modifier.height(IntrinsicSize.Min)) {
            //图片
            ImageCompose(
                data = ImageResourceHelper.getInstance().getMaxCardUrl(character.id),
                ratio = RATIO,
                loadingId = R.drawable.load,
                errorId = R.drawable.error,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier.heightIn(max = getItemWidth())
            ) { result ->
                loadSuccess = true
                //取色
                Palette.from(result.drawable.toBitmap()).generate { palette ->
                    palette?.let {
                        cardMainColor = Color(it.getDominantColor(Color.Transparent.toArgb()))
                    }
                }
            }

            if (loved) {
                IconCompose(
                    data = MainIconType.LOVE_FILL,
                    size = Dimen.fabIconSize,
                    modifier = Modifier.padding(Dimen.mediumPadding)
                )
            }

            //名称
            Column(
                modifier = Modifier
                    .padding(Dimen.mediumPadding)
                    .fillMaxWidth(1f - RATIO_SHAPE)
                    .align(Alignment.BottomStart),
            ) {
                Subtitle1(
                    text = character.getNameL(),
                    color = textColor
                )
                Text(
                    text = character.getNameF(),
                    color = textColor,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            //其它信息
            SlideRTLAnimation(
                visible = loadSuccess,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                //年龄等
                Column(
                    modifier = Modifier
                        .fillMaxWidth(RATIO_SHAPE)
                        .fillMaxHeight()
                        .clip(TrapezoidShape)
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    cardMainColor,
                                    cardMainColor,
                                )
                            ),
                            alpha = 0.6f
                        ),
                    horizontalAlignment = Alignment.End,
                ) {
                    Column(
                        modifier = Modifier
                            .padding(Dimen.mediumPadding),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        //年龄
                        Subtitle2(
                            text = getFixed(character.age),
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        //生日
                        Subtitle2(
                            text = "${getFixed(character.birthMonth)}月${getFixed(character.birthDay)}日",
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        //体重
                        Subtitle2(
                            text = getFixed(character.weight) + "KG",
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                        //身高
                        Subtitle2(
                            text = getFixed(character.height) + "CM",
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }

                    Column(
                        modifier = Modifier
                            .weight(1f),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.End
                    ) {
                        Row {
                            //获取方式
                            CharacterTag(
                                modifier = Modifier.padding(end = Dimen.mediumPadding),
                                text = limitType,
                                backgroundColor = limitColor,
                                textColor = textColor
                            )
                            //攻击
                            CharacterTag(
                                modifier = Modifier.padding(end = Dimen.mediumPadding),
                                text = character.getAtkType(),
                                backgroundColor = getAtkColor(atkType = character.atkType),
                                textColor = textColor
                            )
                        }

                        //位置
                        Row(
                            modifier = Modifier.padding(
                                top = Dimen.mediumPadding,
                                end = Dimen.mediumPadding
                            ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            //位置图标
                            PositionIcon(
                                position = character.position,
                                size = Dimen.smallIconSize
                            )
                            //位置
                            CharacterTag(
                                modifier = Modifier.padding(start = Dimen.smallPadding),
                                text = positionText,
                                backgroundColor = getPositionColor(character.position),
                                textColor = textColor
                            )
                        }
                    }

                    //最近登场日期
                    CaptionText(
                        text = character.startTime.formatTime,
                        color = textColor,
                        modifier = Modifier.padding(Dimen.mediumPadding)
                    )

                }
            }

        }

    }
}

/**
 * 角色属性标签
 */
@Composable
private fun CharacterTag(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color = backgroundColor, shape = CircleShape)
            .padding(horizontal = Dimen.mediumPadding),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.ExtraBold
        )
    }
}

/**
 * 角色筛选
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
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
    //排序类型筛选
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
        mutableStateOf(filter.r6)
    }
    filter.r6 = r6Index.value

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
    val guildList = characterViewModel.getGuilds().collectAsState(initial = arrayListOf()).value
    val guildIndex = remember {
        mutableStateOf(filter.guild)
    }
    filter.guild = guildIndex.value

    //限定类型
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
            .padding(start = Dimen.largePadding, end = Dimen.largePadding)
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
            typeIndex.value = 0
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
            navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        }
        //角色名搜索
        val keyboardController = LocalSoftwareKeyboardController.current
        OutlinedTextField(
            value = textState.value,
            shape = Shape.medium,
            onValueChange = { textState.value = it },
            textStyle = MaterialTheme.typography.labelLarge,
            leadingIcon = {
                IconCompose(
                    data = MainIconType.CHARACTER,
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
            singleLine = false,
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
            ChipData(0, stringResource(id = R.string.sort_date)),
            ChipData(1, stringResource(id = R.string.age)),
            ChipData(2, stringResource(id = R.string.title_height)),
            ChipData(3, stringResource(id = R.string.title_weight)),
            ChipData(4, stringResource(id = R.string.title_position)),
            ChipData(5, stringResource(id = R.string.title_birth)),
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
        //类型
        MainText(
            text = stringResource(id = R.string.title_type),
            modifier = Modifier.padding(top = Dimen.largePadding)
        )
        val typeChipData = arrayListOf(
            ChipData(0, stringResource(id = R.string.all)),
            ChipData(1, stringResource(id = R.string.type_normal)),
            ChipData(2, stringResource(id = R.string.type_limit)),
            ChipData(3, stringResource(id = R.string.type_event_limit)),
        )
        ChipGroup(
            typeChipData,
            typeIndex,
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
            ChipData(2, stringResource(id = R.string.six_locked)),
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
            ChipData(1, stringResource(id = R.string.position_0)),
            ChipData(2, stringResource(id = R.string.position_1)),
            ChipData(3, stringResource(id = R.string.position_2)),
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
            modifier = Modifier.padding(Dimen.smallPadding)
        )
        //公会名
        if (guildList.isNotEmpty()) {
            MainText(
                text = stringResource(id = R.string.title_guild),
                modifier = Modifier.padding(top = Dimen.largePadding)
            )
            val guildChipData = arrayListOf(
                ChipData(0, stringResource(id = R.string.all)),
                ChipData(1, stringResource(id = R.string.no_guild)),
            )
            guildList.forEachIndexed { index, guildData ->
                guildChipData.add(ChipData(index + 2, guildData.guildName))
            }
            ChipGroup(
                guildChipData,
                guildIndex,
                modifier = Modifier.padding(Dimen.smallPadding),
            )
            CommonSpacer()
        }
    }
}