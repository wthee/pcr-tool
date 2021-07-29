package cn.wthee.pcrtool.data.model

data class GuildAllMember(
    val guildId: Int = 1,
    val guildName: String = "???",
    val desc: String = "???",
    val memberIds: List<Int> = listOf(100101),
    val newMemberIds: List<Int> = listOf(100102)
)
