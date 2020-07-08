package cn.wthee.pcrtool.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.EnemyRepository
import cn.wthee.pcrtool.data.model.CharacterBasicInfo
import cn.wthee.pcrtool.data.model.EnemyData
import cn.wthee.pcrtool.utils.Constants.SORT_AGE
import cn.wthee.pcrtool.utils.Constants.SORT_HEIGHT
import cn.wthee.pcrtool.utils.Constants.SORT_POSITION
import cn.wthee.pcrtool.utils.Constants.SORT_WEIGHT
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EnemyViewModel @Inject constructor(
    private val repository: EnemyRepository
) : ViewModel() {

    var enemies = MutableLiveData<List<EnemyData>>()
    var enemyCount = MutableLiveData<Int>()
    var refresh = MutableLiveData<Boolean>()
    var loading = MutableLiveData<Boolean>()
    var reload = MutableLiveData<Boolean>()

    companion object {
        var repeat = mutableMapOf<Int, Int>()
    }


    //怪物基本资料
    fun getAllEnemy() {
        viewModelScope.launch {
            val data = repository.getAllEnemy()
//            if (data.isEmpty()) {
//                loading.postValue(true)
//            } else {
//                loading.postValue(false)
//                refresh.postValue(false)
//            }
            enemies.postValue(data)
        }
    }

    //怪物数量
    fun getEnemyCount() {
        viewModelScope.launch {
            enemyCount.postValue(repository.getEnemyCount())
        }
    }


    private fun getSort(sortType: Int, asc: Boolean): java.util.Comparator<CharacterBasicInfo> {
        return Comparator { o1: CharacterBasicInfo, o2: CharacterBasicInfo ->
            val a: Int
            val b: Int
            when (sortType) {
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
