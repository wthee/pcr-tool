package cn.wthee.pcrtool.ui.common

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shape
import cn.wthee.pcrtool.ui.theme.defaultSpring
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.getFormatText
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState
import kotlinx.coroutines.launch

/**
 * 蓝底白字
 */
@Composable
fun MainTitleText(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    Text(
        text = text,
        color = Color.White,
        style = textStyle,
        modifier = modifier
            .background(color = backgroundColor, shape = Shape.small)
            .padding(start = Dimen.mediumPadding, end = Dimen.mediumPadding)
    )
}

/**
 * 内容文本
 */
@Composable
fun MainContentText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.End,
    selectable: Boolean = false
) {
    if (selectable) {
        SelectionContainer(modifier = modifier) {
            Text(
                text = text,
                textAlign = textAlign,
                color = color,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    } else {
        Text(
            text = text,
            textAlign = textAlign,
            color = color,
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier,
        )
    }
}

/**
 * 蓝色加粗标题
 */
@Composable
fun MainText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Center,
    color: Color = MaterialTheme.colorScheme.primary,
    selectable: Boolean = false,
) {
    if (selectable) {
        SelectionContainer(modifier = modifier) {
            Text(
                text = text,
                color = color,
                style = MaterialTheme.typography.titleMedium,
                textAlign = textAlign,
                fontWeight = FontWeight.Black,
            )
        }
    } else {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.titleMedium,
            textAlign = textAlign,
            fontWeight = FontWeight.Black,
            modifier = modifier
        )
    }

}

/**
 * 副标题
 */
