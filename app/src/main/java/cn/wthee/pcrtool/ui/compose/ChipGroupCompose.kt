package cn.wthee.pcrtool.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.colorResource
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.ui.theme.Dimen
import com.google.accompanist.flowlayout.FlowRow


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
            .clip(CircleShape)
            .background(backgroundColor, CircleShape)
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
