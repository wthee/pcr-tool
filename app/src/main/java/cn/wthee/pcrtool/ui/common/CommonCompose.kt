package cn.wthee.pcrtool.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import cn.wthee.pcrtool.utils.VibrateUtil
import cn.wthee.pcrtool.utils.getFormatText
import com.google.accompanist.insets.navigationBarsPadding

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
 * 蓝底白字
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
            modifier = modifier
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
    selectable: Boolean = false
) {
    if (selectable) {
        SelectionContainer(
            modifier = modifier.padding(
                start = Dimen.mediumPadding,
                end = Dimen.mediumPadding
            )
        ) {
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
    selectable: Boolean = false
) {
    if (selectable) {
        SelectionContainer(modifier = modifier) {
            Text(
                text = text,
                color = color,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleMedium,
            )
        }
    } else {
        Text(
            text = text,
            color = color,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleMedium,
            modifier = modifier
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
    selectable: Boolean = false
) {
    if (selectable) {
        SelectionContainer(modifier = modifier) {
            Text(
                text = text,
                color = color,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    } else {
        Text(
            text = text,
            color = color,
            textAlign = TextAlign.Start,
            style = MaterialTheme.typography.titleSmall,
            modifier = modifier
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
        style = MaterialTheme.typography.bodySmall,
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
            style = MaterialTheme.typography.labelLarge
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
    onClick: () -> Unit
) {
    val context = LocalContext.current

    OutlinedButton(
        shape = Shape.medium,
        modifier = modifier.padding(Dimen.smallPadding),
        onClick = {
            VibrateUtil(context).single()
            onClick.invoke()
        }
    ) {
        Text(text = text, color = color, style = MaterialTheme.typography.labelLarge)
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
@ExperimentalMaterialApi
@Composable
fun MainCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val mModifier = modifier
        .fillMaxWidth()
        .heightIn(min = Dimen.cardHeight)

    if (onClick != null) {
        Card(
            modifier = mModifier,
            content = content,
            onClick = {
                VibrateUtil(context).single()
                onClick.invoke()
            },
            backgroundColor = backgroundColor,
            elevation = Dimen.cardElevation,
            shape = Shape.medium,
        )
    } else {
        Card(
            modifier = mModifier,
            content = content,
            backgroundColor = backgroundColor,
            elevation = Dimen.cardElevation,
            shape = Shape.medium,
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
    characterInfo: CharacterInfo,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    val color: Color
    val type: String
    when (characterInfo.isLimited) {
        1 -> {
            if (characterInfo.rarity == 1) {
                type = stringResource(id = R.string.type_event_limit)
                color = colorResource(id = R.color.color_rank_21)
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
    MainTitleText(modifier = modifier, text = type, backgroundColor = color, textStyle = textStyle)

}


/**
 * 角色站位文本样式
 */
@Composable
fun CharacterPositionText(
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    position: Int,
    padding: Dp = 0.dp,
    colorful: Boolean = true,
    textAlign: TextAlign = TextAlign.Center,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    var color = colorResource(
        id = when (position) {
            in 1..299 -> R.color.color_rank_18_20
            in 300..599 -> R.color.color_rank_7_10
            in 600..9999 -> R.color.colorPrimary
            else -> R.color.black
        }
    )
    color = if (colorful) color else Color.Unspecified

    Text(
        text = position.toString(),
        color = if (selected) Color.Unspecified else color,
        style = textStyle,
        maxLines = 1,
        textAlign = textAlign,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier.padding(top = padding)
    )
}
