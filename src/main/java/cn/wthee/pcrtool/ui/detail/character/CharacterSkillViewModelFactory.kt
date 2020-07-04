package cn.wthee.pcrtool.ui.detail.character

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.wthee.pcrtool.data.CharacterRepository

class CharacterSkillViewModelFactory(
    private val repository: CharacterRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CharacterSkillViewModel(repository) as T
    }
}