package cn.wthee.pcrtool.ui.theme

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable


//val Shapes = androidx.compose.material3.Shapes(
//    extraSmall = RoundedCornerShape(0.dp),
//    small = RoundedCornerShape(4.dp),
//    medium = RoundedCornerShape(8.dp),
//    large = RoundedCornerShape(12.dp)
//)

/**
 * 左上、右上圆角
 */
@Composable
fun ShapeTop() = RoundedCornerShape(
    topStart = MaterialTheme.shapes.medium.topStart,
    topEnd = MaterialTheme.shapes.medium.topEnd,
    bottomStart = CornerSize(0),
    bottomEnd = CornerSize(0)
)


/**
 * 梯形
 */
const val RATIO_SHAPE = 0.382f
val TrapezoidShape = GenericShape { size, _ ->
    moveTo(size.width * RATIO_SHAPE, 0f)
    lineTo(0f, size.height)
    lineTo(size.width, size.height)
    lineTo(size.width, 0f)
}