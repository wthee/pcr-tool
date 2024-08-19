package cn.wthee.pcrtool.ui.home

import androidx.compose.runtime.Immutable
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.enums.OverviewType
import cn.wthee.pcrtool.data.enums.RegionType
import cn.wthee.pcrtool.data.model.AppNotice
import cn.wthee.pcrtool.data.model.DatabaseVersion
import cn.wthee.pcrtool.data.network.ApiRepository
import cn.wthee.pcrtool.data.preferences.MainPreferencesKeys
import cn.wthee.pcrtool.data.preferences.SettingPreferencesKeys
import cn.wthee.pcrtool.database.AppBasicDatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity
import cn.wthee.pcrtool.ui.dataStoreMain
import cn.wthee.pcrtool.ui.dataStoreSetting
import cn.wthee.pcrtool.utils.Constants.SERVER_DOMAIN
import cn.wthee.pcrtool.utils.editOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
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
    val eventExpandState: Int = 0,
    //编辑模式
    val isEditMode: Boolean = false,
    //设置菜单弹窗
    val showDropMenu: Boolean = false,
    //数据切换弹窗
    val showChangeDb: Boolean = false,
    //顶部通知信息
    val appUpdateData: AppNotice = AppNotice(id = -1),
    /**
     * apk下载状态
     * -4: 安装包安装失败
     * -3: 下载失败
     * -2: 隐藏
     * -1: 显示加载中
     * >0: 进度
     * >200: 下载成功
     */
    val apkDownloadState: Int = -2,
    //应用更新布局状态
    val isAppNoticeExpanded: Boolean = false,
    //数据文件异常
    val dbError: Boolean = false,
    /**
     * 数据库文件下载状态
     * -3: 大小异常
     * -2: 隐藏
     * -1: 显示加载中
     * >0: 进度
     */
    val dbDownloadState: Int = DbDownloadState.LOADING.state,
    /**
     * 数据库更新信息
     */
    val dbVersion: DatabaseVersion? = null
)

/**
 * 数据库文件下载状态枚举
 */
enum class DbDownloadState(val state: Int) {
    //数据库文件大小异常
    SIZE_ERROR(-3),

    //正常状态
    NORMAL(-2),

    //加载中
    LOADING(-1);
}

/**
 * 首页纵览
 */
@HiltViewModel
class OverviewScreenViewModel @Inject constructor(
    private val unitRepository: UnitRepository,
    private val apiRepository: ApiRepository
) : ViewModel() {
    private val defaultOrder = "${OverviewType.CHARACTER.id}" +
            "-${OverviewType.EQUIP.id}" +
            "-${OverviewType.UNIQUE_EQUIP.id}" +
            "-${OverviewType.TOOL.id}" +
            "-${OverviewType.NEWS.id}" +
            "-${OverviewType.IN_PROGRESS_EVENT.id}" +
            "-${OverviewType.COMING_SOON_EVENT.id}"

    private val _uiState = MutableStateFlow(OverviewScreenUiState())
    val uiState: StateFlow<OverviewScreenUiState> = _uiState.asStateFlow()

    init {
        initCheck()
        getOrderData()
    }

    fun initCheck() {
        //数据文件正常读取判断
        checkDatabaseFile()
        //数据库校验
        MainScope().launch {
            AppBasicDatabaseUpdater.checkDBVersion(
                fixDb = false,
                updateDbDownloadState = this@OverviewScreenViewModel::updateDbDownloadState,
                updateDbVersion = this@OverviewScreenViewModel::updateDbVersion
            )
        }
        //应用更新校验
        checkAppUpdate()
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
    fun closeAllDialog() {
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
                context = MyApplication.context,
                scope = viewModelScope,
                id = id,
                key = MainPreferencesKeys.SP_OVERVIEW_ORDER
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
     * 数据文件正常读取判断
     */
    private fun checkDatabaseFile() {
        viewModelScope.launch {
            val dbError = unitRepository.getCountInt() == 0
            _uiState.update {
                it.copy(
                    dbError = dbError
                )
            }
        }
    }

    /**
     * 应用更新校验
     */
    private fun checkAppUpdate() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    appUpdateData = AppNotice(id = -1)
                )
            }

            //应用更新
            try {
                val data = apiRepository.getUpdateContent().data ?: AppNotice(id = -2)
                //将下载链接中的域名，根据设置替换为ip
                data.url = data.url.replace(SERVER_DOMAIN, MyApplication.URL_DOMAIN)
                _uiState.update {
                    it.copy(
                        appUpdateData = data
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        appUpdateData = AppNotice(id = -2)
                    )
                }
            }
        }
    }

    /**
     * 更新应用下载状态
     */
    fun updateApkDownloadState(state: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    apkDownloadState = state
                )
            }
        }
    }

    /**
     * 更新应用通知布局状态
     */
    fun updateExpanded(isAppNoticeExpanded: Boolean) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isAppNoticeExpanded = isAppNoticeExpanded
                )
            }
        }
    }

    /**
     * 更新日程展开布局状态
     */
    fun updateEventLayoutState(eventExpandState: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    eventExpandState = eventExpandState
                )
            }
        }
    }

    /**
     * 更新数据库下载状态
     */
    fun updateDbDownloadState(state: Int) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    dbDownloadState = state
                )
            }
        }
    }

    /**
     * 更新数据库版本信息
     */
    fun updateDbVersion(dbVersion: DatabaseVersion?) {
        val key = when (MainActivity.regionType) {
            RegionType.CN -> SettingPreferencesKeys.SP_DATABASE_VERSION_CN
            RegionType.TW -> SettingPreferencesKeys.SP_DATABASE_VERSION_TW
            RegionType.JP -> SettingPreferencesKeys.SP_DATABASE_VERSION_JP
        }

        viewModelScope.launch {
            //更新本地数据库版本、哈希值
            dbVersion?.let {
                MyApplication.context.dataStoreSetting.edit {
                    it[key] = dbVersion.toString()
                }
            }

            _uiState.update {
                it.copy(
                    dbVersion = dbVersion
                )
            }
        }
    }
}