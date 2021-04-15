package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.model.AppNotice
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import kotlinx.coroutines.launch

/**
 * 通知 ViewModel
 *
 * 数据来源 [MyAPIRepository]
 */
class NoticeViewModel : ViewModel() {

    val notice = MutableLiveData<ResponseData<List<AppNotice>>>()

    /**
     * 通知公告
     */
    fun getNotice() {
        viewModelScope.launch {
            val data = MyAPIRepository.getInstance().getNotice()
            notice.postValue(data)
        }
    }
}
