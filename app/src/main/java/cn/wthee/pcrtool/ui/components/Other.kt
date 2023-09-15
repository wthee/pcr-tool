package cn.wthee.pcrtool.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.PositionType
import cn.wthee.pcrtool.data.model.KeywordData
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.isResultError
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.character.getAtkColor
import cn.wthee.pcrtool.ui.character.getAtkText
import cn.wthee.pcrtool.ui.character.getLimitTypeColor
import cn.wthee.pcrtool.ui.character.getLimitTypeText
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.FadeAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorBlue
import cn.wthee.pcrtool.ui.theme.colorCopper
import cn.wthee.pcrtool.ui.theme.colorCyan
import cn.wthee.pcrtool.ui.theme.colorGold
import cn.wthee.pcrtool.ui.theme.colorGray
import cn.wthee.pcrtool.ui.theme.colorGreen
import cn.wthee.pcrtool.ui.theme.colorOrange
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorRed
import cn.wthee.pcrtool.ui.theme.colorSilver
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.dates
import cn.wthee.pcrtool.utils.days
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.utils.fixJpTime
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.utils.isComingSoon
import cn.wthee.pcrtool.utils.isInProgress
import com.google.accompanist.pager.HorizontalPagerIndicator
import kotlinx.coroutines.launch


/**
 * 底部空白占位
 */
@Composable
fun CommonSpacer() {
    Spacer(
        modifier = Modifier
            .navigationBarsPadding()
            .height(Dimen.fabSize + Dimen.fabMargin + Dimen.mediumPadding)
    )
}

/**
 * 位置颜色
 * @param position 角色占位
 */
@Composable
fun getPositionColor(position: Int) = when (PositionType.getPositionType(position)) {
    PositionType.POSITION_0_299 -> colorRed
    PositionType.POSITION_300_599 -> colorGold
    PositionType.POSITION_600_999 -> colorCyan
    PositionType.UNKNOWN -> MaterialTheme.colorScheme.primary
}

/**
 * rank 颜色
 * @param rank rank数值
 */
@Composable
fun getRankColor(rank: Int): Color {
    return when (rank) {
        1 -> colorBlue
        in 2..3 -> colorCopper
        in 4..6 -> colorSilver
        in 7..10 -> colorGold
        in 11..17 -> colorPurple
        in 18..20 -> colorRed
        in 21..23 -> colorGreen
        in 24..27 -> colorOrange
        in 28..99 -> colorCyan
        else -> colorGray
    }
}

/**
 * 带指示器图标
 * @param urls 最大5个
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IconHorizontalPagerIndicator(pagerState: PagerState, urls: List<String>) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxWidth(urls.size * 0.2f),
        contentAlignment = Alignment.Center
    ) {
        //显示指示器
        Row {
            urls.forEachIndexed { index, url ->
                val modifier = if (pagerState.currentPage == index) {
                    Modifier
                        .padding(horizontal = Dimen.mediumPadding)
                        .border(
                            width = Dimen.border,
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.extraSmall
                        )
                } else {
                    Modifier.padding(horizontal = Dimen.mediumPadding)
                }

                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    MainIcon(
                        modifier = modifier,
                        data = url,
                    ) {
                        scope.launch {
                            pagerState.scrollToPage(index)
                        }
                    }
                }
            }
        }
    }
}

/**
 * 指示器
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainHorizontalPagerIndicator(modifier: Modifier, pagerState: PagerState, pageCount: Int) {
    HorizontalPagerIndicator(
        modifier = modifier,
        pagerState = pagerState,
        pageCount = pageCount,
        activeColor = MaterialTheme.colorScheme.primary
    )
}

/**
 * 加载中-圆形
 */
@Composable
fun CircularProgressCompose(
    modifier: Modifier = Modifier,
    size: Dp = Dimen.menuIconSize,
    color: Color = MaterialTheme.colorScheme.primary
) {
    CircularProgressIndicator(
        modifier = modifier
            .size(size)
            .padding(Dimen.smallPadding),
        color = color,
        strokeWidth = Dimen.strokeWidth
    )
}

/**
 * 加载中-圆形
 */
@Composable
fun CircularProgressCompose(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = Dimen.menuIconSize,
    color: Color = MaterialTheme.colorScheme.primary

) {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = progress,
            modifier = modifier
                .size(size)
                .padding(Dimen.smallPadding),
            color = color,
            strokeWidth = Dimen.strokeWidth,
        )
        Text(
            text = (progress * 100).toInt().toString(),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelSmall
        )
    }

}

