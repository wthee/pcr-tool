package cn.wthee.pcrtool.data.model

data class GuildAllMember(
    val guildId: Int = 1,
    val guildName: String = "???",
    val desc: String = "???",
    val memberIds: List<Int> = listOf(),
    var newMemberIds: List<Int> = listOf()
)
