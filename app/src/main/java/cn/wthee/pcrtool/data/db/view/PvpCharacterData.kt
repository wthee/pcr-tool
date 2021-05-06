package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import com.google.gson.JsonArray
import java.io.Serializable

/**
 *  角色位置
 */
data class PvpCharacterData(
    @ColumnInfo(name = "unit_id") val unitId: Int = 0,
    @ColumnInfo(name = "position") val position: Int = 999
) : Serializable


/**
 * 默认数据
 */
fun getDefaultPvpCharacterList() = arrayListOf(
    PvpCharacterData(),
    PvpCharacterData(),
    PvpCharacterData(),
    PvpCharacterData(),
    PvpCharacterData(),
)

/**
 * 获取角色id [JsonArray] 数据
 */
fun List<PvpCharacterData>.getIds(): JsonArray {
    val ids = JsonArray()
    for (character in this) {
        ids.add(character.unitId)
    }
    return ids
}

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
