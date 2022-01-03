package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo

/**
 * 角色剧情属性视图
 */
data class CharacterStoryAttr(
    @ColumnInfo(name = "story_id") val storyId: Int,
    @ColumnInfo(name = "unlock_story_name") val storyName: String,
    @ColumnInfo(name = "status_type_1") val status_type_1: Int,
    @ColumnInfo(name = "status_rate_1") val status_rate_1: Int,
    @ColumnInfo(name = "status_type_2") val status_type_2: Int,
    @ColumnInfo(name = "status_rate_2") val status_rate_2: Int,
    @ColumnInfo(name = "status_type_3") val status_type_3: Int,
    @ColumnInfo(name = "status_rate_3") val status_rate_3: Int,
    @ColumnInfo(name = "status_type_4") val status_type_4: Int,
    @ColumnInfo(name = "status_rate_4") val status_rate_4: Int,
    @ColumnInfo(name = "status_type_5") val status_type_5: Int,
    @ColumnInfo(name = "status_rate_5") val status_rate_5: Int,
)

fun CharacterStoryAttr.getAttr(): Attr {
    val attrTypes = arrayListOf(
        status_type_1, status_type_2, status_type_3, status_type_4, status_type_5,
    )
    val attrs = arrayListOf(
        status_rate_1, status_rate_2, status_rate_3, status_rate_4, status_rate_5
    )
    val allAttrInfo = Attr()
    attrTypes.forEachIndexed { index, type ->
        val attrInfo = Attr()
        when (type) {
            1 -> attrInfo.hp = attrs[index].toDouble()
            2 -> attrInfo.atk = attrs[index].toDouble()
            3 -> attrInfo.def = attrs[index].toDouble()
            4 -> attrInfo.magicStr = attrs[index].toDouble()
            5 -> attrInfo.magicDef = attrs[index].toDouble()
            6 -> attrInfo.physicalCritical = attrs[index].toDouble()
            7 -> attrInfo.magicCritical = attrs[index].toDouble()
            8 -> attrInfo.dodge = attrs[index].toDouble()
            9 -> attrInfo.lifeSteal = attrs[index].toDouble()
            10 -> attrInfo.waveHpRecovery = attrs[index].toDouble()
            11 -> attrInfo.waveEnergyRecovery = attrs[index].toDouble()
//                    12 -> attrInfo.hp = attrs[index].toDouble()
//                    13 -> attrInfo.hp = attrs[index].toDouble()
            14 -> attrInfo.energyRecoveryRate = attrs[index].toDouble()
            15 -> attrInfo.hpRecoveryRate = attrs[index].toDouble()
//                    16 -> attrInfo.hp = attrs[index].toDouble()
            17 -> attrInfo.accuracy = attrs[index].toDouble()
        }
        allAttrInfo.add(attrInfo)
    }
    return allAttrInfo
}
