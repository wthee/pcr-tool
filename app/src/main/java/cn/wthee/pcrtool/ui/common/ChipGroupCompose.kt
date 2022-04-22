package cn.wthee.pcrtool.ui.common

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ChipDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.ui.PreviewBox
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.VibrateUtil
import com.google.accompanist.flowlayout.FlowRow

/**
 * ChipGroup
 *
 * @param items chip 数据列表
 * @param selectIndex 选择位置状态
 * @param type 0：默认，1：rank选择（字体颜色改变）
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
            ChipItem(item = chipData, selectIndex, index, type)
        }
    }
}

/**
 * ChipItem
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ChipItem(item: ChipData, selectIndex: MutableState<Int>, index: Int, type: Int) {
    val context = LocalContext.current
    val isSelected = selectIndex.value == index
    //字体颜色
    val textColor = if (isSelected) {
        //选中字体颜色
        MaterialTheme.colorScheme.onPrimary
    } else {
        //未选中字体颜色
        when (type) {
            1 -> getRankColor(item.text.toInt())
            else -> Color.Unspecified
        }
    }
    //Chip 背景色
    val backgroundColor = if (isSelected) {
        //选中背景色
        when (type) {
            1 -> getRankColor(item.text.toInt())
            else -> MaterialTheme.colorScheme.primary
        }
    } else {
        //未选中背景色
        colorResource(id = if (isSystemInDarkTheme()) R.color.bg_gray_dark else R.color.bg_gray)
    }
    val chipColor = ChipDefaults.filterChipColors(backgroundColor = backgroundColor)

    FilterChip(
        selected = isSelected,
        onClick = {
            VibrateUtil(context).single()
            selectIndex.value = index
        },
        modifier = Modifier.padding(horizontal = Dimen.smallPadding),
        colors = chipColor
    ) {
        Text(
            text = item.text,
            color = textColor,
            style = MaterialTheme.typography.titleMedium
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
