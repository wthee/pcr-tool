package cn.wthee.pcrtool.navigation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.enums.MainIconType
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
     * 加载中
     */
    val loading = MutableLiveData(false)

    /**
     * 重置
     */
    val resetClick = MutableLiveData(false)

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
     * 专用装备搜索
     */
    val uniqueEquipName = MutableLiveData<String>()

}
