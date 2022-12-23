package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.network.MyAPIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 角色排行 ViewModel
 *
 * @param apiRepository
 */
@HiltViewModel
class LeaderViewModel @Inject constructor(
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    /**
     * 获取排行
     */
    fun getLeader(sort: Int, asc: Boolean) = flow {
        val data = apiRepository.getLeader(sort, asc)
        emit(data)
    }

    /**
     * 获取排行评级
     */
    fun getLeaderTier(type: Int) = flow {
        val data = apiRepository.getLeaderTier(type)
        emit(data)
    }
}