@Composable
fun Subtitle1(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    selectable: Boolean = false,
    maxLines: Int = Int.MAX_VALUE
) {
    if (selectable) {
        SelectionContainer(modifier = modifier) {
            Text(
                text = text,
                color = color,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleMedium,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
            )
        }
    } else {
        Text(
            text = text,
            color = color,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * 副标题
 */
@Composable
fun Subtitle2(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    selectable: Boolean = false,
    maxLines: Int = Int.MAX_VALUE
) {
    if (selectable) {
        SelectionContainer(modifier = modifier) {
            Text(
                text = text,
                color = color,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleSmall,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
            )
        }
    } else {
        Text(
            text = text,
            color = color,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            modifier = modifier,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

/**
 * 灰色标注字体
 */
@Composable
fun CaptionText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.End,
    maxLines: Int = Int.MAX_VALUE
) {
    Text(
        text = text,
        textAlign = textAlign,
        color = color,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}

/**
 * 分割线
 */
@Composable
fun LineCompose(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth()) {
        Spacer(
            modifier = modifier
                .fillMaxWidth()
                .height(Dimen.divLineHeight)
                .background(colorResource(id = R.color.div_line))
                .align(Alignment.Center)
        )
    }

}

/**
 * 分割线
 */
@Composable
fun DivCompose(modifier: Modifier = Modifier) {
    Spacer(
        modifier = modifier
            .padding(Dimen.largePadding)
            .width(Dimen.lineWidth)
            .height(Dimen.lineHeight)
            .background(MaterialTheme.colorScheme.primary)
    )
}

/**
 * 主操作按钮
 */
@Composable
fun MainButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onPrimary,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    Button(
        shape = Shape.medium,
        modifier = modifier.padding(Dimen.smallPadding),
        onClick = {
            VibrateUtil(context).single()
            onClick.invoke()
        }
    ) {
        Text(
            text = text,
            color = color,
            style = textStyle
        )
    }
}

/**
 * 次操作按钮
 */
@Composable
fun SubButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    OutlinedButton(
        shape = Shape.medium,
        modifier = modifier.padding(Dimen.smallPadding),
        border = BorderStroke(Dimen.divLineHeight, color = color),
        onClick = {
            VibrateUtil(context).single()
            onClick.invoke()
        }
    ) {
        Text(text = text, color = color, style = textStyle)
    }
}


/**
 * 文本操作按钮
 */
@Composable
fun MainTexButton(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    TextButton(
        shape = Shape.medium,
        modifier = modifier.padding(Dimen.smallPadding),
        onClick = {
            VibrateUtil(context).single()
            onClick.invoke()
        }
    ) {
        Text(text = text, color = color, style = textStyle)
    }
}

/**
 * RANK 文本
 * type: 0 默认 1 白字+底色
 */
@Composable
fun RankText(
    rank: Int,
    style: TextStyle,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
    type: Int = 0
) {
    val color = getRankColor(rank)
    val text = getFormatText(rank)
    if (type == 0) {
        Text(
            text = text,
            textAlign = textAlign,
            color = color,
            style = style,
            modifier = modifier
        )
    } else {
        MainTitleText(
            text = text,
            textStyle = MaterialTheme.typography.titleMedium,
            backgroundColor = color,
            modifier = modifier
        )
    }

}

//rank 颜色
@Composable
fun getRankColor(rank: Int): Color {
    val colorId = when (rank) {
        in 2..3 -> R.color.color_rank_2_3
        in 4..6 -> R.color.color_rank_4_6
        in 7..10 -> R.color.color_rank_7_10
        in 11..17 -> R.color.color_rank_11_17
        in 18..20 -> R.color.color_rank_18_20
        in 21..23 -> R.color.color_rank_21_23
        in 24..99 -> R.color.color_rank_24
        else -> R.color.color_rank_1
    }
    return colorResource(id = colorId)
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
 * 卡片布局
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    shape: RoundedCornerShape = Shape.medium,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    content: @Composable ColumnScope.() -> Unit
) {
    val context = LocalContext.current

    var mModifier =
        modifier
            .fillMaxWidth()
            .shadow(Dimen.cardElevation, shape, true)

    if (onClick != null) {
        mModifier = mModifier.clickable {
            VibrateUtil(context).single()
            onClick.invoke()
        }
    }

    Card(
        modifier = mModifier,
        content = content,
        containerColor = backgroundColor
    )
}

/**
 * 选中文本
 * @param selected 是否选中
 * @param selectedColor 选中的颜色
 */
@Composable
fun SelectText(
    modifier: Modifier = Modifier,
    selected: Boolean,
    text: String,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = Color.Unspecified,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    padding: Dp = Dimen.smallPadding,
    margin: Dp = Dimen.smallPadding,
    textAlign: TextAlign = TextAlign.Center
) {
    val mModifier = if (selected) {
        modifier
            .padding(top = margin)
            .background(color = selectedColor, shape = Shape.small)
            .padding(start = padding, end = padding)
    } else {
        modifier.padding(top = margin)
    }
    Text(
        text = text,
        color = if (selected) MaterialTheme.colorScheme.onPrimary else textColor,
        style = textStyle,
        maxLines = 1,
        textAlign = textAlign,
        overflow = TextOverflow.Ellipsis,
        modifier = mModifier
    )
}


/**
 * 角色限定类型文本样式
 */
@Composable
fun CharacterLimitText(
    modifier: Modifier = Modifier,
    characterInfo: CharacterInfo
) {
    val color: Color
    val type: String
    when (characterInfo.isLimited) {
        1 -> {
            if (characterInfo.rarity == 1) {
                type = stringResource(id = R.string.type_event_limit)
                color = colorResource(id = R.color.color_rank_21_23)
            } else {
                type = stringResource(id = R.string.type_limit)
                color = colorResource(id = R.color.color_rank_18_20)
            }
        }
        else -> {
            type = stringResource(id = R.string.type_normal)
            color = colorResource(id = R.color.color_rank_7_10)
        }
    }
    Subtitle2(modifier = modifier, text = type, color = color)
}


/**
 * 角色站位文本样式
 */
@Composable
fun CharacterPositionText(
    modifier: Modifier = Modifier,
    showColor: Boolean = true,
    showText: Boolean = false,
    position: Int,
    textAlign: TextAlign = TextAlign.Center,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    val color = if (showColor) {
        colorResource(
            id = when (position) {
                in 1..299 -> R.color.color_rank_18_20
                in 300..599 -> R.color.color_rank_7_10
                in 600..9999 -> R.color.colorPrimary
                else -> R.color.black
            }
        )
    } else {
        Color.Unspecified
    }
    var text = position.toString()
    if (showText) {
        val pos = when (position) {
            in 1..299 -> stringResource(id = R.string.position_0)
            in 300..599 -> stringResource(id = R.string.position_1)
            in 600..9999 -> stringResource(id = R.string.position_2)
            else -> Constants.UNKNOWN
        }
        if (pos != Constants.UNKNOWN) {
            text = "$pos($position)"
        }
    }

    Text(
        text = text,
        color = color,
        style = textStyle,
        maxLines = 1,
        textAlign = textAlign,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )
}


//攻击颜色
@Composable
fun getAtkColor(atkType: Int): Color {
    val colorId = when (atkType) {
        1 -> R.color.color_rank_7_10
        2 -> R.color.color_rank_11_17
        else -> R.color.color_rank_2_3
    }
    return colorResource(id = colorId)
}

/**
 * 带指示器图标
 */
@ExperimentalPagerApi
@Composable
fun IconHorizontalPagerIndicator(pagerState: PagerState, urls: List<String>) {
    val scope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        //显示指示器
        Row {
            urls.forEachIndexed { index, url ->
                IconCompose(
                    modifier = Modifier.padding(horizontal = Dimen.largePadding),
                    data = url,
                ) {
                    scope.launch {
                        pagerState.scrollToPage(index)
                    }
                }
            }
        }

        HorizontalPagerIndicator(
            pagerState = pagerState,
            modifier = Modifier.padding(top = Dimen.largePadding + Dimen.iconSize + Dimen.smallPadding),
            spacing = Dimen.iconSize + Dimen.largePadding + 8.dp,
            activeColor = MaterialTheme.colorScheme.primary,
            inactiveColor = Color.Unspecified
        )
    }
}

/**
 * 加载中
 */
@Composable
fun SmallCircularProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(Dimen.menuIconSize)
            .padding(Dimen.smallPadding),
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 2.dp
    )
}

