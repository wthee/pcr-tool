package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.utils.getFormatText
import cn.wthee.pcrtool.utils.getRankColor

/**
 * 蓝底白字
 */
@Composable
fun MainTitleText(text: String, small: Boolean = false, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.onPrimary,
        style = if (small) MaterialTheme.typography.caption else MaterialTheme.typography.body2,
        modifier = modifier
            .background(color = MaterialTheme.colors.primary, shape = Shapes.small)
            .padding(start = Dimen.mediuPadding, end = Dimen.mediuPadding)
    )
}

/**
 * 蓝底白字
 */
@Composable
fun MainContentText(
    text: String,
    modifier: Modifier = Modifier,
    textAlign: TextAlign = TextAlign.End
) {
    Text(
        text = text,
        color = MaterialTheme.colors.onBackground,
        textAlign = textAlign,
        style = MaterialTheme.typography.body2,
        modifier = modifier
    )
}

/**
 * 蓝色加粗标题
 */
@Composable
fun MainText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.subtitle1,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(start = Dimen.mediuPadding, end = Dimen.mediuPadding)
    )
}

/**
 * 蓝色加粗副标题
 */
@Composable
fun MainSubText(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.subtitle2,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(start = Dimen.mediuPadding, end = Dimen.mediuPadding)
    )
}

/**
 * 蓝色加粗标题
 */
@Composable
fun SpaceCompose(modifier: Modifier) {
    Spacer(
        modifier = modifier
            .padding(Dimen.smallPadding)
            .width(Dimen.lineWidth)
            .height(Dimen.lineHeight)
            .background(MaterialTheme.colors.primary)
    )
}

/**
 * 主操作按钮
 */
@Composable
fun MainButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = Shapes.large,
        modifier = modifier.padding(Dimen.smallPadding)
    ) {
        Text(text = text, style = MaterialTheme.typography.button)
    }
}

/**
 * 次操作按钮
 */
@Composable
fun SubButton(
    text: String,
    color: Color = Color.Unspecified,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        shape = Shapes.large,
        modifier = modifier.padding(Dimen.smallPadding)
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
        color = colorResource(id = getRankColor(rank)),
        style = style,
        modifier = modifier
    )
}