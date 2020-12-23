package cn.wthee.pcrtool.data.bean

import kotlin.math.ceil

data class RankCompareData(
    val title: String,
    val attr0: Double,
    val attr1: Double,
    val attrCompare: Double
)

val Double.int: Int
    get() {
        return ceil(this).toInt()
    }


fun getRankCompareList(
    attr0: List<AttrValue>,
    attr1: List<AttrValue>,
    attr2: List<AttrValue>
): List<RankCompareData> {
    val datas = arrayListOf<RankCompareData>()
    attr0.forEachIndexed { index, attrValue ->
        datas.add(
            RankCompareData(
                attrValue.title,
                attrValue.value,
                attr1[index].value,
                attr2[index].value
            )
        )
    }
    return datas
}