/**
 * 加载中-直线
 */
@Composable
fun LinearProgressCompose(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    LinearProgressIndicator(
        modifier = modifier
            .height(Dimen.linearProgressHeight)
            .clip(MaterialTheme.shapes.medium),
        color = color
    )
}

/**
 * 加载中进度-直线
 */
@Composable
fun LinearProgressCompose(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary
) {
    LinearProgressIndicator(
        progress = progress,
        modifier = modifier
            .height(Dimen.linearProgressHeight)
            .clip(MaterialTheme.shapes.medium),
        color = color
    )
}

/**
 * 底部搜索栏
 *
 * @param keywordState 关键词，用于查询
 * @param keywordInputState 输入框内文本，不实时更新 [keywordState] ，仅在输入确认后更新
 * @param defaultKeywordList 默认关键词列表
 * @param fabText 不为空时，fab将显示该文本
 */
@OptIn(
    ExperimentalComposeUiApi::class, ExperimentalLayoutApi::class
)
@Composable
fun BottomSearchBar(
    modifier: Modifier = Modifier,
    fabText: String? = null,
    @StringRes labelStringId: Int,
    keywordInputState: MutableState<String>,
    keywordState: MutableState<String>,
    leadingIcon: MainIconType,
    scrollState: LazyListState? = null,
    defaultKeywordList: List<KeywordData>? = null,
    onResetClick: (() -> Unit)? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    //获取焦点
    val focusRequester = remember {
        FocusRequester()
    }
    //键盘是否可见
    val isImeVisible = WindowInsets.isImeVisible
    val openDialog = remember {
        mutableStateOf(false)
    }

    if (!isImeVisible) {
        Row(
            modifier = modifier
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin),
            horizontalArrangement = Arrangement.End
        ) {
            //回到顶部
            scrollState?.let {
                MainSmallFab(
                    iconType = MainIconType.TOP
                ) {
                    coroutineScope.launch {
                        scrollState.scrollToItem(0)
                    }
                }
            }

            //重置
            if (keywordState.value != "") {
                MainSmallFab(
                    iconType = MainIconType.RESET
                ) {
                    keywordState.value = ""
                    keywordInputState.value = ""
                    if (onResetClick != null) {
                        onResetClick()
                    }
                }
            }

            //搜索
            MainSmallFab(
                iconType = if (fabText != null) leadingIcon else MainIconType.SEARCH,
                text = fabText ?: keywordState.value
            ) {
                keyboardController?.show()
                openDialog.value = true
                focusRequester.requestFocus()
                //如有日期弹窗，则关闭日期弹窗
                navViewModel.fabCloseClick.postValue(true)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(Dimen.mediumPadding)
            .imePadding()
    ) {
        //关键词列表，搜索时显示
        ExpandAnimation(
            visible = openDialog.value && isImeVisible && defaultKeywordList?.isNotEmpty() == true,
            modifier = Modifier.padding(bottom = Dimen.mediumPadding)
        ) {
            MainCard(
                modifier = Modifier.padding(bottom = Dimen.mediumPadding),
                elevation = Dimen.popupMenuElevation
            ) {
                Column(
                    modifier = Modifier.padding(Dimen.mediumPadding)
                ) {
                    MainText(text = stringResource(id = R.string.search_suggestion))

                    SuggestionChipGroup(
                        defaultKeywordList ?: arrayListOf(),
                        modifier = Modifier.padding(top = Dimen.mediumPadding)
                    ) { keyword ->
                        keywordInputState.value = keyword
                        keywordState.value = keyword
                        keyboardController?.hide()
                        focusRequester.freeFocus()
                        openDialog.value = false
                    }
                }

            }
        }

        //focusRequester
        MainCard(
            elevation = Dimen.popupMenuElevation
        ) {
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = if (openDialog.value && isImeVisible) Dp.Unspecified else 0.dp)
                    .padding(Dimen.smallPadding)
                    .focusRequester(focusRequester)
                    .alpha(if (openDialog.value && isImeVisible) 1f else 0f),
                value = keywordInputState.value,
                shape = MaterialTheme.shapes.medium,
                onValueChange = { keywordInputState.value = it.deleteSpace },
                textStyle = MaterialTheme.typography.labelLarge,
                leadingIcon = {
                    MainIcon(
                        data = leadingIcon,
                        size = Dimen.fabIconSize
                    )
                },
                trailingIcon = {
                    MainIcon(
                        data = MainIconType.SEARCH,
                        size = Dimen.fabIconSize
                    ) {
                        keyboardController?.hide()
                        keywordState.value = keywordInputState.value
                        focusRequester.freeFocus()
                        openDialog.value = false
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        keywordState.value = keywordInputState.value
                        focusRequester.freeFocus()
                        openDialog.value = false
                    }
                ),
                label = {
                    Text(
                        text = stringResource(id = labelStringId),
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                maxLines = 1,
                singleLine = true,
            )
        }
    }

}


@Composable
fun <T> CommonResponseBox(
    responseData: ResponseData<T>?,
    fabContent: @Composable (BoxScope.(T) -> Unit)? = null,
    content: @Composable (BoxScope.(T) -> Unit),
) {
    Box(modifier = Modifier.fillMaxSize()) {
        FadeAnimation(visible = isResultError(responseData)) {
            CenterTipText(text = stringResource(id = R.string.response_error))
        }
        FadeAnimation(visible = responseData?.data != null) {
            content(responseData!!.data!!)
        }
        if (responseData == null) {
            CircularProgressCompose(
                modifier = Modifier
                    .padding(vertical = Dimen.largePadding)
                    .align(Alignment.Center)
            )
        }

        if (responseData?.data != null && fabContent != null) {
            fabContent(responseData.data!!)
        }
    }
}

/**
 * 日程标题
 * @param showDays 显示天数
 * @param showOverdueColor 过期日程颜色变灰色
 */
@Composable
fun EventTitle(
    startTime: String,
    endTime: String,
    showDays: Boolean = true,
    showOverdueColor: Boolean = false
) {
    val today = getToday()
    val sd = startTime.fixJpTime
    val ed = endTime.fixJpTime
    val inProgress = isInProgress(today, startTime, endTime)
    val comingSoon = isComingSoon(today, startTime)

    val color = when {
        inProgress -> {
            MaterialTheme.colorScheme.primary
        }

        comingSoon -> {
            colorPurple
        }

        else -> {
            if (showOverdueColor) {
                MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.primary
            }
        }
    }

    //日期
    MainTitleText(
        text = sd.substring(0, 10),
        modifier = Modifier.padding(end = Dimen.smallPadding),
        backgroundColor = color
    )
    //天数，预览时不显示
    if (showDays && !LocalInspectionMode.current) {
        val days = ed.days(sd)
        MainTitleText(
            text = days,
            modifier = Modifier.padding(end = Dimen.smallPadding),
            backgroundColor = color
        )
    }
    //计时
    EventTitleCountdown(today, sd, ed, inProgress, comingSoon)
}

/**
 * 日程倒计时
 */
@Composable
fun EventTitleCountdown(
    today: String,
    sd: String,
    ed: String,
    inProgress: Boolean,
    comingSoon: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (inProgress) {
            MainIcon(
                data = MainIconType.TIME_LEFT,
                size = Dimen.smallIconSize,
            )
            MainContentText(
                text = ed.dates(today),
                modifier = Modifier.padding(start = Dimen.smallPadding),
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.primary
            )
        }
        if (comingSoon) {
            MainIcon(
                data = MainIconType.COUNTDOWN,
                size = Dimen.smallIconSize,
                tint = colorPurple
            )
            MainContentText(
                text = sd.dates(today),
                modifier = Modifier.padding(start = Dimen.smallPadding),
                textAlign = TextAlign.Start,
                color = colorPurple
            )
        }
    }
}

