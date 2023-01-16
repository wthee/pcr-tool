package cn.wthee.pcrtool.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.enums.MainIconType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.model.FilterEquipment
import cn.wthee.pcrtool.data.model.FilterExtraEquipment
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


/**
 * 导航 ViewModel
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
    val fabOKCilck = MutableLiveData(false)

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
    var filterCharacter = MutableLiveData(FilterCharacter())

    /**
     * 装备筛选
     */
    var filterEquip = MutableLiveData(FilterEquipment())

    /**
     * ex装备筛选
     */
    var filterExtraEquip = MutableLiveData(FilterExtraEquipment())

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
     * 悬浮闯最小化
     */
    val floatSearchMin = MutableLiveData(false)

    /**
     * pvp 查询结果显示
     */
    val showResult = MutableLiveData(false)

    /**
     * 数据切换弹窗显示
     */
    val openChangeDataDialog = MutableLiveData(false)

    /**
     * 模拟卡池结果显示
     */
    val showMockGachaResult = MutableLiveData(false)

    /**
     * 模拟卡池数据
     */
    val gachaId = MutableLiveData<String>()

    /**
     * 模拟卡池类型
     * 0：自选角色 1：fes角色
     */
    val gachaType = MutableLiveData<Int>()

    /**
     * 模拟卡池 pickUp 角色
     */
    val pickUpList = MutableLiveData<List<GachaUnitInfo>>()

    /**
     * 菜单顺序
     */
    val toolOrderData = MutableLiveData<String>()

    /**
     * 首页显示
     */
    val overviewOrderData = MutableLiveData<String>()

    /**
     * 角色评级类型
     */
    val leaderTierType = MutableLiveData(0)

}
