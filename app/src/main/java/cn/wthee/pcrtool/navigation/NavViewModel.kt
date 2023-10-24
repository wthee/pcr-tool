package cn.wthee.pcrtool.navigation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.DatabaseVersion
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.data.model.FilterExtraEquipment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 导航 ViewModel
 * 在应用整个生命周期使用的数据，或者需要在不同页面共享的数据
 */
@HiltViewModel
class NavViewModel @Inject constructor() : ViewModel() {

    /**
     * fab 图标显示
     */
    val fabMainIcon = MutableLiveData(MainIconType.MAIN)

    /**
     * 确认
     */
    val fabOKClick = MutableLiveData(false)

    /**
     * 关闭
     */
    val fabCloseClick = MutableLiveData(false)

    /**
     * 下载状态
     * -2: 隐藏
     * -1: 显示加载中
     * >0: 进度
     */
    val downloadProgress = MutableLiveData(-1)

    /**
     * apk下载状态
     * -4: 安装包安装失败
     * -3: 下载失败
     * -2: 隐藏
     * -1: 显示加载中
     * >0: 进度
     * >200: 下载成功
     */
    val apkDownloadProgress = MutableLiveData(-2)

    /**
     * 加载中
     */
    val loading = MutableLiveData(false)

    /**
     * 已六星的角色ID
     */
    val r6Ids = MutableLiveData(listOf<Int>())

    /**
     * 重置
     */
    val resetClick = MutableLiveData(false)

    /**
     * 角色筛选
     */
    var filterCharacter = MutableLiveData<FilterCharacter?>()

    /**
     * 装备筛选
     */
    var filterEquip = MutableLiveData<FilterEquipment?>()

    /**
     * ex装备筛选
     */
    var filterExtraEquip = MutableLiveData<FilterExtraEquipment?>()

    /**
     * 竞技场查询角色
     */
    val selectedPvpData = MutableLiveData(
        arrayListOf(
            PvpCharacterData(),
            PvpCharacterData(),
            PvpCharacterData(),
            PvpCharacterData(),
            PvpCharacterData()
        )
    )

    /**
     * 悬浮服务
     */
    val floatServiceRun = MutableLiveData(true)

    /**
     * 悬浮窗最小化
     */
    val floatSearchMin = MutableLiveData(false)

    /**
     * pvp 查询结果显示
     */
    val showResult = MutableLiveData(false)

    /**
     * 选中的rank
     */
    val rankEquipSelected = MutableLiveData(0)

    /**
     * 数据库更新信息
     */
    val dbVersion = MutableLiveData<DatabaseVersion>()

    /**
     * 搜索装备编号
     */
    val searchEquipIdList = MutableLiveData<ArrayList<Int>>()

    /**
     * 搜索装备模式
     */
    val searchEquipMode = MutableLiveData<Boolean>()

    /**
     * 专用装备搜索
     */
    val uniqueEquipName = MutableLiveData<String>()

    /**
     * 数据文件异常
     */
    val dbError = MutableLiveData(false)
}
