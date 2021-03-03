package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import cn.wthee.pcrtool.data.db.repository.CharacterRepository
import cn.wthee.pcrtool.data.enums.SortType
import cn.wthee.pcrtool.data.model.FilterCharacter
import cn.wthee.pcrtool.data.view.CharacterInfo
import cn.wthee.pcrtool.data.view.CharacterInfoPro
import cn.wthee.pcrtool.data.view.PvpCharacterData
import cn.wthee.pcrtool.ui.home.CharacterListFragment
import cn.wthee.pcrtool.utils.Constants
import com.umeng.umcrash.UMCrash
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * 角色 ViewModel
 *
 * 数据来源 [CharacterRepository]
 */
class CharacterViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    lateinit var characters: Flow<PagingData<CharacterInfo>>
    var characterCount = MutableLiveData<Int>()
    var character = MutableLiveData<CharacterInfoPro>()
    var reset = MutableLiveData<Boolean>()
    var updateCharacter = MutableLiveData<Boolean>()
    var allPvpCharacterData = MutableLiveData<List<PvpCharacterData>>()

    /**
     * 角色基本资料 [CharacterInfo]
     */
    fun getCharacters(
        params: FilterCharacter,
        sortType: SortType,
        asc: Boolean,
        name: String,
        reload: Boolean = true
    ) {
        viewModelScope.launch {
            if (!this@CharacterViewModel::characters.isInitialized || reload) {
                characters = Pager(
                    PagingConfig(
                        pageSize = Int.MAX_VALUE,
                        initialLoadSize = Int.MAX_VALUE,
                        enablePlaceholders = false
                    )
                ) {
                    repository.getInfoAndData(
                        sortType,
                        asc,
                        name,
                        params
                    )
                }.flow
            }
            //角色数量
            characterCount.postValue(
                repository.getInfoAndDataCount(name, CharacterListFragment.characterFilterParams)
            )
            updateCharacter.postValue(true)
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

    /**
     * 六星 id 列表
     */
    suspend fun getR6Ids() = repository.getR6Ids()

    /**
     * 公会信息
     */
    suspend fun getGuilds() = repository.getGuilds()

    /**
     * 角色碎片掉落信息
     */
    suspend fun getDrops(uid: Int) = repository.getItemDropInfos(uid)
}
