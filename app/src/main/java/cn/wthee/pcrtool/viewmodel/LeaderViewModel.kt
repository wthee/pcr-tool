package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.model.LeaderData
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 角色排行 ViewModel
 *
 * 数据来源 [MyAPIRepository]
 */
@HiltViewModel
class LeaderViewModel @Inject constructor() : ViewModel() {

    val leaderData = MutableLiveData<ResponseData<LeaderData>>()

    fun getLeader() {
        viewModelScope.launch {
            val data = MyAPIRepository.getInstance().getLeader()
            leaderData.postValue(data)
        }
    }

}
