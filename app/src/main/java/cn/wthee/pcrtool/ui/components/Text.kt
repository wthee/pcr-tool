package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.utils.getFormatText


/**
 * 蓝底白字
 */
@Composable
fun MainTitleText(
    modifier: Modifier = Modifier,
    text: String,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    maxLines: Int = Int.MAX_VALUE,
    selectable: Boolean = false,
) {
    if (selectable) {
        SelectionContainer(modifier = modifier) {
            Text(
                text = text,
                color = colorWhite,
                style = textStyle,
                maxLines = maxLines,
                modifier = modifier
                    .background(color = backgroundColor, shape = MaterialTheme.shapes.extraSmall)
                    .padding(start = Dimen.mediumPadding, end = Dimen.mediumPadding)
            )
        }
    } else {
        Text(
            text = text,
            color = colorWhite,
            style = textStyle,
            maxLines = maxLines,
            modifier = modifier
                .background(color = backgroundColor, shape = MaterialTheme.shapes.extraSmall)
                .padding(start = Dimen.mediumPadding, end = Dimen.mediumPadding)
        )
    }

}

/**
 * 内容文本
 */
@Composable
fun MainContentText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.End,
    selectable: Boolean = false,
    maxLines: Int = Int.MAX_VALUE
) {
    if (selectable) {
        SelectionContainer(modifier = modifier) {
            Text(
                text = text,
                textAlign = textAlign,
                color = color,
                style = MaterialTheme.typography.bodyLarge,
                overflow = TextOverflow.Ellipsis,
                maxLines = maxLines
            )
        }
    } else {
        Text(
            text = text,
            textAlign = textAlign,
            color = color,
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier,
            overflow = TextOverflow.Ellipsis,
            maxLines = maxLines
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
    style: TextStyle = MaterialTheme.typography.titleMedium,
    selectable: Boolean = false,
) {
    if (selectable) {
        SelectionContainer(modifier = modifier) {
            Text(
                text = text,
                color = color,
                style = style,
                textAlign = textAlign,
                fontWeight = FontWeight.Black,
            )
        }
    } else {
        Text(
            text = text,
            color = color,
            style = style,
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
    color: Color = MaterialTheme.colorScheme.onSurface,
    selectable: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign = TextAlign.Start,
) {
    if (selectable) {
        SelectionContainer(modifier = modifier) {
            Text(
                text = text,
                color = color,
                textAlign = textAlign,
                style = MaterialTheme.typography.titleMedium,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
            )
        }
    } else {
        Text(
            text = text,
            color = color,
            textAlign = textAlign,
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
    color: Color = MaterialTheme.colorScheme.onSurface,
    selectable: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    textAlign: TextAlign = TextAlign.Start,
    fontWeight: FontWeight = FontWeight.Normal
) {
    if (selectable) {
        SelectionContainer(modifier = modifier) {
            Text(
                text = text,
                color = color,
                textAlign = textAlign,
                style = MaterialTheme.typography.titleSmall,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                fontWeight = fontWeight
            )
        }
    } else {
        Text(
            text = text,
            color = color,
            textAlign = textAlign,
            style = MaterialTheme.typography.titleSmall,
            modifier = modifier,
            maxLines = maxLines,
            overflow = TextOverflow.Ellipsis,
            fontWeight = fontWeight
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
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.End,
    maxLines: Int = Int.MAX_VALUE,
    style: TextStyle = MaterialTheme.typography.bodySmall,
) {
    Text(
        text = text,
        textAlign = textAlign,
        color = color,
        style = style,
        modifier = modifier,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}


/**
 * RANK 文本
 * type: 0 默认 1 白字+底色
 */
@Composable
fun RankText(
    modifier: Modifier = Modifier,
    rank: Int,
    style: TextStyle = MaterialTheme.typography.titleMedium,
    textAlign: TextAlign = TextAlign.Center,
    type: Int = 0
) {
    val color = getRankColor(rank)
    val text = getFormatText(rank)
    if (type == 0) {
        Text(
            text = text, textAlign = textAlign, color = color, style = style, modifier = modifier
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
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
    padding: Dp = Dimen.smallPadding,
    margin: Dp = Dimen.smallPadding,
    textAlign: TextAlign = TextAlign.Center
) {
    val mModifier = if (selected) {
        modifier
            .padding(top = margin)
            .background(color = selectedColor, shape = MaterialTheme.shapes.extraSmall)
            .padding(start = padding, end = padding)
    } else {
        modifier.padding(top = margin)
    }
    Text(
        text = text,
        color = if (selected) colorWhite else textColor,
        style = textStyle,
        maxLines = 1,
        textAlign = textAlign,
        overflow = TextOverflow.Ellipsis,
        modifier = mModifier
    )
}

/**
 * 头部标题
 */
@Composable
fun HeaderText(modifier: Modifier = Modifier, text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = modifier
    )
}


/**
 * 居中文本
 */
@Composable
fun CenterTipText(text: String, content: (@Composable () -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .heightIn(min = Dimen.cardHeight),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //内容
        MainText(
            text = text,
            modifier = Modifier.padding(Dimen.mediumPadding),
            selectable = true
        )
        //额外内容
        if (content != null) {
            content()
        }
    }
}

/**
 * 通用标题内容组件，用例：角色属性
 */
@Composable
fun CommonTitleContentText(title: String, content: String) {
    Row(
        modifier = Modifier.padding(
            top = Dimen.smallPadding,
            start = Dimen.commonItemPadding,
            end = Dimen.commonItemPadding
        ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MainTitleText(
            text = title,
            modifier = Modifier
                .weight(0.3f),
            maxLines = 1
        )
        MainContentText(
            text = content,
            modifier = Modifier.weight(0.2f)
        )
    }
}

/**
 * 通用分组标题
 */
@Composable
fun CommonGroupTitle(
    modifier: Modifier = Modifier,
    iconData: Any? = null,
    titleStart: String,
    titleCenter: String = "",
    titleEnd: String,
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = colorWhite,
    iconSize: Dp = Dimen.iconSize
) {
    val startPadding = if (iconData == null) {
        0.dp
    } else {
        Dimen.smallPadding
    }

    Row(
        modifier = modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        iconData?.let {
            MainIcon(
                data = iconData,
                size = iconSize
            )
        }
        Box(
            modifier = Modifier
                .padding(start = startPadding)
                .weight(1f)
                .background(
                    color = backgroundColor,
                    shape = MaterialTheme.shapes.extraSmall
                )
                .padding(horizontal = Dimen.mediumPadding)
        ) {
            Row {
                Subtitle2(
                    text = titleStart,
                    color = textColor
                )
                Spacer(modifier = Modifier.weight(1f))
                Subtitle2(
                    text = titleEnd,
                    color = textColor
                )
            }
            Subtitle2(
                text = titleCenter,
                color = textColor,
                modifier = Modifier.align(Alignment.Center)
            )
        }

    }
}
