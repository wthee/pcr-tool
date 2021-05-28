package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.GuildData
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 活动 ViewModel
 *
 * @param unitRepository
 */
@HiltViewModel
class GuildViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    var guilds = MutableLiveData<List<GuildData>>()

    /**
     * 获取公会
     */
    fun getGuilds() {
        viewModelScope.launch {
            val data = unitRepository.getGuilds()
            guilds.postValue(data)
        }
    }

}
