package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.palette.graphics.Palette
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.enums.CharacterSortType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.getSortType
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.isFilter
import cn.wthee.pcrtool.navigation.NavViewModel
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CenterTipText
import cn.wthee.pcrtool.ui.components.CharacterTagRow
import cn.wthee.pcrtool.ui.components.ChipGroup
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainImage
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.RATIO
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.commonPlaceholder
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.RATIO_GOLDEN
import cn.wthee.pcrtool.ui.theme.TrapezoidShape
import cn.wthee.pcrtool.ui.theme.colorAlphaBlack
import cn.wthee.pcrtool.ui.theme.colorAlphaWhite
import cn.wthee.pcrtool.ui.theme.colorCopper
import cn.wthee.pcrtool.ui.theme.colorCyan
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.ui.theme.shapeTop
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.utils.fixedStr
import cn.wthee.pcrtool.utils.formatTime
import cn.wthee.pcrtool.viewmodel.CharacterViewModel
import kotlinx.coroutines.launch

/**
 * 角色列表
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class)
@Composable
fun CharacterList(
    scrollState: LazyGridState,
    toDetail: (Int) -> Unit,
    characterViewModel: CharacterViewModel = hiltViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    //筛选状态
    val filter = navViewModel.filterCharacter.observeAsState().value ?: FilterCharacter()
    // sheet 状态
    val state = rememberModalBottomSheetState(
        ModalBottomSheetValue.Hidden
    )

    //关闭时监听
    if (!state.isVisible) {
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabOKClick.postValue(false)
        navViewModel.resetClick.postValue(false)
        keyboardController?.hide()
    }

    filter.starIds = FilterCharacter.getStarIdList()

    //角色列表
    val characterListFlow = remember(filter.hashCode()) {
        characterViewModel.getCharacterInfoList(filter)
    }
    val characterList by characterListFlow.collectAsState(initial = arrayListOf())

    ModalBottomSheetLayout(
        sheetState = state,
        scrimColor = if (isSystemInDarkTheme()) colorAlphaBlack else colorAlphaWhite,
        sheetBackgroundColor = MaterialTheme.colorScheme.surface,
        sheetShape = shapeTop(),
        sheetContent = {
            FilterCharacterSheet(navViewModel, state)
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (characterList.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(getItemWidth()),
                    state = scrollState
                ) {
                    items(
                        items = characterList,
                        key = {
                            it.id
                        }
                    ) {
                        CharacterItem(
                            unitId = it.id,
                            character = it,
                            loved = filter.starIds.contains(it.id),
                            modifier = Modifier.padding(Dimen.mediumPadding),
                        ) {
                            toDetail(it.id)
                        }
                    }
                    items(2) {
                        CommonSpacer()
                    }
                }
            } else {
                CenterTipText(
                    stringResource(id = R.string.no_data)
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin),
                horizontalArrangement = Arrangement.End
            ) {
                //回到顶部
                MainSmallFab(
                    iconType = MainIconType.TOP
                ) {
                    coroutineScope.launch {
                        scrollState.scrollToItem(0)
                    }
                }
                //重置筛选
                if (filter.isFilter()) {
                    MainSmallFab(
                        iconType = MainIconType.RESET
                    ) {
                        coroutineScope.launch {
                            state.hide()
                        }
                        navViewModel.resetClick.postValue(true)
                    }
                }
                val count = characterList.size
                // 数量显示&筛选按钮
                MainSmallFab(
                    iconType = MainIconType.CHARACTER,
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

/**
 * 角色列表项
 */
