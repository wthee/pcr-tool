package cn.wthee.pcrtool.data.model

data class GuildAllMember(
    val guildId: Int,
    val guildName: String,
    val desc: String,
    val memberIds: List<Int>,
    val newMemberIds: List<Int>
)
