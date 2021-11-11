package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo

/**
 *  角色位置
 */
data class PvpCharacterData(
    @ColumnInfo(name = "unit_id") val unitId: Int = 0,
    @ColumnInfo(name = "position") val position: Int = 999,
    @ColumnInfo(name = "type") val type: Int = -1
)

/**
 * 用 - 拼接角色id
 */
fun List<PvpCharacterData>.getIdStr(): String {
    var ids = ""
    for (character in this) {
        ids += "${character.unitId}-"
    }
    return ids
}
