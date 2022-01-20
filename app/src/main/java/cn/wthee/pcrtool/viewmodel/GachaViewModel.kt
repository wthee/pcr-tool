package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 卡池 ViewModel
 *
 * @param gachaRepository
 */
@HiltViewModel
class GachaViewModel @Inject constructor(
    private val gachaRepository: GachaRepository
) : ViewModel() {

    /**
     * 获取卡池记录
     */
    fun getGachaHistory() = flow {
        emit(gachaRepository.getGachaHistory(Int.MAX_VALUE))
    }

}
