package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.ExtraEquipmentRepository
import cn.wthee.pcrtool.data.db.view.EquipmentDropInfo
import cn.wthee.pcrtool.data.model.FilterEquipment
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
     * 获取装备列表
     *
     * @param params 装备筛选
     */
    fun getEquips(params: FilterEquipment) = flow {
        try {
            val data = equipmentRepository.getEquipments(params, Int.MAX_VALUE)
            emit(data)
        } catch (_: Exception) {

        }
    }

    /**
     * 根据掉率排序
     *
     * @param equipId 装备编号
     */
    private fun getSort(equipId: Int): java.util.Comparator<EquipmentDropInfo> {
        val str = equipId.toString()
        return Comparator { o1: EquipmentDropInfo, o2: EquipmentDropInfo ->
            val a = o1.getOddOfEquip(str)
            val b = o2.getOddOfEquip(str)
            b.compareTo(a)
        }
    }

    /**
     * 获取装备颜色种类数
     */
    fun getEquipColorNum() = flow {
        emit(equipmentRepository.getEquipColorNum())
    }
}
