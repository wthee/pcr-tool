package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.utils.Constants

/**
 * 公会人员信息
 */
data class GuildAllMember(
    @PrimaryKey
    @ColumnInfo(name = "guild_id") var guildId: Int = 0,
    @ColumnInfo(name = "guild_name") var guildName: String = Constants.UNKNOWN,
    @ColumnInfo(name = "description") var description: String = Constants.UNKNOWN,
    @ColumnInfo(name = "guild_master") var guildMasterId: Int = 0,
    @ColumnInfo(name = "unit_ids") var unitIds: String = "0-0",
    @ColumnInfo(name = "unit_names") var unitNames: String = "0-0",
) {
    fun getDesc() = description.ifBlank { "?" }
}

data class GuildMemberInfo(
    val unitId: Int,
    val unitName: String
)

data class NoGuildMemberInfo(
    @ColumnInfo(name = "unit_ids") var unitIds: String = "0-0",
    @ColumnInfo(name = "unit_names") var unitNames: String = "0-0"
)
