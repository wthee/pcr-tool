package cn.wthee.pcrtool.ui.tool

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.enums.LeaderTierType
import cn.wthee.pcrtool.data.model.LeaderTierData
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.ui.LoadingState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：角色评级
 */
@Immutable
data class LeaderTierUiState(
    val currentData: LeaderTierData? = null,
    //角色评级类型
    val leaderTierType: LeaderTierType = LeaderTierType.ALL,
    val leaderTierMap: HashMap<Int, ResponseData<LeaderTierData>> = hashMapOf(),
    val openDialog: Boolean = false,
    val loadingState: LoadingState = LoadingState.Loading
)

/**
 * 角色评级 ViewModel
 *
 * @param apiRepository
 */
@HiltViewModel
class LeaderTierViewModel @Inject constructor(
    private val apiRepository: MyAPIRepository,
    private val unitRepository: UnitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderTierUiState())
    val uiState: StateFlow<LeaderTierUiState> = _uiState.asStateFlow()

    init {
        getLeaderTier(LeaderTierType.ALL.type)
    }

    /**
     * 获取角色基本信息
     *
     * @param unitId 角色编号
     */
    fun getCharacterBasicInfo(unitId: Int) = flow {
        emit(unitRepository.getCharacterBasicInfo(unitId))
    }

    /**
     * 获取排行评级
     */
    private fun getLeaderTier(type: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    loadingState = LoadingState.Loading
                )
            }

            val leaderTierMap = _uiState.value.leaderTierMap
            if (leaderTierMap[type] == null) {
                leaderTierMap[type] = apiRepository.getLeaderTier(type)
            }

            _uiState.update {
                it.copy(
                    currentData = leaderTierMap[type]?.data,
                    leaderTierMap = leaderTierMap,
                    leaderTierType = LeaderTierType.getByValue(type),
                    loadingState = it.loadingState.isSuccess(leaderTierMap[type]?.data != null)
                )
            }
        }
    }

    /**
     * 切换类型
     */
    fun changeSelect(type: Int) {
        getLeaderTier(type = type)
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
}
