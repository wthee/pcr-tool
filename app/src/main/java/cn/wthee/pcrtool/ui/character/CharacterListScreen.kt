package cn.wthee.pcrtool.ui.character

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.palette.graphics.Palette
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.isFilter
import cn.wthee.pcrtool.ui.MainActivity.Companion.navSheetState
import cn.wthee.pcrtool.ui.components.CaptionText
import cn.wthee.pcrtool.ui.components.CharacterTagRow
import cn.wthee.pcrtool.ui.components.CommonSpacer
import cn.wthee.pcrtool.ui.components.MainCard
import cn.wthee.pcrtool.ui.components.MainIcon
import cn.wthee.pcrtool.ui.components.MainImage
import cn.wthee.pcrtool.ui.components.MainScaffold
import cn.wthee.pcrtool.ui.components.MainSmallFab
import cn.wthee.pcrtool.ui.components.MainText
import cn.wthee.pcrtool.ui.components.RATIO
import cn.wthee.pcrtool.ui.components.StateBox
import cn.wthee.pcrtool.ui.components.Subtitle1
import cn.wthee.pcrtool.ui.components.Subtitle2
import cn.wthee.pcrtool.ui.components.commonPlaceholder
import cn.wthee.pcrtool.ui.components.getItemWidth
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.RATIO_GOLDEN
import cn.wthee.pcrtool.ui.theme.TrapezoidShape
import cn.wthee.pcrtool.ui.theme.colorCopper
import cn.wthee.pcrtool.ui.theme.colorCyan
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.fixedStr
import cn.wthee.pcrtool.utils.formatTime
import com.google.gson.Gson
import kotlinx.coroutines.launch

/**
 * 角色列表
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CharacterListScreen(
    scrollState: LazyGridState,
    toCharacterDetail: (Int) -> Unit,
    toFilterCharacter: (String) -> Unit,
    characterListViewModel: CharacterListViewModel = hiltViewModel(),
) {
    val uiState by characterListViewModel.uiState.collectAsStateWithLifecycle()

    //初始筛选信息
    LaunchedEffect(navSheetState.isVisible){
        if(!navSheetState.isVisible){
            characterListViewModel.initFilter()
        }
    }
    

    MainScaffold(
        fab = {
            CharacterListFabContent(
                count = uiState.characterList?.size ?: 0,
                scrollState = scrollState,
                filter = uiState.filter,
                resetFilter = characterListViewModel::resetFilter,
                toFilterCharacter = toFilterCharacter
            )
        }
    ) {
        StateBox(
            stateType = uiState.loadingState,
        ) {
            CharacterListContent(
                characterList = uiState.characterList,
                scrollState = scrollState,
                startIdList = uiState.startIdList,
                toDetail = toCharacterDetail
            )
        }
    }
}

@Composable
private fun CharacterListContent(
    characterList: List<CharacterInfo>?,
    scrollState: LazyGridState,
    startIdList: ArrayList<Int>,
    toDetail: (Int) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(getItemWidth()),
        state = scrollState
    ) {
        characterList?.let {
            items(
                items = characterList,
                key = {
                    it.id
                }
            ) {
                CharacterItemContent(
                    unitId = it.id,
                    character = it,
                    loved = startIdList.contains(it.id),
                    modifier = Modifier.padding(Dimen.mediumPadding),
                ) {
                    toDetail(it.id)
                }
            }
        }

        items(2) {
            CommonSpacer()
        }
    }
}

@Composable
private fun CharacterListFabContent(
    count: Int,
    scrollState: LazyGridState,
    filter: FilterCharacter?,
    resetFilter: () -> Unit,
    toFilterCharacter: (String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    //回到顶部
    MainSmallFab(
        iconType = MainIconType.TOP
    ) {
        coroutineScope.launch {
            scrollState.scrollToItem(0)
        }
    }
    //重置筛选
    if (filter?.isFilter() == true) {
        MainSmallFab(
            iconType = MainIconType.RESET
        ) {
            resetFilter()
        }
    }

    // 数量显示&筛选按钮
    MainSmallFab(
        iconType = MainIconType.CHARACTER,
        text = "$count"
    ) {
        filter?.let {
            toFilterCharacter(Gson().toJson(filter))
        }
    }

}

/**
 * 角色列表项
 */
@Composable
fun CharacterItemContent(
    unitId: Int,
    character: CharacterInfo?,
    loved: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {

    //图片是否加载成功
    var imageLoadSuccess by remember {
        mutableStateOf(false)
    }
    //预览时，默认为 true
    val defaultError = LocalInspectionMode.current
    var imageLoadError by remember {
        mutableStateOf(defaultError)
    }
    //主色
    val initColor = colorWhite
    var cardMaskColor by remember {
        mutableStateOf(initColor)
    }
    //主要字体颜色
    val textColor = if (imageLoadSuccess) {
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
                onError = { imageLoadError = true }
            ) { result ->
                imageLoadSuccess = true
                //取色
                Palette.from(result.drawable.toBitmap()).generate { palette ->
                    palette?.let {
                        cardMaskColor = Color(it.getDominantColor(Color.Transparent.toArgb()))
                    }
                }
            }
            if (character != null) {
                //名称阴影效果
                if (imageLoadSuccess) {
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
                if (imageLoadSuccess) {
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
                visible = imageLoadSuccess || imageLoadError || LocalInspectionMode.current,
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
            FadeAnimation(visible = loved && (imageLoadSuccess || imageLoadError)) {
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


@CombinedPreviews
@Composable
private fun CharacterItemPreview() {
    PreviewLayout {
        CharacterItemContent(
            unitId = 100101,
            character = CharacterInfo(
                id = 100101,
                position = 100,
                name = stringResource(id = R.string.debug_name)
            ),
            loved = true,
        ) {}
    }
}

