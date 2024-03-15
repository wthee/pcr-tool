package cn.wthee.pcrtool.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 初始化
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    init {
        getR6Ids()
    }

    /**
     * 六星 id 列表
     */
    fun getR6Ids() {
        viewModelScope.launch {
            val r6Ids = unitRepository.getR6Ids()

            if (r6Ids != null) {
                MainActivity.r6Ids = r6Ids
            }
        }
    }
}