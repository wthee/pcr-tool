package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.RoomCommentData
import cn.wthee.pcrtool.data.enums.SortType
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
    fun getCharacters(params: FilterCharacter?) = flow {
        try {
            if (params != null) {
                //六星排序时，仅显示六星角色
                if (params.sortType == SortType.SORT_UNLOCK_6) {
                    params.r6 = 1
                }

                var filterList = unitRepository.getInfoAndData(params, Int.MAX_VALUE)

                //按六星解放时间排序
                if (params.sortType == SortType.SORT_UNLOCK_6) {
                    val sortedIdList = unitRepository.getR6UnitIdList(params.asc)
                    filterList = filterList.sortedWith { o1, o2 ->
                        val id1 = sortedIdList.indexOf(o1.id)
                        val id2 = sortedIdList.indexOf(o2.id)
                        id1.compareTo(id2)
                    }
                }

                emit(filterList)
            }
        } catch (_: Exception) {

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
     * 角色站位
     */
    suspend fun getPvpCharacterByIds(ids: List<Int>) =
        try {
            unitRepository.getCharacterByIds(ids).filter { it.position > 0 }
        } catch (e: Exception) {
            arrayListOf()
        }

}

