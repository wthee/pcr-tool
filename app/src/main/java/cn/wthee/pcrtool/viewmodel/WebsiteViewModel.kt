package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.network.MyAPIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 网站 ViewModel
 *
 * @param apiRepository
 */
@HiltViewModel
class WebsiteViewModel @Inject constructor(
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    /**
     * 获取排行
     */
    fun getWebsiteList() = flow {
        val data = apiRepository.getWebsiteList()
        emit(data)
    }

}
