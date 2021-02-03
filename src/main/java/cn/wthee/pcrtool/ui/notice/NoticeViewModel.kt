package cn.wthee.pcrtool.ui.notice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.network.model.AppNotice
import cn.wthee.pcrtool.data.network.model.ResponseData
import kotlinx.coroutines.launch

/**
 * 通知 ViewModel
 *
 * 数据来源 [MyA]
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