@Composable
fun CharacterItem(
    unitId: Int,
    character: CharacterInfo?,
    loved: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    //图片是否加载成功
    var loadSuccess by remember {
        mutableStateOf(false)
    }
    var loadError by remember {
        mutableStateOf(false)
    }
    //主色
    val initColor = colorWhite
    var cardMaskColor by remember {
        mutableStateOf(initColor)
    }
    //主要字体颜色
    val textColor = if (loadSuccess) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.onSurface
    }


    MainCard(
        modifier = modifier.commonPlaceholder(character?.id == -1),
        onClick = onClick
    ) {
        Box(modifier = Modifier.height(IntrinsicSize.Min)) {
            //图片
            MainImage(
                data = ImageRequestHelper.getInstance().getMaxCardUrl(unitId),
                ratio = RATIO,
                contentScale = ContentScale.FillHeight,
                onError = { loadError = true }
            ) { result ->
                loadSuccess = true
                //取色
                Palette.from(result.drawable.toBitmap()).generate { palette ->
                    palette?.let {
                        cardMaskColor = Color(it.getDominantColor(Color.Transparent.toArgb()))
                    }
                }
            }
            if (character != null) {
                //名称阴影效果
                if (loadSuccess) {
                    CharacterName(
                        color = MaterialTheme.colorScheme.primary,
                        name = character.getNameF(),
                        nameExtra = character.getNameL(),
                        isBorder = true,
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                }
                //名称
                CharacterName(
                    color = textColor,
                    name = character.getNameF(),
                    nameExtra = character.getNameL(),
                    isBorder = false,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            } else {
                //暂未登场
                if (loadSuccess) {
                    CharacterName(
                        color = MaterialTheme.colorScheme.primary,
                        name = stringResource(id = R.string.unknown_character),
                        nameExtra = "",
                        isBorder = true,
                        modifier = Modifier.align(Alignment.BottomStart)
                    )
                }
                //名称
                CharacterName(
                    color = textColor,
                    name = stringResource(id = R.string.unknown_character),
                    nameExtra = "",
                    isBorder = false,
                    modifier = Modifier.align(Alignment.BottomStart)
                )
            }


            //其它信息
            FadeAnimation(
                visible = loadSuccess || loadError || LocalInspectionMode.current,
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                //年龄等
                if (character != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(1 - RATIO_GOLDEN)
                            .fillMaxHeight()
                            .clip(TrapezoidShape)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        cardMaskColor,
                                        cardMaskColor,
                                        MaterialTheme.colorScheme.primary,
                                    )
                                ),
                                alpha = 0.6f
                            ),
                        horizontalAlignment = Alignment.End,
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(
                                    horizontal = Dimen.mediumPadding,
                                    vertical = Dimen.smallPadding
                                ),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.SpaceAround
                        ) {
                            //年龄
                            Subtitle2(
                                text = character.age.fixedStr,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            //生日
                            Subtitle2(
                                text = stringResource(
                                    id = R.string.date_m_d,
                                    character.birthMonth.fixedStr,
                                    character.birthDay.fixedStr
                                ),
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            //体重
                            Subtitle2(
                                text = "${character.weight.fixedStr} KG",
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                            //身高
                            Subtitle2(
                                text = "${character.height.fixedStr} CM",
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )

                        }

                        //获取方式等
                        Box(
                            modifier = Modifier
                                .padding(end = Dimen.smallPadding)
                                .weight(1f),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            CharacterTagRow(
                                basicInfo = character,
                                horizontalArrangement = Arrangement.End
                            )
                        }

                        //最近登场日期
                        CaptionText(
                            text = character.startTime.formatTime.substring(0, 10),
                            color = textColor,
                            modifier = Modifier.padding(
                                end = Dimen.mediumPadding,
                                bottom = Dimen.smallPadding
                            )
                        )
                    }
                }
            }

            //收藏标识
            FadeAnimation(visible = loved && (loadSuccess || loadError)) {
                MainIcon(
                    data = MainIconType.LOVE_FILL,
                    size = Dimen.textIconSize,
                    modifier = Modifier.padding(Dimen.mediumPadding)
                )
            }
        }
    }
}


/**
 * 获取限定类型
 */
@Composable
fun getLimitTypeText(limitType: Int) = when (limitType) {
    2 -> {
        stringResource(id = R.string.type_limit)
    }

    3 -> {
        stringResource(id = R.string.type_event_limit)
    }

    4 -> {
        stringResource(id = R.string.type_extra_character)

    }

    else -> {
        stringResource(id = R.string.type_normal)
    }
}

/**
 * 获取限定类型颜色
 */
fun getLimitTypeColor(limitType: Int) = when (limitType) {
    2 -> {
        colorRed
    }

    3 -> {
        colorGreen
    }

    4 -> {
        colorCyan
    }

    else -> {
        colorGold
    }
}


/**
 * 攻击类型
 */
@Composable
fun getAtkText(atkType: Int) = when (atkType) {
    1 -> stringResource(id = R.string.physical)
    2 -> stringResource(id = R.string.magic)
    else -> stringResource(id = R.string.unknown)
}

/**
 * 攻击颜色
 */
fun getAtkColor(atkType: Int) = when (atkType) {
    1 -> colorGold
    2 -> colorPurple
    else -> colorCopper
}


/**
 * 角色名称
 */
@Composable
private fun CharacterName(
    color: Color,
    name: String,
    nameExtra: String,
    isBorder: Boolean,
    modifier: Modifier = Modifier
) {
    val mModifier = if (isBorder) {
        modifier
            .padding(
                start = Dimen.mediumPadding + Dimen.textElevation,
                end = Dimen.mediumPadding,
                top = Dimen.mediumPadding + Dimen.textElevation,
                bottom = Dimen.mediumPadding
            )

    } else {
        modifier
            .padding(Dimen.mediumPadding)
    }

    Column(
        modifier = mModifier
            .fillMaxWidth(RATIO_GOLDEN)
    ) {
        Subtitle1(
            text = nameExtra,
            color = color,
            selectable = !isBorder
        )
        MainText(
            text = name,
            color = color,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleLarge,
            selectable = !isBorder
        )
    }
}

