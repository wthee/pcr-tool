package cn.wthee.pcrtool.ui.tool.gacha

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cn.wthee.pcrtool.data.GachaRepository
import cn.wthee.pcrtool.ui.tool.enemy.GachaViewModel

class GachaViewModelFactory(
    private val repository: GachaRepository
) : ViewModelProvider.NewInstanceFactory() {


    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GachaViewModel(
            repository
        ) as T
    }
}