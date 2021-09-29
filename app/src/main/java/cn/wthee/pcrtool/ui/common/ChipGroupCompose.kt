package cn.wthee.pcrtool.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.tool.getSectionTextColor
import cn.wthee.pcrtool.utils.VibrateUtil
import com.google.accompanist.flowlayout.FlowRow


/**
 * ChipGroup
 *
 * @param items chip 数据列表
 * @param selectIndex 选择位置状态
 * @param type 0：默认，1：rank选择（字体颜色改变），2：阶段选择（字体颜色改变）
 */
@Composable
fun ChipGroup(
    items: List<ChipData>,
    selectIndex: MutableState<Int>,
    modifier: Modifier = Modifier,
    type: Int = 0
) {
    FlowRow(modifier = modifier) {
        items.forEachIndexed { index, chipData ->
            ChipItem(item = chipData, selectIndex, items.size, index, type)
        }
    }
}

/**
 * ChipItem
 */
@Composable
fun ChipItem(item: ChipData, selectIndex: MutableState<Int>, size: Int, index: Int, type: Int) {
    val context = LocalContext.current
    //背景色
    val backgroundColor = if (selectIndex.value == index)
        colorResource(id = if (MaterialTheme.colors.isLight) R.color.alpha_primary else R.color.alpha_primary_dark)
    else
        colorResource(id = if (MaterialTheme.colors.isLight) R.color.bg_gray else R.color.bg_gray_dark)
    //字体颜色
    val textColor = when (type) {
        1 -> getRankColor(size - index)
        2 -> getSectionTextColor(index + 1)
        else -> {
            if (selectIndex.value == index)
                MaterialTheme.colors.background
            else
                MaterialTheme.colors.onSurface
        }
    }
    Box(
        modifier = Modifier
            .padding(Dimen.mediumPadding)
            .clip(CircleShape)
            .background(backgroundColor, CircleShape)
            .clickable {
                VibrateUtil(context).single()
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
                top = Dimen.mediumPadding,
                bottom = Dimen.mediumPadding
            )
        )
    }
}

@Preview
@Composable
private fun ChipGroupPreview() {
    val mockData = arrayListOf<ChipData>()
    val selectIndex = remember {
        mutableStateOf(3)
    }
    for (i in 0..10) {
        mockData.add(ChipData(i, "chip $i"))
    }
    PreviewBox {
        ChipGroup(items = mockData, selectIndex = selectIndex)
    }
}
