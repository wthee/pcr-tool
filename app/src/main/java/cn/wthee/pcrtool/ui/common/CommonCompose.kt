package cn.wthee.pcrtool.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shape
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
 */
@Composable
fun RankText(
    rank: Int,
    style: TextStyle,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.Center,
) {
    Text(
        text = getFormatText(rank),
        textAlign = textAlign,
        color = getRankColor(rank),
        style = style,
        modifier = modifier
    )
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
            .height(Dimen.fabSize + Dimen.fabMargin)
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

    val mModifier = modifier
        .fillMaxWidth()
        .shadow(Dimen.cardElevation, shape, true)
        .clickable(enabled = onClick != null) {
            VibrateUtil(context).single()
            onClick?.invoke()
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
            urls.forEachIndexed { index, s ->
                IconCompose(
                    modifier = Modifier.padding(horizontal = Dimen.largePadding),
                    data = s,
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