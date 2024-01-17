package cn.wthee.pcrtool.ui.tool.gacha

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import cn.wthee.pcrtool.data.db.view.GachaInfo
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.components.DateRange
import cn.wthee.pcrtool.ui.updateLoadingState
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.compareAllTypeEvent
import cn.wthee.pcrtool.utils.intArrayList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：卡池
 */
@Immutable
data class GachaListUiState(
    //日期
    val dateRange: DateRange = DateRange(),
    //日期选择弹窗
    val openDialog: Boolean = false,
    //卡池列表
    val gachaList: List<GachaInfo>? = null,
    //fes角色id
    val fesUnitIdList: List<Int> = emptyList(),
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 卡池 ViewModel
 */
@HiltViewModel
class GachaListViewModel @Inject constructor(
    private val gachaRepository: GachaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GachaListUiState())
    val uiState: StateFlow<GachaListUiState> = _uiState.asStateFlow()

    /**
     * 日期选择更新
     */
    fun changeRange(dateRange: DateRange) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    dateRange = dateRange
                )
            }
        }
        getGachaFesUnitList()
        getGachaHistory(dateRange)
    }

    /**
     * 弹窗状态更新
     */
    fun changeDialog(openDialog: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    openDialog = openDialog
                )
            }
        }
    }

    /**
     * 获取卡池记录
     */
    private fun getGachaHistory(dateRange: DateRange) {
        viewModelScope.launch {
            try {
                var list = gachaRepository.getGachaHistory(Int.MAX_VALUE)
                if (dateRange.hasFilter()) {
                    list = list.filter {
                        dateRange.predicate(it.startTime)
                    }
                }
                list = list.sortedWith(compareAllTypeEvent())

                _uiState.update {
                    it.copy(
                        gachaList = list,
                        loadingState = updateLoadingState(list)
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getGachaHistory")
            }
        }
    }

    /**
     * 获取卡池fes角色
     */
    private fun getGachaFesUnitList() {
        viewModelScope.launch {
            val data = gachaRepository.getFesUnitIdList()?.unitIds?.intArrayList ?: emptyList()
            _uiState.update {
                it.copy(
                    fesUnitIdList = data
                )
            }
        }
    }

    /**
     * 重置
     */
    fun reset() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    dateRange = DateRange()
                )
            }
        }
        getGachaFesUnitList()
        getGachaHistory(DateRange())
    }
}
