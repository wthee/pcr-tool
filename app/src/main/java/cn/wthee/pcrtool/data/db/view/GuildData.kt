package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * 公会信息
 */
data class GuildData(
    @PrimaryKey
    @ColumnInfo(name = "guild_id") val guildId: Int,
    @ColumnInfo(name = "guild_name") val guildName: String
)