package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.QuestRepository
import cn.wthee.pcrtool.utils.LogReportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 装备 ViewModel
 *
 * @param equipmentRepository
 * @param questRepository
 */
@HiltViewModel
class EquipmentViewModel @Inject constructor(
    private val equipmentRepository: EquipmentRepository,
    private val questRepository: QuestRepository
) : ViewModel() {


    /**
     * 获取专用装备列表
     *
     * @param name 装备或角色名
     */
    fun getUniqueEquips(name: String, slot: Int) = flow {
        try {
            val data = equipmentRepository.getUniqueEquipList(name, slot)
            emit(data)
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getUniqueEquips#name:$name")
        }
    }
}
