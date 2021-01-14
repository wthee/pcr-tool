package cn.wthee.pcrtool.ui.tool.leader

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.data.network.model.LeaderData
import cn.wthee.pcrtool.data.network.model.ResponseData
import kotlinx.coroutines.launch


class LeaderViewModel : ViewModel() {

    val leaderData = MutableLiveData<ResponseData<LeaderData>>()

    fun getLeader() {
        viewModelScope.launch {
            val data = MyAPIRepository.getLeader()
            leaderData.postValue(data)
        }
    }

}