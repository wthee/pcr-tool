package cn.wthee.pcrtool.data.db.view

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * 公会额外成员
 */
data class GuildAdditionalMember(
    @PrimaryKey
    @ColumnInfo(name = "guild_id") val guildId: Int,
    @ColumnInfo(name = "unlock_story_id") val unlockStoryId: Int,
    @ColumnInfo(name = "thumb_id") val thumb_id: Int,
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
)