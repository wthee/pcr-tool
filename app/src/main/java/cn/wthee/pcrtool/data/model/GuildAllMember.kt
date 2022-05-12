package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.utils.Constants

data class GuildAllMember(
    val guildId: Int = 1,
    val guildName: String = Constants.UNKNOWN,
    val desc: String = Constants.UNKNOWN,
    val memberIds: List<Int> = listOf(),
    var newMemberIds: List<Int> = listOf()
)
