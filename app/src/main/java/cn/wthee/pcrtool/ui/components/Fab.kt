package cn.wthee.pcrtool.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.enums.RankColor
import cn.wthee.pcrtool.data.enums.RankSelectType
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.ui.theme.defaultTween
import cn.wthee.pcrtool.utils.VibrateUtil
import kotlinx.coroutines.launch

/**
 * 通用悬浮按钮
 *
 * @param hasNavBarPadding 适配导航栏
 * @param loading true 显示圆形加载中进度条
 * @param iconScale 图标缩放，非ImageVector才生效
 * @param vibrate 点击振动
 */
@Composable
fun MainSmallFab(
    iconType: Any,
    modifier: Modifier = Modifier,
    text: String = "",
    hasNavBarPadding: Boolean = true,
    iconScale: ContentScale = ContentScale.FillWidth,
    vibrate: Boolean = true,
    tintColor: Color? = null,
    onClick: () -> Unit = {},
    loading: Boolean = false
) {
    val context = LocalContext.current
    val isTextFab = text != "" && !loading
    val contentColor = tintColor ?: MaterialTheme.colorScheme.primary


    SmallFloatingActionButton(
        onClick = {
            if (vibrate) {
                VibrateUtil(context).single()
            }
            onClick()
        },
        shape = CircleShape,
        modifier = modifier
            .then(
                //导航栏间距
                if (hasNavBarPadding) {
                    Modifier.navigationBarsPadding()
                } else {
                    Modifier
                }
            )
            .then(
                //文本fab
                if (isTextFab) {
                    Modifier.padding(horizontal = Dimen.textFabMargin)
                } else {
                    Modifier
                }
            ),
        contentColor = contentColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = (if (isTextFab) {
                Modifier.padding(start = Dimen.largePadding)
            } else {
                Modifier.padding(start = 0.dp)
            }).animateContentSize(defaultTween())
        ) {

            if (!loading) {
                MainIcon(
                    data = iconType,
                    size = Dimen.fabIconSize,
                    colorFilter = ColorFilter.tint(contentColor),
                    tint = contentColor,
                    contentScale = iconScale
                )

                Text(
                    text = text,
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .then(
                            if (isTextFab) {
                                Modifier.padding(
                                    start = Dimen.mediumPadding,
                                    end = Dimen.largePadding
                                )
                            } else {
                                Modifier
                            }
                        )
                        .widthIn(max = Dimen.fabTextMaxWidth),
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            } else {
                CircularProgressCompose()
            }
        }
    }

}

/**
 * 可展开fab
 *
 * @param expanded 状态
 * @param onClick 点击监听改变状态
 * @param icon 按钮图标
 * @param tint 按钮图标和文本颜色
 * @param text 按钮文本
 * @param isSecondLineFab fab 所在行，是否在第二行
 * @param paddingValues 自定义边距
 * @param customFabContent 自定义未展开布局
 * @param expandedContent 展开布局具体内容
 */
@Composable
fun ExpandableFab(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onClick: () -> Unit,
    icon: MainIconType? = null,
    tint: Color = MaterialTheme.colorScheme.primary,
    text: String = "",
    isSecondLineFab: Boolean = false,
    noPadding: Boolean = false,
    paddingValues: PaddingValues? = null,
    animateContent: Boolean = true,
    customFabContent: @Composable (() -> Unit)? = null,
    expandedContent: @Composable () -> Unit
) {
    val context = LocalContext.current
    val endPadding = if (noPadding) {
        0.dp
    } else {
        if (isSecondLineFab) {
            Dimen.fabMargin
        } else {
            Dimen.fabMarginEnd
        }
    }
    val bottomPadding = if (noPadding) {
        0.dp
    } else {
        if (isSecondLineFab) {
            Dimen.fabMarginLargeBottom
        } else {
            Dimen.fabMargin
        }
    }

    val mPaddingValues = paddingValues ?: PaddingValues(
        start = if (noPadding) 0.dp else Dimen.fabMargin,
        end = endPadding,
        top = Dimen.largePadding,
        bottom = bottomPadding
    )

    SmallFloatingActionButton(
        modifier = modifier
            .then(
                if (animateContent) {
                    Modifier.animateContentSize(defaultSpring())
                } else {
                    Modifier
                }
            )
            .widthIn(max = Dimen.itemMaxWidth)
            .padding(mPaddingValues)
            .padding(start = Dimen.textFabMargin, end = Dimen.textFabMargin)
            .imePadding()
            .navigationBarsPadding(),
        shape = if (expanded) MaterialTheme.shapes.medium else CircleShape,
        onClick = {
            //点击展开布局
            if (!expanded) {
                VibrateUtil(context).single()
                onClick()
            }
        },
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = if (expanded) {
                Dimen.popupMenuElevation
            } else {
                Dimen.fabElevation
            }
        ),
    ) {
        if (expanded) {
            //展开内容
            expandedContent()
        } else {
            if (customFabContent != null) {
                customFabContent()
            } else {
                //fab
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = Dimen.largePadding)
                ) {
                    if (icon != null) {
                        MainIcon(
                            data = icon,
                            tint = tint,
                            size = Dimen.fabIconSize
                        )
                    }
                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Center,
                        color = tint,
                        modifier = Modifier.padding(
                            start = Dimen.mediumPadding, end = Dimen.largePadding
                        )
                    )
                }
            }
        }
    }
}

/**
 * 切换
 */
