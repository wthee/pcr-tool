package cn.wthee.pcrtool.ui.tool.gacha

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import cn.wthee.pcrtool.data.db.view.GachaInfo
import kotlinx.coroutines.launch


class GachaViewModel(
    private val repository: GachaRepository
) : ViewModel() {

    var gachas = MutableLiveData<List<GachaInfo>>()


    //卡池信息
    fun getGachaHistory() {
        viewModelScope.launch {
            val data = repository.getGachaHistory()
            gachas.postValue(data)
        }
    }

}
