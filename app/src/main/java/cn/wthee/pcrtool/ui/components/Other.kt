package cn.wthee.pcrtool.ui.components

import androidx.activity.compose.BackHandler
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.Shape
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
import cn.wthee.pcrtool.data.enums.AtkType
import cn.wthee.pcrtool.data.enums.CharacterLimitType
import cn.wthee.pcrtool.data.enums.IconResourceType
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.PositionType
import cn.wthee.pcrtool.data.model.KeywordData
import cn.wthee.pcrtool.navigation.navigateUp
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.ExpandAnimation
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorGray
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.ui.theme.noShape
import cn.wthee.pcrtool.utils.dates
import cn.wthee.pcrtool.utils.days
import cn.wthee.pcrtool.utils.deleteSpace
import cn.wthee.pcrtool.utils.fixJpTime
import cn.wthee.pcrtool.utils.getToday
import cn.wthee.pcrtool.utils.isComingSoon
import cn.wthee.pcrtool.utils.isInProgress
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * 通用加载数据 box
 *
 * @param stateType 状态
 * @param loadingContent 加载中布局
 * @param errorContent 异常布局
 * @param noDataContent 无数据布局
 * @param successContent 加载成功后的布局
 */
@Composable
fun StateBox(
    stateType: LoadingState,
    loadingContent: @Composable (() -> Unit)? = {
        Box(modifier = Modifier.fillMaxSize()) {
            CircularProgressCompose(
                modifier = Modifier
                    .padding(vertical = Dimen.largePadding)
                    .align(Alignment.Center)
            )
        }
    },
    errorContent: @Composable () -> Unit = {
        CenterTipText(stringResource(id = R.string.data_get_error))
    },
    noDataContent: @Composable () -> Unit = {
        CenterTipText(stringResource(id = R.string.no_data))
    },
    successContent: @Composable () -> Unit
) {
    when (stateType) {
        LoadingState.Loading -> if (loadingContent != null) {
            loadingContent()
        }
        LoadingState.NoData -> noDataContent()
        LoadingState.Error -> errorContent()
        LoadingState.Success -> successContent()
    }
}


/**
 * 通用布局
 * 已默认设置：背景色、主悬浮按钮
 *
 * @param backgroundColor 背景色
 * @param fillMaxSize 最大尺寸布局，默认true
 * @param onMainFabClick 主悬浮按钮点击事件，null时为返回
 * @param mainFabIcon 主悬浮按钮图标
 * @param hideMainFab 是否隐藏主悬浮按钮
 * @param enableClickClose 是否启用点击背景关闭功能
 * @param onCloseClick 点击背景关闭回调
 * @param fabWithCustomPadding 需自定义 padding 的悬浮按钮
 * @param fab 正常悬浮按钮
 * @param secondLineFab 第二行显示的悬浮按钮
 * @param content 内容
 */
@Composable
fun MainScaffold(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentAlignment: Alignment = Alignment.TopStart,
    shape: Shape = noShape(),
    fillMaxSize: Boolean = true,
    onMainFabClick: (() -> Unit)? = null,
    mainFabIcon: MainIconType = MainIconType.BACK,
    hideMainFab: Boolean = false,
    enableClickClose: Boolean = false,
    onCloseClick: () -> Unit = {},
    fabWithCustomPadding: @Composable BoxScope.() -> Unit = {},
    fab: @Composable RowScope.() -> Unit = {},
    secondLineFab: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    //返回拦截
    BackHandler(enableClickClose) {
        onCloseClick()
    }

    Box(
        modifier = (if (fillMaxSize) {
            modifier.fillMaxSize()
        } else {
            modifier
        }).background(color = backgroundColor, shape),
        contentAlignment = contentAlignment
    ) {
        //主要内容
        content()

        //fab内容
        Box(
            modifier = Modifier
                .clickClose(enableClickClose) {
                    onCloseClick()
                }
                .navigationBarsPadding()
                .align(Alignment.BottomEnd),
            contentAlignment = Alignment.BottomEnd
        ) {
            //fab 第二行
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
            ) {
                secondLineFab()
            }

            //底部 fab
            fabWithCustomPadding()
            //底部 fab 行
            Row(
                modifier = Modifier
                    .padding(
                        end = Dimen.fabMarginEnd,
                        bottom = Dimen.fabMargin
                    ),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Bottom
            ) {
                fab()
            }


            //主按钮
            if (!hideMainFab) {
                MainSmallFab(
                    iconType = mainFabIcon,
                    modifier = Modifier
                        .padding(
                            end = Dimen.fabMargin,
                            bottom = Dimen.fabMargin
                        )
                ) {
                    if (onMainFabClick != null) {
                        onMainFabClick()
                    } else {
                        //默认返回操作
                        navigateUp()
                    }
                }
            }
        }


    }
}

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
 * 加载中-圆形
 */
