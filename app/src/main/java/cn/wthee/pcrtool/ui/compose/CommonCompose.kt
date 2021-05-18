package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.getFormatText
import cn.wthee.pcrtool.utils.vibrate
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.PagerState

/**
 * 蓝底白字
 */
@Composable
fun MainTitleText(
    modifier: Modifier = Modifier,
    text: String,
    small: Boolean = false,
    backgroundColor: Color = MaterialTheme.colors.primary,
) {
    SelectionContainer(modifier = modifier) {
        Text(
            text = text,
            color = MaterialTheme.colors.onPrimary,
            style = if (small) MaterialTheme.typography.caption else MaterialTheme.typography.body2,
            modifier = Modifier
                .background(color = backgroundColor, shape = Shapes.small)
                .padding(start = Dimen.mediuPadding, end = Dimen.mediuPadding)
        )
    }
}

/**
 * 蓝底白字
 */
@Composable
fun MainContentText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.End
) {
    SelectionContainer(modifier = modifier) {
        Text(
            text = text,
            textAlign = textAlign,
            color = color,
            style = MaterialTheme.typography.body1,
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
    color: Color = MaterialTheme.colors.primary
) {
    SelectionContainer(
        modifier = modifier.padding(
            start = Dimen.mediuPadding,
            end = Dimen.mediuPadding
        )
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Black,
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
    color: Color = Color.Unspecified
) {
    SelectionContainer(modifier = modifier) {
        Text(
            text = text,
            color = color,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.subtitle1,
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
    color: Color = Color.Unspecified
) {
    SelectionContainer(modifier = modifier) {
        Text(
            text = text,
            color = color,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.subtitle2,
        )
    }
}

/**
 * 灰色标注字体
 */
@Composable
fun CaptionText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.End
) {
    SelectionContainer(modifier = modifier) {
        Text(
            text = text,
            textAlign = textAlign,
            style = MaterialTheme.typography.caption,
        )
    }

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
 * 主操作按钮
 */
@Composable
fun MainButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val context = LocalContext.current
    Button(
        shape = Shapes.large,
        modifier = modifier.padding(Dimen.smallPadding),
        onClick = onClick.vibrate {
            VibrateUtil(context).single()
        }
    ) {
        Text(text = text, style = MaterialTheme.typography.button)
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
    onClick: () -> Unit
) {
    val context = LocalContext.current

    OutlinedButton(
        shape = Shapes.large,
        modifier = modifier.padding(Dimen.smallPadding),
        onClick = onClick.vibrate {
            VibrateUtil(context).single()
        }
    ) {
        Text(text = text, color = color, style = MaterialTheme.typography.button)
    }
}


/**
 * 转化成 gridlist 数据
 */
@Composable
fun <T> getGridData(spanCount: Int, list: List<T>, placeholder: T): ArrayList<T> {
    //不足 spanCount 的倍数，填充空的占位
    val newList = arrayListOf<T>()
    newList.addAll(list)
    if (newList.size % spanCount != 0) {
        val rowNum = newList.size / spanCount + 1
        for (i in newList.size until rowNum * spanCount) {
            newList.add(placeholder)
        }
    }
    return newList
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
        in 21..99 -> R.color.color_rank_21
        else -> {
            R.color.color_rank_2_3
        }
    }
    return colorResource(id = colorId)
}

@ExperimentalPagerApi
@Composable
fun PagerIndicator(pagerState: PagerState, modifier: Modifier) {
    HorizontalPagerIndicator(
        pagerState = pagerState,
        activeColor = MaterialTheme.colors.primary,
        inactiveColor = colorResource(id = R.color.alpha_primary),
        modifier = modifier
            .padding(bottom = Dimen.sheetMarginBottom + Dimen.largePadding)
    )
}

@Composable
fun CommonSpacer() {
    Spacer(modifier = Modifier
        .navigationBarsPadding()
        .height(Dimen.fabSize + Dimen.fabMargin))
}

/**
 * 卡片布局
 */
@Composable
fun MainCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    var mModifier = modifier
        .fillMaxWidth()
        .heightIn(min = Dimen.cardHeight)
        .shadow(elevation = Dimen.cardElevation, shape = Shapes.large, clip = true)
    if (onClick != null) {
        mModifier = mModifier.clickable(onClick = onClick.vibrate {
            VibrateUtil(context).single()
        })
    }

    Card(
        modifier = mModifier,
        content = content
    )
}