package cn.wthee.pcrtool.ui.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.MainActivity.Companion.navViewModel
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.editOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject


/**
 * 页面状态：角色纵览
 */
@Immutable
data class OverviewScreenUiState(
    //排序数量
    val orderData: String = "",
    //日程点击展开状态
    val confirmState: Int = 0,
    //编辑模式
    val isEditMode: Boolean = false,
    //设置菜单弹窗
    val showDropMenu: Boolean = false,
    //数据切换弹窗
    val showChangeDb: Boolean = false,
)

/**
 * 首页纵览
 */
@HiltViewModel
class OverviewScreenViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
) : ViewModel() {
    private val defaultOrder = "0-1-6-2-3-4-5-"

    private val _uiState = MutableStateFlow(OverviewScreenUiState())
    val uiState: StateFlow<OverviewScreenUiState> = _uiState.asStateFlow()

    init {
        getOrderData()
    }

    /**
     * 加载模块排序信息
     */
    private fun getOrderData() {
        val orderData = runBlocking {
            val data = MyApplication.context.dataStoreMain.data.first()
            data[MainPreferencesKeys.SP_OVERVIEW_ORDER] ?: defaultOrder
        }
        _uiState.update {
            it.copy(
                orderData = orderData
            )
        }
    }

    /**
     * 主按钮点击
     */
    fun fabClick() {
        viewModelScope.launch {
            _uiState.update {
                //避免同时弹出
                if (it.showChangeDb) {
                    it.copy(
                        showChangeDb = false
                    )
                } else {
                    it.copy(
                        showDropMenu = !it.showDropMenu
                    )
                }
            }
        }
    }

    /**
     * 数据切换点击
     */
    fun changeDbClick() {
        viewModelScope.launch {
            _uiState.update {
                //避免同时弹出
                if (it.showDropMenu) {
                    it.copy(
                        showDropMenu = false
                    )
                } else {
                    it.copy(
                        showChangeDb = !it.showChangeDb
                    )
                }
            }
        }
    }

    /**
     * 关闭弹窗
     */
    fun closeAll() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    showDropMenu = false,
                    showChangeDb = false,
                )
            }
        }
    }

    /**
     * 编辑模式
     */
    fun changeEditMode() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isEditMode = !it.isEditMode
                )
            }
        }
    }

    /**
     * 编辑排序
     */
    fun updateOrderData(id: Int) {
        viewModelScope.launch {
            editOrder(
                MyApplication.context,
                viewModelScope,
                id,
                MainPreferencesKeys.SP_OVERVIEW_ORDER
            ) { data ->
                _uiState.update {
                    it.copy(
                        orderData = data
                    )
                }
            }
        }
    }

    /**
     * 六星 id 列表
     */
    fun getR6Ids() {
        viewModelScope.launch {
            try {
                val r6Ids = unitRepository.getR6Ids()
                MainActivity.r6Ids = r6Ids
            } catch (e: Exception) {
                navViewModel.dbError.postValue(true)
                LogReportUtil.upload(e, "getR6Ids")
            }
            try {
                navViewModel.dbError.postValue(unitRepository.getCountInt() == 0)
            } catch (e: Exception) {
                navViewModel.dbError.postValue(true)
                LogReportUtil.upload(e, "dbError")
            }
        }
    }

}