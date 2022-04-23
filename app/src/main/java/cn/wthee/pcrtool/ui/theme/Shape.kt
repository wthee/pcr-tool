package cn.wthee.pcrtool.ui.theme

import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp


object Shape {
    val none = RoundedCornerShape(0.dp)
    val small = RoundedCornerShape(4.dp)
    val medium = RoundedCornerShape(8.dp)
    val large = RoundedCornerShape(12.dp)
}

/**
 * 左上、右上圆角
 */
val CardTopShape = RoundedCornerShape(
    topStart = Dimen.cardRadius,
    topEnd = Dimen.cardRadius
)

val noShape = RoundedCornerShape(0)


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