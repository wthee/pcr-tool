package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.stringArrayList

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

    /**
     * 获取名字，（之前的
     */
    fun getNameList(): ArrayList<String> {
        val names = unitNames.stringArrayList
        val newNames = arrayListOf<String>()
        names.forEach {
            newNames.add(it.substringBefore('（'))
        }
        return newNames
    }
}

data class GuildMemberInfo(
    val unitId: Int,
    val unitName: String
)

data class NoGuildMemberInfo(
    @ColumnInfo(name = "unit_ids") var unitIds: String = "0-0",
    @ColumnInfo(name = "unit_names") var unitNames: String = "0-0"
)
