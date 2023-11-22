package cn.wthee.pcrtool.navigation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * 导航 ViewModel
 * 在应用整个生命周期使用的数据，或者需要在不同页面共享的数据
 */
@HiltViewModel
class NavViewModel @Inject constructor() : ViewModel() {

    /**
     * 加载中
     */
    val loading = MutableLiveData(false)

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

}
