package cn.wthee.pcrtool.ui.tool.guild

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.entity.GuildData
import cn.wthee.pcrtool.data.db.repository.CharacterRepository
import kotlinx.coroutines.launch

/**
 * 活动 ViewModel
 *
 * 数据来源 [CharacterRepository]
 */
class GuildViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    var guilds = MutableLiveData<List<GuildData>>()

    /**
     * 获取公会
     */
    fun getGuilds() {
        viewModelScope.launch {
            val data = repository.getGuilds()
            guilds.postValue(data)
        }
    }

}
