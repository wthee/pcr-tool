package cn.wthee.pcrtool.ui.character.detail

import androidx.compose.runtime.Immutable
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.repository.EquipmentRepository
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.Attr
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.UnitStatusCoefficient
import cn.wthee.pcrtool.data.enums.CharacterDetailModuleType
import cn.wthee.pcrtool.data.model.AllAttrData
import cn.wthee.pcrtool.data.model.CharacterProperty
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.utils.JsonUtil
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.editOrder
import cn.wthee.pcrtool.utils.int
import cn.wthee.pcrtool.utils.intArrayList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.math.max


/**
 * 页面状态：角色详情
 */
@Immutable
data class CharacterDetailUiState(
    //角色id
    val unitId: Int = 0,
    //角色信息
    val basicInfo: CharacterInfo? = null,
    //角色转换id
    val cutinId: Int = 0,
    //当前角色id
    val currentId: Int = 0,
    //形态转换
    val isCutinSkill: Boolean = true,
    //最大值
    val maxValue: CharacterProperty = CharacterProperty(),
    //当前值
    val currentValue: CharacterProperty = CharacterProperty(),
    //属性
    val allAttr: AllAttrData = AllAttrData(),
    //最大攻击力
    val maxAtk: Int = 0,
    //编辑模式
    val isEditMode: Boolean = false,
    //收藏角色
    val loved: Boolean = false,
    //排序
    val orderData: String = "",
    //主页面模块id列表
    val mainList: List<Int> = emptyList(),
    //次要页面模块id列表
    val subList: List<Int> = emptyList(),
    //展示所有信息
    val showAllInfo: Boolean = true,
    //战力系数
    val coeValue: UnitStatusCoefficient? = null,
    //加载状态
    val loadingState: LoadingState = LoadingState.Loading,
    //页面数量
    val pageCount: Int = 0,
)


/**
 * 角色 ViewModel
 *
 * @param unitRepository
 */
