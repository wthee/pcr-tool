package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.model.AppNotice
import cn.wthee.pcrtool.data.network.MyAPIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 通知 ViewModel
 *
 * @param apiRepository
 */
@HiltViewModel
class NoticeViewModel @Inject constructor(private val apiRepository: MyAPIRepository) :
    ViewModel() {

    /**
     * 应用更新
     */
    val updateApp = MutableLiveData<AppNotice>()

    /**
     * 更新校验
     */
    fun check() {
        viewModelScope.launch {
            updateApp.postValue(AppNotice(id = -1))
            try {
                val data = apiRepository.getUpdateContent().data
                if (data != null) {
                    updateApp.postValue(data!!)
                } else {
                    updateApp.postValue(AppNotice(id = -2))
                }
            } catch (e: Exception) {
                updateApp.postValue(AppNotice(id = -2))
            }
        }
    }
}
