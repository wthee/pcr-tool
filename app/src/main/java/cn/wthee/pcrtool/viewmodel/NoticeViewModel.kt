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
     * 数据更新
     */
    val updateDb = MutableLiveData<String>()

    /**
     * 更新校验
     */
    fun check() {
        viewModelScope.launch {
            updateApp.postValue(AppNotice(id = -1))
            try {
                val data = apiRepository.getUpdateContent().data ?: AppNotice(id = -2)
                updateApp.postValue(data)
            } catch (e: Exception) {
                updateApp.postValue(AppNotice(id = -2))
            }
        }
    }

    /**
     * 更新校验
     */
    fun getDbDiff() {
        viewModelScope.launch {
            try {
                val data = apiRepository.getDbDiff().data ?: ""
                updateDb.postValue(data)
            } catch (e: Exception) {
                updateDb.postValue("")
            }
        }
    }
}
