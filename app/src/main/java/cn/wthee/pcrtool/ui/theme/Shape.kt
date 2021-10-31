package cn.wthee.pcrtool.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp


object Shape {
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