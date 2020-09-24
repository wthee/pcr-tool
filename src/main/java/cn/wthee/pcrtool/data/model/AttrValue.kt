package cn.wthee.pcrtool.data.model

import kotlin.math.ceil

data class AttrValue(
    val title: String,
    val value: Double
) {
    fun getIntValue() = ceil(value).toInt()
}