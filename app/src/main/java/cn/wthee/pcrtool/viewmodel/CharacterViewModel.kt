package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.RoomCommentData
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
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
    fun getCharacterInfoList(params: FilterCharacter?) = flow {
        try {
            if (params != null) {
                val filterList = unitRepository.getCharacterInfoList(params, Int.MAX_VALUE)
                emit(filterList)
            }
        } catch (e: Exception) {
            LogReportUtil.upload(
                e,
                Constants.EXCEPTION_UNIT_NULL + "getCharacterInfoList#params:${params ?: ""}"
            )
        }

    }


    /**
     * 获取角色基本信息
     *
     * @param unitId 角色编号
     */
    fun getCharacterBasicInfo(unitId: Int) = flow {
        try {
            emit(unitRepository.getCharacterBasicInfo(unitId))
        }catch (e:Exception){
            LogReportUtil.upload(
                e,
                Constants.EXCEPTION_UNIT_NULL + "getCharacterBasicInfo#unitId:$unitId"
            )
        }
    }

    /**
     * 获取角色基本资料
     *
     * @param unitId 角色编号
     */
    fun getCharacter(unitId: Int) = flow {
        //校验是否未多角色卡
        val data = unitRepository.getInfoPro(unitId)
        if (data == null) {
            LogReportUtil.upload(
                NullPointerException(),
                Constants.EXCEPTION_UNIT_NULL + "unit_id:$unitId"
            )
        }
        emit(data)
    }

    /**
     * 获取角色基本资料
     *
     * @param unitId 角色编号
     */
    fun getHomePageComments(unitId: Int) = flow {
        //校验是否未多角色卡
        val data = unitRepository.getHomePageComments(unitId)
        emit(data)
    }

    /**
     * 获取角色小屋对话
     *
     * @param unitId 角色编号
     */
    fun getRoomComments(unitId: Int) = flow {
        //校验是否为多角色卡
        val ids = arrayListOf(unitId)
        try {
            val multiIds = unitRepository.getMultiIds(unitId)
            if (multiIds.isNotEmpty()) {
                ids.addAll(multiIds)
            }
        } catch (_: Exception) {

        }
        val commentList = arrayListOf<RoomCommentData>()
        ids.forEach {
            val data = unitRepository.getRoomComments(it)
            if (data != null) {
                commentList.add(data)
            }
        }

        emit(commentList)
    }

    /**
     * 竞技场角色信息
     */
    fun getAllCharacter() = flow {
        try {
            emit(unitRepository.getCharacterByPosition(1, 999))
        } catch (_: Exception) {

        }
    }

    /**
     * 公会信息
     */
    fun getGuilds() = flow {
        try {
            emit(unitRepository.getGuilds())
        } catch (_: Exception) {

        }
    }

    /**
     * 种族信息
     */
    fun getRaces() = flow {
        try {
            emit(unitRepository.getRaces())
        } catch (_: Exception) {

        }
    }

    /**
     * 角色站位
     */
    suspend fun getPvpCharacterByIds(ids: List<Int>) =
        try {
            unitRepository.getCharacterByIds(ids).filter { it.position > 0 }
        } catch (e: Exception) {
            arrayListOf()
        }

    /**
     * 获取普通攻击时间
     */
    fun getAtkCastTime(unitId: Int) = flow {
        try {
            emit(unitRepository.getAtkCastTime(unitId))
        } catch (_: Exception) {

        }
    }

}

