package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.UnitRepository
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
     * 竞技场角色信息
     */
    fun getAllCharacter() = flow {
        try {
            emit(unitRepository.getCharacterByPosition(1, 999))
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

