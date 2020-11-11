package cn.wthee.pcrtool.ui.tool.enemy

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.GachaRepository
import cn.wthee.pcrtool.data.view.GachaInfo
import kotlinx.coroutines.launch


class GachaViewModel(
    private val repository: GachaRepository
) : ViewModel() {

    var gachas = MutableLiveData<List<GachaInfo>>()
    var isLoading = MutableLiveData<Boolean>()


    //怪物基本资料
    fun getGachaHistory() {
        isLoading.postValue(true)
        viewModelScope.launch {
            val data = repository.getGachaHistory()
            isLoading.postValue(false)
            gachas.postValue(data)
        }
    }

}
