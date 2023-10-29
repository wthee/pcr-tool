package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.utils.LogReportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 角色面板属性 ViewModel
 *
 * @param unitRepository
 * @param equipmentRepository
 *
 */
@HiltViewModel
class CharacterAttrViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    private val equipmentRepository: EquipmentRepository
) : ViewModel() {

    /**
     * 当前属性
     */
    val currentValue = MutableLiveData(CharacterProperty())

    /**
     * 根据角色 id  星级 等级 专武等级
     * 获取角色属性信息 [Attr]
     * @param unitId 角色编号
     * @param property 角色属性
     */
    fun getCharacterInfo(unitId: Int, property: CharacterProperty?) = flow {
        try {
            if (property != null && property.isInit()) {
                emit(
                    unitRepository.getAttrs(
                        unitId,
                        property.level,
                        property.rank,
                        property.rarity,
                        property.uniqueEquipmentLevel,
                        property.uniqueEquipmentLevel2
                    )
                )
            }
        } catch (e: Exception) {
            LogReportUtil.upload(
                e,
                "getCharacterInfo#unitId:$unitId,property:${property ?: ""}"
            )
        }
    }
}
