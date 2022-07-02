package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.Ignore

/**
 *  角色位置
 */
data class PvpCharacterData(
    @ColumnInfo(name = "unit_id") var unitId: Int = 0,
    @ColumnInfo(name = "position") var position: Int = 999,
    @ColumnInfo(name = "type") var type: Int = -1,
    @Ignore var count: Int = 0
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
