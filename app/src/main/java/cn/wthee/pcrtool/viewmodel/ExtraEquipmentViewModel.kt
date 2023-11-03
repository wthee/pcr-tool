package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.ExtraEquipmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * ex装备 ViewModel
 *
 * @param equipmentRepository
 */
@HiltViewModel
class ExtraEquipmentViewModel @Inject constructor(
    private val equipmentRepository: ExtraEquipmentRepository
) : ViewModel() {

    /**
     * 获取可使用装备的角色列表
     */
    fun getExtraEquipUnitList(category: Int) = flow {
        emit(equipmentRepository.getEquipUnitList(category))
    }

    /**
     * 次要掉落信息
     */
    fun getSubRewardList(questId: Int) = flow {
        emit(equipmentRepository.getSubRewardList(questId))
    }

    /**
     * 冒险区域详情
     */
    fun getTravelQuest(questId: Int) = flow {
        emit(equipmentRepository.getTravelQuest(questId))
    }

    /**
     * ex冒险区域
     */
    fun getTravelAreaList() = flow {
        emit(equipmentRepository.getTravelAreaList())
    }


    /**
     * 获取装所有备技能id
     *
     */
    fun getAllEquipSkillIdList() = flow {
        try {
            val data = equipmentRepository.getAllEquipSkillIdList()
            emit(data)
        } catch (_: Exception) {

        }
    }
}
