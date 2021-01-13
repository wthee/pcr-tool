package cn.wthee.pcrtool.ui.tool.pvp

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.PvpLikedData
import cn.wthee.pcrtool.database.AppPvpDatabase
import kotlinx.coroutines.launch


class PvpLikedViewModel : ViewModel() {

    private val pvpDao = AppPvpDatabase.getInstance().getPvpDao()
    var allData = MutableLiveData<List<PvpLikedData>>()

    fun getLiked(region: Int) {
        viewModelScope.launch {
            val data = pvpDao.getAll(region)
            allData.postValue(data)
        }
    }


}
