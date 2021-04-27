package cn.wthee.pcrtool.ui.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.Shapes
import cn.wthee.pcrtool.ui.theme.efabShape
import com.google.accompanist.flowlayout.FlowRow

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
 * 通用悬浮按钮
 */
@Composable
fun FabCompose(@DrawableRes iconId: Int, modifier: Modifier = Modifier, onClick: () -> Unit) {

    FloatingActionButton(
        onClick = onClick,
        backgroundColor = MaterialTheme.colors.onPrimary,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = Dimen.fabElevation),
        contentColor = MaterialTheme.colors.primary,
        modifier = modifier.size(Dimen.fabSize),
    ) {
        val icon =
            painterResource(iconId)
        Icon(icon, "", modifier = Modifier.padding(Dimen.fabPadding))
    }
}

/**
 * 通用展开悬浮按钮
 */
@Composable
fun ExtendedFabCompose(
    modifier: Modifier = Modifier,
    @DrawableRes iconId: Int,
    text: String,
    textWidth: Dp = Dimen.getWordWidth(2),
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        icon = { Icon(painterResource(iconId), null, modifier = Modifier.size(Dimen.fabIconSize)) },
        text = {
            Text(
                text = text,
                style = MaterialTheme.typography.subtitle2,
                modifier = Modifier.requiredWidth(textWidth)
            )
        },
        onClick = onClick,
        elevation = FloatingActionButtonDefaults.elevation(Dimen.fabElevation),
        backgroundColor = MaterialTheme.colors.onPrimary,
        contentColor = MaterialTheme.colors.primary,
        modifier = modifier.height(Dimen.fabSize),
    )
}

/**
 * chip 选择组
 */
@Composable
fun ChipGroup(
    items: List<ChipData>,
    selectIndex: MutableState<Int>,
    modifier: Modifier = Modifier
) {
    FlowRow(modifier = modifier) {
        items.forEachIndexed { index, chipData ->
            ChipItem(item = chipData, selectIndex, index)
        }
    }
}

/**
 * chip 选择组
 */
@Composable
fun ChipItem(item: ChipData, selectIndex: MutableState<Int>, index: Int) {
    //背景色
    val backgroundColor = if (selectIndex.value == index)
        colorResource(id = R.color.alpha_primary)
    else
        MaterialTheme.colors.background

    //字体颜色
    val textColor = if (selectIndex.value == index)
        MaterialTheme.colors.primary
    else
        MaterialTheme.colors.onBackground
    Box(
        modifier = Modifier
            .padding(Dimen.mediuPadding)
            .clip(efabShape)
            .background(backgroundColor, efabShape)
            .clickable {
                selectIndex.value = index
            }
    ) {
        Text(
            text = item.text,
            color = textColor,
            style = MaterialTheme.typography.subtitle1,
            modifier = Modifier.padding(
                start = Dimen.largePadding,
                end = Dimen.largePadding,
                top = Dimen.mediuPadding,
                bottom = Dimen.mediuPadding
            )
        )
    }
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
fun SubButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = Shapes.large,
        modifier = modifier.padding(Dimen.smallPadding)
    ) {
        Text(text = text, style = MaterialTheme.typography.button)
    }
}