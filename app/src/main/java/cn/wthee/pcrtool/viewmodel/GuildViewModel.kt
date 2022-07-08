package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.GuildAllMember
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
            val data = unitRepository.getAllGuildMembers()
            val list = ArrayList(data)
//            list.forEach {
//                try {
//                    val add = unitRepository.getGuildAddMembers(it.guildId)
//                    it.newAddUnitIds = add?.getMemberIds() ?: arrayListOf()
//                } catch (e: Exception) {
//
//                }
//            }
            //无公会成员
            val noGuildData = unitRepository.getNoGuildMembers()
            noGuildData?.let {
                list.add(
                    GuildAllMember(
                        guildId = 999,
                        guildName = "无公会",
                        unitIds = it.unitIds,
                        unitNames = it.unitNames
                    )
                )
            }
            emit(list)
        } catch (e: Exception) {

        }
    }

}
