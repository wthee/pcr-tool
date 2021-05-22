package cn.wthee.pcrtool.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(4.dp),
    medium = RoundedCornerShape(6.dp),
    large = RoundedCornerShape(10.dp),
)

/**
 * 左上、右上圆角
 */
val CardTopShape = RoundedCornerShape(
    topStart = Dimen.cardRadius,
    topEnd = Dimen.cardRadius
)

val CardBottomShape = RoundedCornerShape(
    bottomStart = Dimen.cardRadius,
    bottomEnd = Dimen.cardRadius
)