package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.model.GuildAllMember
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 活动 ViewModel
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
        val data = unitRepository.getGuilds()
        val list = arrayListOf<GuildAllMember>()
        data.forEach {
            val add = unitRepository.getGuildAddMembers(it.guildId)
            list.add(
                GuildAllMember(
                    it.guildId,
                    it.guildName,
                    it.getDesc(),
                    it.getMemberIds(),
                    add?.getMemberIds() ?: listOf()
                )
            )
        }
        emit(list)
    }
}
