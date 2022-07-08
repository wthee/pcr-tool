package cn.wthee.pcrtool.ui.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
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
 * ChipItem
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChipItem(item: ChipData, selectIndex: MutableState<Int>, index: Int) {
    val context = LocalContext.current
    val isSelected = selectIndex.value == index
    //字体颜色
    val textColor = if (isSelected) {
        //选中字体颜色
        MaterialTheme.colorScheme.onPrimary
    } else {
        //未选中字体颜色
        Color.Unspecified
    }
    //Chip 背景色
    val containerColor = if (isSelected) {
        //选中背景色
        MaterialTheme.colorScheme.primary
    } else {
        //未选中背景色
        Color.Transparent
    }
    val chipColor = FilterChipDefaults.filterChipColors(selectedContainerColor = containerColor)

    FilterChip(
        selected = isSelected,
        onClick = {
            VibrateUtil(context).single()
            selectIndex.value = index
        },
        modifier = Modifier.padding(horizontal = Dimen.smallPadding),
        colors = chipColor,
        label = {
            Text(
                text = item.text,
                color = textColor,
                style = MaterialTheme.typography.titleMedium
            )
        }
    )
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