/**
 * 装备适用角色
 */
@Composable
fun UnitList(unitIds: List<Int>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.mediumPadding),
        state = rememberLazyListState()
    ) {
        //标题
        item {
            MainText(
                text = stringResource(R.string.extra_equip_unit),
                modifier = Modifier
                    .padding(Dimen.largePadding)
                    .fillMaxWidth()
            )
        }

        //角色图标
        item {
            GridIconList(unitIds, isSubLayout = false) {}
        }

        item {
            CommonSpacer()
        }
    }
}

/**
 * 角色标签行
 *
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CharacterTagRow(
    modifier: Modifier = Modifier,
    unknown: Boolean = false,
    basicInfo: CharacterInfo?,
    tipText: String? = null,
    endText: String? = null,
    endTextColor: Color? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalArrangement = Arrangement.Center
    ) {
        if (!unknown) {
            //位置
            CharacterPositionTag(
                modifier = Modifier
                    .padding(
                        start = Dimen.smallPadding
                    )
                    .align(Alignment.CenterVertically),
                position = basicInfo!!.position
            )


            Row {
                //获取方式
                CharacterTag(
                    modifier = Modifier.padding(Dimen.smallPadding),
                    text = getLimitTypeText(limitType = basicInfo.limitType),
                    backgroundColor = getLimitTypeColor(limitType = basicInfo.limitType)
                )
                //攻击
                CharacterTag(
                    modifier = Modifier.padding(
                        bottom = Dimen.smallPadding,
                        top = Dimen.smallPadding
                    ),
                    text = getAtkText(atkType = basicInfo.atkType),
                    backgroundColor = getAtkColor(atkType = basicInfo.atkType)
                )
            }

            //日期
            if (endText != null && endTextColor != null) {
                CharacterTag(
                    text = endText,
                    backgroundColor = Color.Transparent,
                    textColor = endTextColor,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically),
                    endAlignment = true
                )
            }

        } else {
            Row(modifier = Modifier.padding(bottom = Dimen.smallPadding)) {
                if (unknown && tipText != null) {
                    //提示
                    CharacterTag(
                        modifier = Modifier.padding(start = Dimen.smallPadding),
                        text = tipText,
                        backgroundColor = Color.Transparent,
                        textColor = colorGray,
                        fontWeight = FontWeight.Light
                    )
                }

                //日期
                if (endText != null && endTextColor != null) {
                    CharacterTag(
                        text = endText,
                        backgroundColor = Color.Transparent,
                        textColor = endTextColor,
                        fontWeight = FontWeight.Light,
                        modifier = Modifier.weight(1f),
                        endAlignment = true
                    )
                }
            }
        }
    }
}

/**
 * 位置信息
 *
 */
