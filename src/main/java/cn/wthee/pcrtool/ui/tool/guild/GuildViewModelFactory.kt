package cn.wthee.pcrtool.ui.tool.guild

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.wthee.pcrtool.data.db.repository.CharacterRepository

class GuildViewModelFactory(
    private val repository: CharacterRepository
) : ViewModelProvider.NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GuildViewModel(
            repository
        ) as T
    }
}