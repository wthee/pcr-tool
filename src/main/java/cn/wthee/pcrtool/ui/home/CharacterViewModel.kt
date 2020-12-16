package cn.wthee.pcrtool.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import cn.wthee.pcrtool.data.db.repository.CharacterRepository
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.CharacterInfoPro
import cn.wthee.pcrtool.enums.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class CharacterViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    lateinit var characters: Flow<PagingData<CharacterInfo>>
    var characterCount = MutableLiveData<Int>()
    var character = MutableLiveData<CharacterInfoPro>()
    var reset = MutableLiveData<Boolean>()
    var updateCharacter = MutableLiveData<Boolean>()
    var reload = MutableLiveData<Boolean>()

    //角色基本资料
    fun getCharacters(sortType: SortType, asc: Boolean, name: String) {
        viewModelScope.launch {
            characters = Pager(
                PagingConfig(
                    pageSize = 10,
                    enablePlaceholders = false
                )
            ) {
                repository.getInfoAndData(
                    sortType,
                    asc,
                    name,
                    CharacterListFragment.characterFilterParams
                )
            }.flow
            //角色数量
            characterCount.postValue(
                repository.getInfoAndDataCount(name, CharacterListFragment.characterFilterParams)
            )
            updateCharacter.postValue(true)
        }
    }

    //角色基本资料
    fun getCharacter(uid: Int) {
        viewModelScope.launch {
            val data = repository.getInfoPro(uid)
            character.postValue(data)
        }
    }

    suspend fun getCharacterData(uid: Int) = repository.getInfoPro(uid)

    //角色基本资料
    suspend fun getCharacterByPosition(positionType: Int) = when (positionType) {
        1 -> repository.getCharacterByPosition(0, 299)
        2 -> repository.getCharacterByPosition(300, 599)
        3 -> repository.getCharacterByPosition(600, 999)
        else -> repository.getCharacterByPosition(0, 999)
    }

    //升级经验列表
    suspend fun getR6Ids() = repository.getR6Ids()

    //公会信息
    suspend fun getGuilds() = repository.getGuilds()

    //掉落信息
    suspend fun getDrops(uid: Int) = repository.getItemDropInfos(uid)
}
