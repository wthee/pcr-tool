package cn.wthee.pcrtool.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.wthee.pcrtool.data.CharacterRepository

class CharacterViewModelFactory(
    private val repository: CharacterRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CharacterViewModel(repository) as T
    }
}