@Composable
fun SelectTypeFab(
    modifier: Modifier = Modifier,
    openDialog: Boolean,
    changeDialog: (Boolean) -> Unit,
    icon: MainIconType,
    tabs: List<String>,
    selectedIndex: Int,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    isSecondLineFab: Boolean = false,
    noPadding: Boolean = false,
    changeSelect: (Int) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    //切换
    ExpandableFab(
        modifier = modifier,
        expanded = openDialog,
        onClick = {
            changeDialog(true)
        },
        icon = icon,
        tint = selectedColor,
        text = tabs[selectedIndex],
        isSecondLineFab = isSecondLineFab,
        noPadding = noPadding
    ) {
        Column(
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .widthIn(min = Dimen.selectFabMinWidth),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            //选择
            tabs.forEachIndexed { index, tab ->
                SelectText(
                    selected = selectedIndex == index,
                    text = tab,
                    textStyle = MaterialTheme.typography.titleLarge,
                    selectedColor = selectedColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimen.mediumPadding),
                    onClick = {
                        coroutineScope.launch {
                            changeSelect(index)
                        }
                        changeDialog(false)
                    }
                )
            }
        }
    }
}


/**
 * 选择RANK范围
 */
@Composable
fun RankRangePickerCompose(
    rank0: Int,
    rank1: Int,
    maxRank: Int,
    openDialog: Boolean,
    changeDialog: (Boolean) -> Unit,
    type: RankSelectType = RankSelectType.DEFAULT,
    updateRank: (Int, Int) -> Unit
) {
    val rankList = arrayListOf<Int>()
    for (i in maxRank downTo 1) {
        rankList.add(i)
    }

    //选择
    val selectIndex0 = remember {
        mutableIntStateOf(maxRank - rank0)
    }
    val selectIndex1 = remember {
        mutableIntStateOf(maxRank - rank1)
    }
    LaunchedEffect(selectIndex0.intValue, selectIndex1.intValue) {
        if (maxRank != 0) {
            updateRank(maxRank - selectIndex0.intValue, maxRank - selectIndex1.intValue)
        }
    }

    //关闭监听
    BackHandler(openDialog) {
        changeDialog(false)
    }

    ExpandableFab(
        expanded = openDialog,
        onClick = {
            changeDialog(true)
        },
        icon = MainIconType.RANK_SELECT,
        text = stringResource(id = R.string.rank_select),
        isSecondLineFab = true
    ) {
        Column(
            modifier = Modifier
                .padding(
                    horizontal = Dimen.mediumPadding,
                    vertical = Dimen.largePadding
                )
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Bottom
        ) {
            //当前
            MainText(text = stringResource(id = R.string.cur_rank))
            RankSelectItem(
                selectIndex = selectIndex0,
                rankList = rankList,
                targetType = RankSelectType.DEFAULT,
                currentRank = maxRank - selectIndex0.intValue
            )
            //目标
            MainText(
                text = stringResource(id = R.string.target_rank),
                modifier = Modifier.padding(top = Dimen.largePadding)
            )
            RankSelectItem(
                selectIndex = selectIndex1,
                rankList = rankList,
                targetType = type,
                currentRank = maxRank - selectIndex0.intValue
            )
        }
    }
}

/**
 * RANK 选择器
 */
@Composable
private fun RankSelectItem(
    selectIndex: MutableState<Int>,
    rankList: List<Int>,
    targetType: RankSelectType,
    currentRank: Int
) {
    val list = rankList.filter {
        targetType == RankSelectType.DEFAULT ||
                (targetType == RankSelectType.LIMIT && it >= currentRank)
    }
    VerticalGridList(
        itemCount = list.size,
        itemWidth = Dimen.rankTextWidth,
        contentPadding = Dimen.smallPadding
    ) {
        val rank = list[it]
        val rankColor = RankColor.getRankColor(rank = rank)
        val selected = selectIndex.value == it

        MainChip(
            index = it,
            selected = selected,
            selectIndex = selectIndex,
            text = rankFillBlank(rank),
            selectedColor = rankColor
        )
    }
}

/**
 * 填充空格
 */
private fun rankFillBlank(rank: Int): String {
    return when (rank) {
        in 0..9 -> "0$rank"
        else -> "$rank"
    }
}


@CombinedPreviews
@Composable
private fun FabComposePreview() {
    PreviewLayout {
        Row {
            MainSmallFab(iconType = MainIconType.ANIMATION)
            MainSmallFab(iconType = MainIconType.ANIMATION, text = "fab")
        }
    }
}

@CombinedPreviews
@Composable
private fun SelectTypeFabPreview() {
    PreviewLayout {
        val debugText = stringResource(id = R.string.debug_short_text)
        val debugText2 = stringResource(id = R.string.debug_name)
        SelectTypeFab(
            openDialog = true,
            changeDialog = {},
            icon = MainIconType.ADD,
            tabs = arrayListOf(debugText, debugText2, debugText),
            selectedIndex = 2,
            changeSelect = {}
        )
        SelectTypeFab(
            openDialog = false,
            changeDialog = {},
            icon = MainIconType.ADD,
            tabs = arrayListOf(debugText, debugText2, debugText),
            selectedIndex = 2,
            changeSelect = {}
        )
    }
}

@CombinedPreviews
@Composable
private fun RankRangePickerComposePreview() {
    PreviewLayout {
        RankRangePickerCompose(
            rank0 = 1,
            rank1 = 30,
            maxRank = 30,
            openDialog = true,
            changeDialog = {},
            updateRank = { _, _ -> }
        )
    }
}