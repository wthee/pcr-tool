package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import com.google.gson.JsonArray
import java.io.Serializable

/**
 *  角色位置
 */
data class PvpCharacterData(
    @ColumnInfo(name = "unit_id") val unitId: Int,
    @ColumnInfo(name = "position") val position: Int
) : Serializable


/**
 * 默认数据
 */
fun getDefault() = arrayListOf(
    PvpCharacterData(0, 999),
    PvpCharacterData(0, 999),
    PvpCharacterData(0, 999),
    PvpCharacterData(0, 999),
    PvpCharacterData(0, 999),
)

/**
 * 获取角色id [JsonArray] 数据
 */
fun ArrayList<PvpCharacterData>.getIds(): JsonArray {
    val ids = JsonArray()
    for (character in this) {
        ids.add(character.unitId)
    }
    return ids
}

/**
 * 用 - 拼接角色id
 */
fun ArrayList<PvpCharacterData>.getIdStr(): String {
    var ids = ""
    for (character in this) {
        ids += "${character.unitId}-"
    }
    return ids
}