/**
 * 角色筛选
 */
@OptIn(
    ExperimentalMaterialApi::class, ExperimentalComposeUiApi::class
)
@Composable
private fun FilterCharacterSheet(
    navViewModel: NavViewModel,
    sheetState: ModalBottomSheetState,
    characterViewModel: CharacterViewModel = hiltViewModel()
) {
    val filter = navViewModel.filterCharacter.value ?: FilterCharacter()

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
    val guildListFlow = remember {
        characterViewModel.getGuilds()
    }
    val guildList by guildListFlow.collectAsState(initial = arrayListOf())
    val guildIndex = remember {
        mutableIntStateOf(filter.guild)
    }
    filter.guild = guildIndex.intValue

    //种族
    val raceListFlow = remember {
        characterViewModel.getRaces()
    }
    val raceList by raceListFlow.collectAsState(initial = arrayListOf())
    val raceIndex = remember {
        mutableIntStateOf(filter.race)
    }
    filter.race = raceIndex.intValue

    //限定类型
    val typeIndex = remember {
        mutableIntStateOf(filter.type)
    }
    filter.type = typeIndex.intValue

    //确认操作
    val ok = navViewModel.fabOKClick.observeAsState().value ?: false
    val reset = navViewModel.resetClick.observeAsState().value ?: false

    //重置或确认
    LaunchedEffect(sheetState.isVisible, reset, ok) {
        //点击重置
        if (reset) {
            textState.value = ""
            sortTypeIndex.intValue = 0
            sortAscIndex.intValue = 1
            loveIndex.intValue = 0
            r6Index.intValue = 0
            positionIndex.intValue = 0
            atkIndex.intValue = 0
            guildIndex.intValue = 0
            typeIndex.intValue = 0
            raceIndex.intValue = 0
            navViewModel.resetClick.postValue(false)
            navViewModel.filterCharacter.postValue(null)
        }
        //点击确认
        if (ok) {
            sheetState.hide()
            navViewModel.filterCharacter.postValue(filter)
            navViewModel.fabOKClick.postValue(false)
            navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        }
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
                    navViewModel.fabOKClick.postValue(true)
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    keyboardController?.hide()
                    navViewModel.fabOKClick.postValue(true)
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
            ChipData(0, stringResource(id = R.string.sort_date)),
            ChipData(1, stringResource(id = R.string.age)),
            ChipData(2, stringResource(id = R.string.title_height)),
            ChipData(3, stringResource(id = R.string.title_weight)),
            ChipData(4, stringResource(id = R.string.title_position)),
            ChipData(5, stringResource(id = R.string.title_birth)),
            ChipData(6, stringResource(id = R.string.title_unlock_6))
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
            ChipData(4, stringResource(id = R.string.type_extra_character)),
        )
        ChipGroup(
            typeChipData,
            typeIndex,
            modifier = Modifier.padding(Dimen.smallPadding),
        )

        //六星
        val r6ChipData = arrayListOf(
            ChipData(0, stringResource(id = R.string.all)),
            ChipData(1, stringResource(id = R.string.six_star)),
            ChipData(2, stringResource(id = R.string.six_locked)),
        )
        //是否选择了六星解放排序
        val isUnlock6SortType = sortTypeIndex.intValue == CharacterSortType.SORT_UNLOCK_6.type
        //未选择六星解放排序是显示
        ExpandAnimation(visible = !isUnlock6SortType) {
            Column {
                MainText(
                    text = stringResource(id = R.string.title_rarity),
                    modifier = Modifier.padding(top = Dimen.largePadding)
                )
                ChipGroup(
                    r6ChipData,
                    r6Index,
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
        //种族
        if (raceList.isNotEmpty()) {
            MainText(
                text = stringResource(id = R.string.title_race),
                modifier = Modifier.padding(top = Dimen.largePadding)
            )
            val raceChipData = arrayListOf(
                ChipData(0, stringResource(id = R.string.all)),
                ChipData(1, stringResource(id = R.string.title_race_multiple)),
            )
            raceList.forEachIndexed { index, raceData ->
                raceChipData.add(ChipData(index + 2, raceData))
            }
            ChipGroup(
                raceChipData,
                raceIndex,
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

@CombinedPreviews
@Composable
private fun CharacterItemPreview() {
    PreviewLayout {
        CharacterItem(
            unitId = 100101,
            character = CharacterInfo(),
            loved = true,
        ) {}
    }
}
