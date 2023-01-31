package cn.wthee.pcrtool.ui.common

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.dp2px
import kotlin.math.max

/**
 * 网格布局
 */
//@Composable
//fun VerticalGrid(
//    modifier: Modifier = Modifier,
//    maxColumnWidth: Dp,
//    children: @Composable () -> Unit
//) {
//    Layout(
//        content = children,
//        modifier = modifier
//    ) { measurables, constraints ->
//        check(constraints.hasBoundedWidth) {
//            "Unbounded width not supported"
//        }
//        val columns = max(1, ceil(constraints.maxWidth / maxColumnWidth.toPx()).toInt())
//        val columnWidth = constraints.maxWidth / columns
//        val itemConstraints = constraints.copy(maxWidth = columnWidth)
//        val colHeights = IntArray(columns) { 0 } // track each column's height
//        val placeables = measurables.map { measurable ->
//            val column = shortestColumn(colHeights)
//            val placeable = measurable.measure(itemConstraints)
//            colHeights[column] += placeable.height
//            placeable
//        }
//
//        val height = colHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
//            ?: constraints.minHeight
//        layout(
//            width = constraints.maxWidth,
//            height = height
//        ) {
//            val colY = IntArray(columns) { 0 }
//            placeables.forEach { placeable ->
//                val column = shortestColumn(colY)
//                placeable.place(
//                    x = columnWidth * column,
//                    y = colY[column]
//                )
//                colY[column] += placeable.height
//            }
//        }
//    }
//}

/**
 * 网格布局
 * @param itemWidth 子项宽度
 * @param fixCount 固定列数
 * @param contentPadding 子项间距
 */
@Composable
fun VerticalGrid(
    modifier: Modifier = Modifier,
    itemWidth: Dp? = null,
    fixCount: Int = 0,
    contentPadding: Dp = 0.dp,
    children: @Composable () -> Unit
) {
    var size by remember { mutableStateOf(IntSize(width = ScreenUtil.getWidth(), height = 0)) }
    val mSpanCount = if (fixCount == 0) {
        size.width / (itemWidth!! + contentPadding).value.dp2px
    } else {
        fixCount
    }

    Layout(
        content = children,
        modifier = modifier.onSizeChanged {
            size = it
        }
    ) { measurables, constraints ->
        check(constraints.hasBoundedWidth) {
            "Unbounded width not supported"
        }
        val columnWidth = (constraints.maxWidth / max(1, mSpanCount).toFloat()).toInt()
        val itemConstraints = constraints.copy(maxWidth = columnWidth)
        val colHeights = IntArray(max(1, mSpanCount)) { 0 } // track each column's height
        val placeables = measurables.map { measurable ->
            val column = shortestColumn(colHeights)
            val placeable = measurable.measure(itemConstraints)
            colHeights[column] += placeable.height
            placeable
        }

        val height = colHeights.maxOrNull()?.coerceIn(constraints.minHeight, constraints.maxHeight)
            ?: constraints.minHeight
        layout(
            width = constraints.maxWidth,
            height = height
        ) {
            val colY = IntArray(max(1, mSpanCount)) { 0 }
            placeables.forEach { placeable ->
                val column = shortestColumn(colY)
                placeable.place(
                    x = columnWidth * column,
                    y = colY[column]
                )
                colY[column] += placeable.height
            }
        }
    }
}

private fun shortestColumn(colHeights: IntArray): Int {
    var minHeight = Int.MAX_VALUE
    var column = 0
    colHeights.forEachIndexed { index, height ->
        if (height < minHeight) {
            minHeight = height
            column = index
        }
    }
    return column
}


/**
 * 获取宽度
 */
fun getItemWidth(floatWindow: Boolean = false) = if (floatWindow) {
    Dimen.mediumIconSize + Dimen.mediumPadding * 2
} else {
    Dimen.iconSize + Dimen.mediumPadding * 2
} * 5