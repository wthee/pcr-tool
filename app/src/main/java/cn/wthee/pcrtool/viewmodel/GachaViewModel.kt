package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import cn.wthee.pcrtool.data.view.GachaInfo
import kotlinx.coroutines.launch

/**
 * 卡池 ViewModel
 *
 * 数据来源 [GachaRepository]
 */
class GachaViewModel(
    private val repository: GachaRepository
) : ViewModel() {

    var gachas = MutableLiveData<List<GachaInfo>>()


    /**
     * 获取卡池记录
     */
    fun getGachaHistory() {
        viewModelScope.launch {
            val data = repository.getGachaHistory()
            gachas.postValue(data)
        }
    }

}