/**
 * 切换
 */
@Composable
fun SelectTypeCompose(
    icon: MainIconType,
    tabs: List<String>,
    type: MutableState<Int>,
    width: Dp = Dimen.dataChangeWidth,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    modifier: Modifier = Modifier,
    changeListener: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val openDialog = navViewModel.openChangeDataDialog.observeAsState().value ?: false
    val close = navViewModel.fabCloseClick.observeAsState().value ?: false
    val mainIcon = navViewModel.fabMainIcon.observeAsState().value ?: MainIconType.BACK
    //切换关闭监听
    if (close) {
        navViewModel.openChangeDataDialog.postValue(false)
        navViewModel.fabMainIcon.postValue(MainIconType.BACK)
        navViewModel.fabCloseClick.postValue(false)
    }
    if (mainIcon == MainIconType.BACK) {
        navViewModel.openChangeDataDialog.postValue(false)
    }

    //切换
    SmallFloatingActionButton(
        modifier = modifier
            .animateContentSize(defaultSpring())
            .padding(
                end = Dimen.fabMarginEnd,
                start = Dimen.fabMargin,
                top = Dimen.fabMargin,
                bottom = Dimen.fabMargin,
            ),
        containerColor = MaterialTheme.colorScheme.background,
        shape = if (openDialog) androidx.compose.material.MaterialTheme.shapes.medium else CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        onClick = {
            VibrateUtil(context).single()
            if (!openDialog) {
                navViewModel.fabMainIcon.postValue(MainIconType.CLOSE)
                navViewModel.openChangeDataDialog.postValue(true)
            } else {
                navViewModel.fabCloseClick.postValue(true)
            }
        },
    ) {
        if (openDialog) {
            Column(
                modifier = Modifier.width(width),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                //选择
                tabs.forEachIndexed { index, tab ->
                    val mModifier = if (type.value == index) {
                        Modifier.fillMaxWidth()
                    } else {
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                VibrateUtil(context).single()
                                navViewModel.openChangeDataDialog.postValue(false)
                                navViewModel.fabCloseClick.postValue(true)
                                if (type.value != index) {
                                    coroutineScope.launch {
                                        type.value = index
                                    }
                                    changeListener?.invoke()
                                }
                            }
                    }
                    SelectText(
                        selected = type.value == index,
                        text = tab,
                        textStyle = MaterialTheme.typography.titleLarge,
                        selectedColor = selectedColor,
                        modifier = mModifier.padding(Dimen.mediumPadding)
                    )
                }
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = Dimen.largePadding)
            ) {
                IconCompose(
                    data = icon.icon,
                    tint = selectedColor,
                    size = Dimen.menuIconSize
                )
                Text(
                    text = tabs[type.value],
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Center,
                    color = selectedColor,
                    modifier = Modifier.padding(
                        start = Dimen.mediumPadding,
                        end = Dimen.largePadding
                    )
                )
            }

        }
    }
}
