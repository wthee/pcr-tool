package cn.wthee.pcrtool.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import cn.wthee.pcrtool.data.CharacterRepository
import cn.wthee.pcrtool.database.view.CharacterInfo
import cn.wthee.pcrtool.database.view.CharacterInfoPro
import cn.wthee.pcrtool.enums.SortType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class CharacterViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    lateinit var characters: Flow<PagingData<CharacterInfo>>
    var characterCount = MutableLiveData<Int>()
    var character = MutableLiveData<CharacterInfoPro>()
    var refresh = MutableLiveData<Boolean>()
    var isLoading = MutableLiveData<Boolean>()
    var updateChatacter = MutableLiveData<Boolean>()
    var reload = MutableLiveData<Boolean>()

    //角色基本资料
    fun getCharacters(sortType: SortType, asc: Boolean, name: String) {
        isLoading.postValue(true)
        viewModelScope.launch {
            //TODO 收藏 DataStroe 获取 已收藏的角色 id 数据
            characters = Pager(
                PagingConfig(
                    pageSize = 50,
                    enablePlaceholders = false,
                    maxSize = 500
                )
            ) {
                repository.getInfoAndData(
                    sortType,
                    asc,
                    name,
                    CharacterListFragment.characterfilterParams
                )
            }.flow
            //角色数量
            characterCount.postValue(
                repository.getInfoAndDataCount(name, CharacterListFragment.characterfilterParams)
            )
            updateChatacter.postValue(true)
            isLoading.postValue(false)
        }
    }

    //角色基本资料
    fun getCharacter(uid: Int) {
        viewModelScope.launch {
            val data = repository.getInfoPro(uid)
            character.postValue(data)
        }
    }

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

    //升级经验列表
    suspend fun getLevelExp() = repository.getLevelExp()

}
