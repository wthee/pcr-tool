package cn.wthee.pcrtool.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.CharacterRepository
import cn.wthee.pcrtool.database.view.CharacterInfo
import cn.wthee.pcrtool.database.view.CharacterInfoPro
import cn.wthee.pcrtool.database.view.getDefault
import cn.wthee.pcrtool.utils.Constants.SORT_AGE
import cn.wthee.pcrtool.utils.Constants.SORT_DATE
import cn.wthee.pcrtool.utils.Constants.SORT_HEIGHT
import cn.wthee.pcrtool.utils.Constants.SORT_POSITION
import cn.wthee.pcrtool.utils.Constants.SORT_WEIGHT
import kotlinx.coroutines.launch


class CharacterViewModel(
    private val repository: CharacterRepository
) : ViewModel() {

    var characters = MutableLiveData<List<CharacterInfo>>()
    var character = MutableLiveData<CharacterInfoPro>()
    var refresh = MutableLiveData<Boolean>()
    var isLoading = MutableLiveData<Boolean>()
    var reload = MutableLiveData<Boolean>()

    //角色基本资料
    fun getCharacters(sortType: Int, asc: Boolean, name: String) {
        isLoading.postValue(true)
        viewModelScope.launch {
            val data = repository.getInfoAndData(name)
                .sortedWith(getSort(sortType, asc))
            characters.postValue(data)
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
        else -> getDefault()
    }

    //公会信息
    suspend fun getGuilds() = repository.getGuilds()

    //升级经验列表
    suspend fun getLevelExp() = repository.getLevelExp()

    //角色排序
    private fun getSort(sortType: Int, asc: Boolean): java.util.Comparator<CharacterInfo> {
        return Comparator { o1: CharacterInfo, o2: CharacterInfo ->
            val a: Int
            val b: Int
            when (sortType) {
                SORT_DATE -> {
                    a = o1.startTime.toInt()
                    b = o2.startTime.toInt()
                }
                SORT_AGE -> {
                    a = if (o1.age.contains("?")) 999 else o1.age.toInt()
                    b = if (o2.age.contains("?")) 999 else o2.age.toInt()
                }
                SORT_HEIGHT -> {
                    a = if (o1.height.contains("?")) 999 else o1.height.toInt()
                    b = if (o2.height.contains("?")) 999 else o2.height.toInt()
                }
                SORT_WEIGHT -> {
                    a = if (o1.weight.contains("?")) 999 else o1.weight.toInt()
                    b = if (o2.weight.contains("?")) 999 else o2.weight.toInt()
                }
                SORT_POSITION -> {
                    a = o1.position
                    b = o2.position
                }
                else -> {
                    a = o1.id
                    b = o2.id
                }
            }
            (if (asc) -1 else 1) * b.compareTo(a)
        }
    }
}
