package cn.wthee.pcrtool.ui.tool.uniqueequip

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.QuestRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.UniqueEquipBasicData
import cn.wthee.pcrtool.data.model.KeywordData
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.updateLoadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 页面状态：专用装备
 */
@Immutable
data class UniqueEquipListUiState(
    val uniqueEquipList: List<UniqueEquipBasicData>? = null,
    val loadState: LoadState = LoadState.Loading,
    //搜索弹窗
    val openSearch: Boolean = false,
    //快捷搜索关键词
    val keywordList: List<KeywordData> = emptyList(),
    val keyword: String = ""
)

/**
 * 专用装备 ViewModel
 *
 * @param equipmentRepository
 * @param questRepository
 */
@HiltViewModel
class UniqueEquipListViewModel @Inject constructor(
    private val equipmentRepository: EquipmentRepository,
    private val questRepository: QuestRepository,
    private val unitRepository: UnitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UniqueEquipListUiState())
    val uiState: StateFlow<UniqueEquipListUiState> = _uiState.asStateFlow()

    init {
        getUniqueEquips("")
    }

    /**
     * 获取专用装备列表
     *
     * @param name 装备或角色名
     */
    private fun getUniqueEquips(name: String) {
        viewModelScope.launch {
            val list = equipmentRepository.getUniqueEquipList(name, 0)
            _uiState.update {
                it.copy(
                    uniqueEquipList = list,
                    loadState = updateLoadState(list)
                )
            }
        }
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
     * 关键词更新
     */
    fun changeKeyword(keyword: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    keyword = keyword
                )
            }
        }
        getUniqueEquips(keyword)
    }

    /**
     * 搜索弹窗
     */
    fun changeSearchBar(openSearch: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    openSearch = openSearch
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
                    keyword = ""
                )
            }
        }
        getUniqueEquips("")
    }
}