@Composable
fun CircularProgressCompose(
    modifier: Modifier = Modifier,
    size: Dp = Dimen.menuIconSize,
    strokeWidth: Dp = Dimen.strokeWidth,
    color: Color = MaterialTheme.colorScheme.primary
) {
    CircularProgressIndicator(
        modifier = modifier
            .size(size)
            .padding(Dimen.exSmallPadding),
        color = color,
        strokeWidth = strokeWidth
    )
}

/**
 * 加载中-圆形
 */
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
                .padding(Dimen.exSmallPadding),
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
    leadingIcon: MainIconType,
    defaultKeywordList: List<KeywordData>? = null,
    keyword: String,
    openSearch: Boolean,
    showReset: Boolean,
    changeSearchBar: (Boolean) -> Unit,
    changeKeyword: (String) -> Unit,
    onTopClick: (suspend CoroutineScope.() -> Unit)? = null,
    onResetClick: (() -> Unit)? = null,
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val coroutineScope = rememberCoroutineScope()
    //获取焦点
    val focusRequester = remember {
        FocusRequester()
    }
    //关键词输入
    val keywordInputState = remember {
        mutableStateOf("")
    }
    //键盘是否可见
    val isImeVisible = WindowInsets.isImeVisible
    LaunchedEffect(isImeVisible) {
        if (!isImeVisible) {
            changeSearchBar(false)
        }
    }
    LaunchedEffect(openSearch) {
        if (openSearch) {
            keyboardController?.show()
        }
    }


    Box {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = modifier
                .fillMaxWidth()
                .padding(end = Dimen.fabMarginEnd, bottom = Dimen.fabMargin),
        ) {
            if (!openSearch) {
                //回到顶部
                onTopClick?.let {
                    MainSmallFab(
                        iconType = MainIconType.TOP
                    ) {
                        coroutineScope.launch {
                            onTopClick()
                        }
                    }
                }

                //重置
                if (showReset) {
                    MainSmallFab(
                        iconType = MainIconType.RESET
                    ) {
                        changeKeyword("")
                        if (onResetClick != null) {
                            onResetClick()
                        }
                    }
                }

                //搜索
                MainSmallFab(
                    iconType = if (fabText != null) leadingIcon else MainIconType.SEARCH,
                    text = fabText ?: keyword
                ) {
                    keyboardController?.show()
                    changeSearchBar(true)
                    focusRequester.requestFocus()
                }
            }
        }

        Column(
            modifier = modifier
                .imePadding(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            //关键词列表，搜索时显示
            ExpandAnimation(
                visible = openSearch && defaultKeywordList?.isNotEmpty() == true,
            ) {
                MainCard(
                    modifier = Modifier.padding(Dimen.largePadding),
                    elevation = Dimen.popupMenuElevation,
                ) {
                    Column(
                        modifier = Modifier.padding(Dimen.mediumPadding)
                    ) {
                        MainText(text = stringResource(id = R.string.search_suggestion))

                        SuggestionChipGroup(
                            defaultKeywordList ?: arrayListOf(),
                            modifier = Modifier.padding(top = Dimen.mediumPadding)
                        ) {
                            changeKeyword(it)
                            keyboardController?.hide()
                            focusRequester.freeFocus()
                            changeSearchBar(false)
                        }
                    }

                }
            }

            //focusRequester
            MainCard(
                modifier = Modifier.padding(Dimen.largePadding),
                elevation = Dimen.popupMenuElevation,
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(if (openSearch) 1f else 0.1f)
                        .heightIn(max = if (openSearch) Dp.Unspecified else 0.dp)
                        .padding(Dimen.smallPadding)
                        .focusRequester(focusRequester)
                        .alpha(if (openSearch) 1f else 0f),
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
                            changeKeyword(keywordInputState.value)
                            focusRequester.freeFocus()
                            changeSearchBar(false)
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            keyboardController?.hide()
                            changeKeyword(keywordInputState.value)
                            focusRequester.freeFocus()
                            changeSearchBar(false)
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
 * 装备、角色图标布局，带标题
 */
@Composable
fun IconListContent(
    idList: List<Int>,
    title: String,
    iconResourceType: IconResourceType,
    onClickItem: ((Int) -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Dimen.mediumPadding)
            .verticalScroll(rememberScrollState())
    ) {
        //标题
        MainText(
            text = title,
            modifier = Modifier
                .padding(Dimen.largePadding)
                .fillMaxWidth()
        )

        //图标
        GridIconList(
            idList = idList,
            iconResourceType = iconResourceType,
            onClickItem = onClickItem
        )

        CommonSpacer()
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

            val limitType = CharacterLimitType.getByType(basicInfo.limitType)

            Row {
                //获取方式
                CharacterTag(
                    modifier = Modifier.padding(Dimen.smallPadding),
                    text = stringResource(id = limitType.typeNameId),
                    backgroundColor = limitType.color
                )
                //攻击
                val atkType = AtkType.getByType(basicInfo.atkType)
                CharacterTag(
                    modifier = Modifier.padding(
                        bottom = Dimen.smallPadding,
                        top = Dimen.smallPadding
                    ),
                    text = stringResource(id = atkType.typeNameId),
                    backgroundColor = atkType.color
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
                if (tipText != null) {
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
    val positionText =
        stringResource(id = PositionType.getPositionType(position).typeNameId) + " $position"

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        //位置图标
        PositionIcon(
            position = position
        )
        //位置
        CharacterTag(
            modifier = Modifier.padding(start = Dimen.smallPadding),
            text = positionText,
            backgroundColor = PositionType.getPositionType(position).color
        )
    }
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

@CombinedPreviews
@Composable
private fun AllPreview() {
    PreviewLayout {
        CircularProgressCompose(progress = 0.31f)
        LinearProgressCompose(progress = 0.31f)
        EventTitle(startTime = "2023-12-12 22:22:22", endTime = "2023-12-15 22:22:22")
        EventTitleCountdown(
            today = "2023/12/13 22:22:22",
            sd = "2023/12/12 22:22:22",
            ed = "2023/12/15 22:22:22",
            inProgress = true,
            comingSoon = false
        )
        EventTitleCountdown(
            today = "2023/12/10 22:22:22",
            sd = "2023/12/12 22:22:22",
            ed = "2023/12/15 22:22:22",
            inProgress = false,
            comingSoon = true
        )
    }
}

@CombinedPreviews
@Composable
private fun CharacterTagPreview() {
    PreviewLayout {
        val text = stringResource(id = R.string.debug_short_text)
        Column(modifier = Modifier.width(150.dp)) {
            CharacterTagRow(
                unknown = false,
                basicInfo = CharacterInfo(
                    position = 123,
                    atkType = 1,
                    limitType = 2
                ),
                tipText = text,
                endText = text,
            )
        }
        CharacterTagRow(
            unknown = false,
            basicInfo = CharacterInfo(
                position = 123,
                atkType = 1,
                limitType = 2
            ),
            tipText = text,
            endText = text,
        )
    }
}