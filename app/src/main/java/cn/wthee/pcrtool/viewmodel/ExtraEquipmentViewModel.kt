package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.ExtraEquipmentRepository
import cn.wthee.pcrtool.data.db.view.EquipmentDropInfo
import cn.wthee.pcrtool.data.model.FilterExtraEquipment
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
    fun getEquips(params: FilterExtraEquipment) = flow {
        try {
            val data = equipmentRepository.getEquipments(params, Int.MAX_VALUE)
            emit(data)
        } catch (_: Exception) {

        }
    }

    /**
     * 获取装备信息
     *
     * @param equipId 装备编号
     */
    fun getEquip(equipId: Int) = flow {
        try {
            emit(equipmentRepository.getEquipmentData(equipId))
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

    /**
     * 获取装备类别
     */
    fun getEquipCategoryList() = flow {
        emit(equipmentRepository.getEquipCategoryList())
    }

    /**
     * 获取可使用装备的角色列表
     */
    fun getEquipUnitList(category: Int) = flow {
        emit(equipmentRepository.getEquipUnitList(category))
    }

    /**
     * 获取角色可使用的ex装备列表
     */
    fun getCharacterExtraEquipList(unitId: Int) = flow {
        emit(equipmentRepository.getCharacterExtraEquipList(unitId))
    }
}
