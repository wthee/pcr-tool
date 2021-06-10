package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextOverflow
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.getFormatText
import cn.wthee.pcrtool.utils.vibrate
import com.google.accompanist.insets.navigationBarsPadding

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
    textAlign: TextAlign = TextAlign.Center,
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
            textAlign = textAlign,
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
    modifier: Modifier = Modifier,
    text: String,
    color: Color = Color.Unspecified,
    textAlign: TextAlign = TextAlign.End
) {
    Text(
        text = text,
        textAlign = textAlign,
        color = color,
        style = MaterialTheme.typography.caption,
        modifier = modifier
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

@Composable
fun CommonSpacer() {
    Spacer(
        modifier = Modifier
            .navigationBarsPadding()
            .height(Dimen.fabSize + Dimen.fabMargin)
    )
}

@Composable
fun CommonIconSpacer() {
    Spacer(
        modifier = Modifier
            .navigationBarsPadding()
            .size(Dimen.iconSize)
    )
}

/**
 * 卡片布局
 */
@ExperimentalMaterialApi
@Composable
fun MainCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.background,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val mModifier = modifier
        .fillMaxWidth()
        .heightIn(min = Dimen.cardHeight)
        .shadow(elevation = Dimen.cardElevation, shape = Shapes.large, clip = true)

    if (onClick != null) {
        Card(
            modifier = mModifier,
            content = content,
            onClick = onClick.vibrate {
                VibrateUtil(context).single()
            },
            backgroundColor = backgroundColor
        )
    } else {
        Card(
            modifier = mModifier,
            content = content,
            backgroundColor = backgroundColor
        )
    }

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
    selectedColor: Color = MaterialTheme.colors.primary,
    textColor: Color = Color.Unspecified,
    style: TextStyle = MaterialTheme.typography.body2
) {
    val mModifier = if (selected) {
        modifier
            .padding(top = Dimen.smallPadding)
            .background(color = selectedColor, shape = Shapes.small)
            .padding(start = Dimen.smallPadding, end = Dimen.smallPadding)
    } else {
        modifier.padding(top = Dimen.smallPadding)
    }
    Text(
        text = text,
        color = if (selected) MaterialTheme.colors.onPrimary else textColor,
        style = style,
        maxLines = 1,
        textAlign = TextAlign.Center,
        overflow = TextOverflow.Ellipsis,
        modifier = mModifier
    )
}