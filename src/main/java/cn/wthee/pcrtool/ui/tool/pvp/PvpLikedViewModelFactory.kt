package cn.wthee.pcrtool.ui.tool.pvp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.wthee.pcrtool.data.db.repository.PvpRepository

class PvpLikedViewModelFactory(
    private val repository: PvpRepository
) : ViewModelProvider.NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PvpLikedViewModel(
            repository
        ) as T
    }
}