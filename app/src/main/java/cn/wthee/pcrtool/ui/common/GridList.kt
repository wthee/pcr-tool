package cn.wthee.pcrtool.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import cn.wthee.pcrtool.ui.theme.Dimen
import cn.wthee.pcrtool.utils.dp2px
import cn.wthee.pcrtool.utils.spanCount
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
 * @param isSubLayout VerticalGrid 嵌套时，内部VerticalGrid 传 true ，将计算父级 spanCount
 */
@Composable
fun VerticalGrid(
    modifier: Modifier = Modifier,
    itemWidth: Dp? = null,
    fixCount: Int = 0,
    contentPadding: Dp = 0.dp,
    isSubLayout: Boolean = false,
    children: @Composable () -> Unit
) {
    val appWidth = LocalView.current.width
    var size by remember { mutableStateOf(IntSize(width = appWidth, height = 0)) }
    val mSpanCount = if (fixCount == 0) {
        if (isSubLayout) {
            val parentSpanCount = getItemWidth().spanCount
            ((appWidth - (Dimen.largePadding * 2).value.toInt()) / parentSpanCount / (itemWidth!! + contentPadding).value.dp2px)
        } else {
            (size.width / (itemWidth!! + contentPadding).value.dp2px)
        }
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