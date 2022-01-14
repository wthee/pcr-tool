package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.CharacterInfoPro
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.UMengLogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 角色 ViewModel
 *
 * @param unitRepository
 */
@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    /**
     * 获取角色基本信息列表
     *
     * @param params 角色筛选
     */
    fun getCharacters(params: FilterCharacter?) = flow {
        if (params != null) {
            val guildName = if (params.guild > 0)
                unitRepository.getGuilds()[params.guild - 1].guildName
            else
                "全部"
            emit(unitRepository.getInfoAndData(params, guildName, Int.MAX_VALUE))
        }
    }


    /**
     * 获取角色基本信息
     *
     * @param unitId 角色编号
     */
    fun getCharacterBasicInfo(unitId: Int) = flow {
        emit(unitRepository.getInfoAndData(unitId))
    }

    /**
     * 获取角色基本资料
     *
     * @param unitId 角色编号
     */
    fun getCharacter(unitId: Int) = flow {
        //校验是否未多角色卡
        val ids = arrayListOf(unitId)
        try {
            val multiIds = unitRepository.getMultiIds(unitId)
            if (multiIds.isNotEmpty()) {
                ids.addAll(multiIds)
            }
        } catch (e: Exception) {

        }
        val infoList = arrayListOf<CharacterInfoPro>()
        ids.forEach {
            val data = unitRepository.getInfoPro(it)
            if (data == null) {
                UMengLogUtil.upload(
                    NullPointerException(),
                    Constants.EXCEPTION_UNIT_NULL + "unit_id:$it"
                )
            } else {
                infoList.add(data)
            }
        }

        emit(infoList)
    }

    /**
     * 竞技场角色信息
     */
    fun getAllCharacter() = flow {
        emit(unitRepository.getCharacterByPosition(0, 999))
    }

    /**
     * 公会信息
     */
    fun getGuilds() = flow {
        emit(unitRepository.getGuilds())
    }

    /**
     * 角色站位
     */
    suspend fun getPvpCharacterByIds(ids: List<Int>) = unitRepository.getCharacterByIds(ids)
}
