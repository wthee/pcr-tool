package cn.wthee.pcrtool.data.model.entity

import androidx.room.ColumnInfo
import com.google.gson.JsonArray
import java.io.Serializable

data class PvpCharacterData(
    @ColumnInfo(name = "unit_id") val unitId: Int,
    @ColumnInfo(name = "position") val position: Int
) : Serializable {
    fun getFixedId() = unitId + 30
}

fun getDefault() = arrayListOf(
    PvpCharacterData(0, 999),
    PvpCharacterData(0, 999),
    PvpCharacterData(0, 999),
    PvpCharacterData(0, 999),
    PvpCharacterData(0, 999),
)

fun ArrayList<PvpCharacterData>.getIds(): JsonArray {
    val ids = JsonArray()
    for (character in this) {
        ids.add(character.unitId)
    }
    return ids
}