@Composable
fun CharacterPositionTag(
    modifier: Modifier = Modifier,
    position: Int
) {
    val positionText = getPositionText(position)

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        //位置图标
        PositionIcon(
            position = position
        )
        //位置
        CharacterTag(
            modifier = Modifier.padding(start = Dimen.smallPadding),
            text = positionText,
            backgroundColor = getPositionColor(position)
        )
    }
}

/**
 * 获取位置描述
 */
@Composable
private fun getPositionText(position: Int): String {
    var positionText = ""
    val pos = when (PositionType.getPositionType(position)) {
        PositionType.POSITION_0_299 -> stringResource(id = R.string.position_0)
        PositionType.POSITION_300_599 -> stringResource(id = R.string.position_1)
        PositionType.POSITION_600_999 -> stringResource(id = R.string.position_2)
        PositionType.UNKNOWN -> Constants.UNKNOWN
    }
    if (pos != Constants.UNKNOWN) {
        positionText = "$pos $position"
    }
    return positionText
}

/**
 * 角色属性标签
 *
 * @param leadingContent 开头内容
 */
@Composable
fun CharacterTag(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = colorWhite,
    fontWeight: FontWeight = FontWeight.ExtraBold,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    endAlignment: Boolean = false,
    leadingContent: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(color = backgroundColor, shape = CircleShape)
            .padding(horizontal = Dimen.mediumPadding),
        contentAlignment = if (endAlignment) Alignment.CenterEnd else Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            leadingContent?.let {
                it()
                Spacer(modifier = Modifier.padding(start = Dimen.smallPadding))
            }

            Text(
                text = text,
                color = textColor,
                style = style,
                fontWeight = fontWeight,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@CombinedPreviews
@Composable
private fun AllPreview() {
    val text = stringResource(id = R.string.debug_short_text)
    PreviewLayout {
        MainTitleText(text = text)
        MainButton(text = text) {}
        SubButton(text = text) {}
        RankText(rank = 21)
        SelectText(text = text, selected = true)
        IconTextButton(icon = MainIconType.MORE, text = text)
        CommonTitleContentText(title = text, content = text)
        MainTabRow(
            pagerState = rememberPagerState { 2 },
            tabs = arrayListOf(text, text)
        )
    }
}

@CombinedPreviews
@Composable
private fun CharacterTagPreview() {
    PreviewLayout {
        CharacterTag(
            text = getLimitTypeText(limitType = 1),
            backgroundColor = getLimitTypeColor(limitType = 1)
        )
    }
}