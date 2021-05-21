package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import cn.wthee.pcrtool.data.db.entity.GuildData
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.CharacterInfoPro
import cn.wthee.pcrtool.data.db.view.PvpCharacterData
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.utils.Constants
import com.umeng.umcrash.UMCrash
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 角色 ViewModel
 *
 * 数据来源 [UnitRepository]
 */
@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val repository: UnitRepository
) : ViewModel() {

    lateinit var characters: Flow<PagingData<CharacterInfo>>
    var characterList = MutableLiveData<List<CharacterInfo>>()
    var character = MutableLiveData<CharacterInfoPro>()
    var allPvpCharacterData = MutableLiveData<List<PvpCharacterData>>()
    var selectPvpCharacterData = MutableLiveData<List<PvpCharacterData>>()
    var guilds = MutableLiveData<List<GuildData>>()

    /**
     * 角色基本资料 [CharacterInfo]
     */
    fun getCharacters(params: FilterCharacter) {
        viewModelScope.launch {
            val guildName = if (params.guild > 0)
                repository.getGuilds()[params.guild - 1].guildName
            else
                "全部"
            val data = repository.getInfoAndData(params, guildName)
            characterList.postValue(data)
        }
    }


    /**
     * 角色基本资料 [CharacterInfoPro]
     */
    fun getCharacter(uid: Int) {
        viewModelScope.launch {
            val data = repository.getInfoPro(uid)
            data?.let {
                character.postValue(it)
            }
            if (data == null) {
                MainScope().launch {
                    UMCrash.generateCustomLog(
                        NullPointerException(),
                        Constants.EXCEPTION_UNIT_NULL + "unit_id:$uid"
                    )
                }
            }
        }
    }

    /**
     * 竞技场角色信息
     */
    fun getAllCharacter() {
        viewModelScope.launch {
            val data = repository.getCharacterByPosition(0, 999)
            allPvpCharacterData.postValue(data)
        }
    }

    /**
     * 六星 id 列表
     */
    suspend fun getR6Ids() = repository.getR6Ids()

    /**
     * 公会信息
     */
    fun getGuilds() {
        viewModelScope.launch {
            val data = repository.getGuilds()
            guilds.postValue(data)
        }
    }

    /**
     * 角色碎片掉落信息
     */
    suspend fun getDrops(uid: Int) = repository.getItemDropInfos(uid)

    /**
     * 角色站位
     */
    suspend fun getPvpCharacterByIds(ids: ArrayList<Int>) = repository.getCharacterByIds(ids)
}
