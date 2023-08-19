package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.KeywordData
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.utils.VibrateUtil

/**
 * ChipGroup
 *
 * @param items chip 数据列表
 * @param selectIndex 选择位置状态
 */
@OptIn(ExperimentalLayoutApi::class)
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
 * SuggestionChipGroup 展示用
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuggestionChipGroup(
    items: List<KeywordData>,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    val context = LocalContext.current

    FlowRow(modifier = modifier) {
        items.forEach {
            ElevatedSuggestionChip(
                onClick = {
                    VibrateUtil(context).single()
                    onClick(it.keyword)
                },
                modifier = Modifier.padding(horizontal = Dimen.smallPadding),
                label = {
                    Subtitle2(
                        text = "${it.desc}：${it.keyword}"
                    )
                }
            )
        }
    }
}

/**
 * 可选中的 ChipItem
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipItem(item: ChipData, selectIndex: MutableState<Int>, index: Int) {
    val context = LocalContext.current
    val isSelected = selectIndex.value == index
    //字体颜色
    val textColor = if (isSelected) {
        //选中字体颜色
        colorWhite
    } else {
        //未选中字体颜色
        MaterialTheme.colorScheme.onSurface
    }


    ElevatedFilterChip(
        selected = isSelected,
        onClick = {
            VibrateUtil(context).single()
            selectIndex.value = index
        },
        modifier = Modifier.padding(horizontal = Dimen.smallPadding),
        colors = FilterChipDefaults.elevatedFilterChipColors(
            containerColor = MaterialTheme.colorScheme.background,
            selectedContainerColor = MaterialTheme.colorScheme.primary
        ),
        label = {
            CaptionText(
                text = item.text,
                color = textColor
            )
        }
    )
}


@CombinedPreviews
@Composable
private fun ChipGroupPreview() {
    val mockData = arrayListOf<ChipData>()
    val selectIndex = remember {
        mutableStateOf(3)
    }
    for (i in 0..10) {
        mockData.add(ChipData(i, "chip $i"))
    }
    PreviewLayout {
        ChipGroup(items = mockData, selectIndex = selectIndex)
    }
}
