package cn.wthee.pcrtool.ui.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.EnemyRepository
import cn.wthee.pcrtool.data.model.EnemyData
import kotlinx.coroutines.launch


class EnemyViewModel(
    private val repository: EnemyRepository
) : ViewModel() {

    var enemies = MutableLiveData<List<EnemyData>>()
    var enemyCount = MutableLiveData<Int>()
    var refresh = MutableLiveData<Boolean>()
    var isLoading = MutableLiveData<Boolean>()


    //怪物基本资料
    fun getAllEnemy() {
        isLoading.postValue(true)
        viewModelScope.launch {
            val data = repository.getAllEnemy()
            refresh.postValue(false)
            isLoading.postValue(false)
            enemies.postValue(data)
        }
    }

    //怪物数量
    fun getEnemyCount() {
        viewModelScope.launch {
            enemyCount.postValue(repository.getEnemyCount())
        }
    }

}
