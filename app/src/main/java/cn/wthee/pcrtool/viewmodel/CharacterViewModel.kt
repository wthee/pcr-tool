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
import cn.wthee.pcrtool.utils.UMengLogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 角色 ViewModel
 *
 * @param unitRepository
 */
@HiltViewModel
class CharacterViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    lateinit var characters: Flow<PagingData<CharacterInfo>>
    var characterList = MutableLiveData<List<CharacterInfo>>()
    var character = MutableLiveData<CharacterInfoPro>()
    var allPvpCharacterData = MutableLiveData<List<PvpCharacterData>>()
    var selectPvpCharacterData = MutableLiveData<List<PvpCharacterData>>()
    var guilds = MutableLiveData<List<GuildData>>()

    /**
     * 获取角色基本信息列表
     *
     * @param params 角色筛选
     */
    fun getCharacters(params: FilterCharacter) {
        viewModelScope.launch {
            val guildName = if (params.guild > 0)
                unitRepository.getGuilds()[params.guild - 1].guildName
            else
                "全部"
            val data = unitRepository.getInfoAndData(params, guildName)
            characterList.postValue(data)
        }
    }


    /**
     * 获取角色基本资料
     *
     * @param unitId 角色编号
     */
    fun getCharacter(unitId: Int) {
        viewModelScope.launch {
            val data = unitRepository.getInfoPro(unitId)
            data?.let {
                character.postValue(it)
            }
            if (data == null) {
                UMengLogUtil.upload(
                    NullPointerException(),
                    Constants.EXCEPTION_UNIT_NULL + "unit_id:$unitId"
                )
            }
        }
    }

    /**
     * 竞技场角色信息
     */
    fun getAllCharacter() {
        viewModelScope.launch {
            val data = unitRepository.getCharacterByPosition(0, 999)
            allPvpCharacterData.postValue(data)
        }
    }

    /**
     * 公会信息
     */
    fun getGuilds() {
        viewModelScope.launch {
            val data = unitRepository.getGuilds()
            guilds.postValue(data)
        }
    }

    /**
     * 角色站位
     */
    suspend fun getPvpCharacterByIds(ids: ArrayList<Int>) = unitRepository.getCharacterByIds(ids)
}
