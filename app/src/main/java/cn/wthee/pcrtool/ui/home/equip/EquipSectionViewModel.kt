package cn.wthee.pcrtool.ui.home.equip

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.model.FilterEquip
import cn.wthee.pcrtool.utils.ImageRequestHelper
import cn.wthee.pcrtool.utils.LogReportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：装备纵览
 */
@Immutable
data class EquipSectionUiState(
    //装备数量
    val equipCount: Int = 0,
    //装备列表
    val equipIdList: List<Int>? = null
)

/**
 * 装备纵览
 */
@HiltViewModel
class EquipSectionViewModel @Inject constructor(
    private val equipmentRepository: EquipmentRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(EquipSectionUiState())
    val uiState: StateFlow<EquipSectionUiState> = _uiState.asStateFlow()


    fun loadData(limit: Int) {
        if (_uiState.value.equipIdList == null) {
            val initList = arrayListOf<Int>()
            for (i in 1..limit) {
                initList.add(ImageRequestHelper.UNKNOWN_EQUIP_ID)
            }
            _uiState.update {
                it.copy(
                    equipIdList = initList
                )
            }
        }
        getEquipCount()
        getEquipInfoList(limit)
    }

    /**
     * 获取装备数量
     */
    private fun getEquipCount() {
        viewModelScope.launch {
            val count = equipmentRepository.getCount()
            _uiState.update {
                it.copy(
                    equipCount = count
                )
            }
        }
    }

    /**
     * 获取装备列表
     */
    private fun getEquipInfoList(limit: Int) {
        viewModelScope.launch {
            try {
                val filterList = equipmentRepository.getEquipmentList(FilterEquip(), limit)?.map {
                    it.equipmentId
                }

                _uiState.update {
                    it.copy(
                        equipIdList = filterList
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getEquipInfoList")
            }

        }
    }

}