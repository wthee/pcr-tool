package cn.wthee.pcrtool.ui.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ElevatedSuggestionChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import cn.wthee.pcrtool.data.model.ChipData
import cn.wthee.pcrtool.data.model.KeywordData
import cn.wthee.pcrtool.ui.theme.CombinedPreviews
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.ui.theme.PreviewLayout
import cn.wthee.pcrtool.ui.theme.colorPurple
import cn.wthee.pcrtool.ui.theme.colorWhite
import cn.wthee.pcrtool.utils.VibrateUtil

/**
 * ChipGroup
 *
 * @param items 文本列表
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
            MainChip(
                index = index,
                selected = selectIndex.value == index,
                selectIndex = selectIndex,
                text = chipData.text,
                selectedColor = chipData.color,
                modifier = Modifier.padding(horizontal = Dimen.smallPadding)
            )
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
fun MainChip(
    modifier: Modifier = Modifier,
    index: Int,
    selected: Boolean,
    selectIndex: MutableState<Int>,
    text: String,
    selectedColor: Color?
) {
    val context = LocalContext.current

    ElevatedFilterChip(
        selected = selected,
        onClick = {
            VibrateUtil(context).single()
            selectIndex.value = index
        },
        modifier = modifier,
        colors = FilterChipDefaults.elevatedFilterChipColors(
            containerColor = MaterialTheme.colorScheme.surface,
            selectedContainerColor = selectedColor ?: MaterialTheme.colorScheme.primary
        ),
        label = {
            CaptionText(
                text = text,
                color = if (selected) {
                    //选中字体颜色
                    colorWhite
                } else {
                    //未选中字体颜色
                    selectedColor ?: MaterialTheme.colorScheme.onSurface
                }
            )
        },
//        border = FilterChipDefaults.filterChipBorder(
//            borderColor = selectedColor ?: MaterialTheme.colorScheme.onSurface
//        )
    )
}

@CombinedPreviews
@Composable
private fun ChipGroupPreview() {
    val mockData = arrayListOf<ChipData>()
    val selectIndex = remember {
        mutableIntStateOf(3)
    }
    mockData.add(ChipData("chip"))
    for (i in 0..10) {
        mockData.add(ChipData("chip $i", colorPurple))
    }
    PreviewLayout {
        ChipGroup(items = mockData, selectIndex = selectIndex)
    }
}
