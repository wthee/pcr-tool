package cn.wthee.pcrtool.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

//角色属性状态
@Entity(tableName = "guild")
data class GuildData(
    @PrimaryKey
    @ColumnInfo(name = "guild_id") val guild_id: Int,
    @ColumnInfo(name = "guild_name") val guild_name: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "guild_master") val guild_master: Int,
    @ColumnInfo(name = "member1") val member1: Int,
    @ColumnInfo(name = "member2") val member2: Int,
    @ColumnInfo(name = "member3") val member3: Int,
    @ColumnInfo(name = "member4") val member4: Int,
    @ColumnInfo(name = "member5") val member5: Int,
    @ColumnInfo(name = "member6") val member6: Int,
    @ColumnInfo(name = "member7") val member7: Int,
    @ColumnInfo(name = "member8") val member8: Int,
    @ColumnInfo(name = "member9") val member9: Int,
    @ColumnInfo(name = "member10") val member10: Int,
    @ColumnInfo(name = "member11") val member11: Int,
    @ColumnInfo(name = "member12") val member12: Int,
    @ColumnInfo(name = "member13") val member13: Int,
    @ColumnInfo(name = "member14") val member14: Int,
    @ColumnInfo(name = "member15") val member15: Int,
    @ColumnInfo(name = "member16") val member16: Int,
    @ColumnInfo(name = "member17") val member17: Int,
    @ColumnInfo(name = "member18") val member18: Int,
    @ColumnInfo(name = "member19") val member19: Int,
    @ColumnInfo(name = "member20") val member20: Int,
    @ColumnInfo(name = "member21") val member21: Int,
    @ColumnInfo(name = "member22") val member22: Int,
    @ColumnInfo(name = "member23") val member23: Int,
    @ColumnInfo(name = "member24") val member24: Int,
    @ColumnInfo(name = "member25") val member25: Int,
    @ColumnInfo(name = "member26") val member26: Int,
    @ColumnInfo(name = "member27") val member27: Int,
    @ColumnInfo(name = "member28") val member28: Int,
    @ColumnInfo(name = "member29") val member29: Int,
    @ColumnInfo(name = "member30") val member30: Int
) : Serializable {

}