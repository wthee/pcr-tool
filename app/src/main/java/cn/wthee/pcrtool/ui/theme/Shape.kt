package cn.wthee.pcrtool.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable


/**
 * 左上、右上圆角
 */
@Composable
fun shapeTop() = RoundedCornerShape(
    topStart = MaterialTheme.shapes.medium.topStart,
    topEnd = MaterialTheme.shapes.medium.topEnd,
    bottomStart = CornerSize(0),
    bottomEnd = CornerSize(0)
)

/**
 * 比例
 */
const val RATIO_GOLDEN = 0.618f

/**
 * dialog 高度
 */
const val WEIGHT_DIALOG = 0.85f

/**
 * 梯形
 */
val TrapezoidShape = GenericShape { size, _ ->
    moveTo(size.width * (1 - RATIO_GOLDEN), 0f)
    lineTo(0f, size.height)
    lineTo(size.width, size.height)
    lineTo(size.width, 0f)
}