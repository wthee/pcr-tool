package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.entity.GuildData
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.view.CharacterInfo
import cn.wthee.pcrtool.data.view.CharacterInfoPro
import cn.wthee.pcrtool.data.view.PvpCharacterData
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
    var characterCount = MutableLiveData<Int>()
    var character = MutableLiveData<CharacterInfoPro>()
    var updateCharacter = MutableLiveData<Boolean>()
    var allPvpCharacterData = MutableLiveData<List<PvpCharacterData>>()
    var guilds = MutableLiveData<List<GuildData>>()
    var filter = MutableLiveData<FilterCharacter>()

    /**
     * 角色基本资料 [CharacterInfo]
     */
    fun getCharacters(params: FilterCharacter) {
        viewModelScope.launch {
            val data = repository.getInfoAndData(params)
            characterList.postValue(data)
        }
    }


    /**
     * 角色基本资料 [CharacterInfoPro]
     */
    fun getCharacter(uid: Int) {
        viewModelScope.launch {
            val data = repository.getInfoPro(uid)
            if (data == null) {
                MainScope().launch {
                    UMCrash.generateCustomLog(
                        NullPointerException(),
                        Constants.EXCEPTION_UNIT_NULL + "unit_id:$uid"
                    )
                }
            } else {
                character.postValue(data!!)
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

    suspend fun getAllPvp() = repository.getCharacterByPosition(0, 999)

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
}