@HiltViewModel
class CharacterDetailViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    private val equipmentRepository: EquipmentRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val defaultOrder = "300-301-302-303-304-305-306-307-308-310-"

    private val unitId: Int? = savedStateHandle[NavRoute.UNIT_ID]
    private val showAllInfo: Boolean = savedStateHandle[NavRoute.SHOW_ALL_INFO] ?: true

    private val _uiState = MutableStateFlow(CharacterDetailUiState())
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    init {
        if (unitId != null) {
            _uiState.update {
                it.copy(
                    showAllInfo = showAllInfo,
                    unitId = unitId,
                )
            }
            updateOrderList(getOrderData(showAllInfo))
            getCoefficient()
            getCutinId(unitId)
            getCharacterBasicInfo(unitId)
            getMaxRankAndRarity(unitId)
            getLoveState(unitId)
        }
    }


    /**
     * 获取角色基本信息
     *
     * @param unitId 角色编号
     */
    private fun getCharacterBasicInfo(unitId: Int) {
        viewModelScope.launch {
            val data = unitRepository.getCharacterBasicInfo(unitId)
            _uiState.update {
                it.copy(
                    basicInfo = data,
                    loadingState = it.loadingState.isNoData(data == null)
                )
            }
        }
    }

    /**
     * 获取特殊六星 id
     *
     * @param unitId 角色编号
     */
    private fun getCutinId(unitId: Int) {
        viewModelScope.launch {
            val cutinId = unitRepository.getCutinId(unitId)
            _uiState.update {
                it.copy(
                    cutinId = cutinId,
                    currentId = if (cutinId != 0) cutinId else unitId,
                )
            }
        }
    }

    /**
     * 获取最大Rank和星级等
     *
     * @param unitId 角色编号
     */
    private fun getMaxRankAndRarity(unitId: Int) {
        viewModelScope.launch {
            try {
                val rank = unitRepository.getMaxRank(unitId)
                val rarity = unitRepository.getMaxRarity(unitId)
                val level = unitRepository.getMaxLevel()
                val uniqueEquipLevel = equipmentRepository.getUniqueEquipMaxLv(1) ?: 0
                val uniqueEquipLevel2 = equipmentRepository.getUniqueEquipMaxLv(2) ?: 0
                val maxValue =
                    CharacterProperty(level, rank, rarity, uniqueEquipLevel, uniqueEquipLevel2)
                //数值信息
                if (!_uiState.value.currentValue.isInit()) {
                    //初始为最大值
                    _uiState.update {
                        it.copy(
                            currentValue = maxValue
                        )
                    }
                    //初始加载
                    getCharacterInfo(unitId, maxValue)

                }
                _uiState.update {
                    it.copy(
                        maxValue = maxValue,
                        loadingState = it.loadingState.isError(maxValue.level == -1)
                    )
                }
            } catch (e: Exception) {
                LogReportUtil.upload(e, "getMaxRankAndRarity:$unitId")
                _uiState.update {
                    it.copy(
                        maxValue = CharacterProperty(level = -1)
                    )
                }
            }
        }
    }


    /**
     * 根据角色 id  星级 等级 专武等级
     * 获取角色属性信息 [Attr]
     * @param unitId 角色编号
     * @param property 角色属性
     */
    private fun getCharacterInfo(unitId: Int, property: CharacterProperty?) {
        viewModelScope.launch {
            val allAttr = unitRepository.getCharacterInfo(unitId, property)
            if (allAttr == null) {
                _uiState.update {
                    it.copy(
                        loadingState = LoadingState.Error
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        allAttr = allAttr,
                        maxAtk = max(
                            allAttr.sumAttr.atk.int,
                            allAttr.sumAttr.magicStr.int
                        ),
                        loadingState = it.loadingState.isSuccess(allAttr.sumAttr.hp > 1 && allAttr.equips.isNotEmpty())
                    )
                }
            }
        }
    }

    /**
     * 获取角色收藏列表
     */
    private fun getLoveState(unitId: Int) {
        viewModelScope.launch {
            val list = FilterCharacter.getStarIdList()
            _uiState.update {
                it.copy(loved = list.contains(unitId))
            }
        }
    }

    /**
     * 加载模块排序信息
     */
    private fun getOrderData(showAllInfo: Boolean): String {
        //自定义显示顺序
        return if (showAllInfo) {
            //角色详情页面
            runBlocking {
                val data = MyApplication.context.dataStoreMain.data.first()
                data[MainPreferencesKeys.SP_CHARACTER_DETAIL_ORDER] ?: defaultOrder
            }
        } else {
            //专武详情页面
            "${CharacterDetailModuleType.UNIT_ICON.id}-${CharacterDetailModuleType.UNIQUE_EQUIP.id}-${CharacterDetailModuleType.SKILL.id}-"
        }
    }

    /**
     * 加载模块排序信息
     */
    private fun updateOrderList(orderData: String) {
        //选中的
        val mainList = orderData.intArrayList
        //未选中的
        val subList = arrayListOf(
            CharacterDetailModuleType.UNIT_ICON.id,
            CharacterDetailModuleType.CARD.id,
            CharacterDetailModuleType.COE.id,
            CharacterDetailModuleType.TOOLS.id,
            CharacterDetailModuleType.STAR.id,
            CharacterDetailModuleType.LEVEL.id,
            CharacterDetailModuleType.ATTR.id,
            CharacterDetailModuleType.OTHER_TOOLS.id,
            CharacterDetailModuleType.EQUIP.id,
            CharacterDetailModuleType.UNIQUE_EQUIP.id,
            CharacterDetailModuleType.SKILL.id,
        ).filter {
            !orderData.intArrayList.contains(it)
        }
        _uiState.update {
            it.copy(
                mainList = mainList,
                subList = subList,
                orderData = orderData,
                pageCount = if (it.showAllInfo && subList.isNotEmpty()) 2 else 1
            )
        }
    }

    /**
     * 获取战力系数
     */
    private fun getCoefficient() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    coeValue = unitRepository.getCoefficient()
                )
            }
        }
    }

    /**
     * 形态切换
     */
    fun changeCutin() {
        unitId?.let {
            _uiState.update { state ->
                state.copy(
                    currentId = if (state.isCutinSkill) unitId else state.cutinId,
                    isCutinSkill = !state.isCutinSkill,
                )
            }
        }
    }

    /**
     * 更新当前属性值
     */
    fun updateCurrentValue(value: CharacterProperty) {
        _uiState.update {
            it.copy(
                currentValue = value
            )
        }
        if (unitId != null) {
            getCharacterInfo(unitId, _uiState.value.currentValue)
        }
    }

    /**
     * 编辑模式
     */
    fun changeEditMode(isEditMode: Boolean) {
        _uiState.update {
            it.copy(
                isEditMode = isEditMode
            )
        }
    }

    /**
     * 更新收藏id
     */
    fun updateStarId() {
        viewModelScope.launch {
            if (unitId != null) {
                MyApplication.context.dataStoreMain.edit { preferences ->
                    val list =
                        JsonUtil.toIntList(preferences[MainPreferencesKeys.SP_STAR_CHARACTER])
                    if (list.contains(unitId)) {
                        list.remove(unitId)
                        _uiState.update {
                            it.copy(
                                loved = false
                            )
                        }
                    } else {
                        list.add(unitId)
                        _uiState.update {
                            it.copy(
                                loved = true
                            )
                        }
                    }
                    preferences[MainPreferencesKeys.SP_STAR_CHARACTER] = Json.encodeToString(list)
                }
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
                MainPreferencesKeys.SP_CHARACTER_DETAIL_ORDER
            ) { data ->
                updateOrderList(data)
            }
        }
    }
}

