package cn.wthee.pcrtool.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.wthee.pcrtool.data.EnemyRepository

class EnemyViewModelFactory(
    private val repository: EnemyRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EnemyViewModel(repository) as T
    }
}