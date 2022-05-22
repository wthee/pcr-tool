package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.model.GuildAllMember
import cn.wthee.pcrtool.utils.intArrayList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 公会信息 ViewModel
 *
 * @param unitRepository
 */
@HiltViewModel
class GuildViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    /**
     * 获取公会
     */
    fun getGuilds() = flow {
        try {
            val data = unitRepository.getGuilds()
            val list = arrayListOf<GuildAllMember>()
            data.forEach {
                val allMember = GuildAllMember(
                    it.guildId,
                    it.guildName,
                    it.getDesc(),
                    it.getMemberIds()
                )
                try {
                    val add = unitRepository.getGuildAddMembers(it.guildId)
                    allMember.newMemberIds = add?.getMemberIds() ?: arrayListOf()
                } catch (e: Exception) {

                }
                list.add(allMember)
            }
            //无公会成员
            val noGuildData = unitRepository.getNoGuildMembers()
            noGuildData?.let {
                val members = it.intArrayList
                list.add(
                    GuildAllMember(
                        guildId = 999,
                        guildName = "无公会",
                        memberIds = members
                    )
                )
            }
            emit(list)
        } catch (e: Exception) {

        }
    }

}
