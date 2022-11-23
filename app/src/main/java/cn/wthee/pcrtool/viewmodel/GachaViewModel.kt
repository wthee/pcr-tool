package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import cn.wthee.pcrtool.utils.compareGacha
import cn.wthee.pcrtool.utils.intArrayList
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
        try {
            emit(gachaRepository.getGachaHistory(Int.MAX_VALUE).sortedWith(compareGacha()))
        } catch (_: Exception) {

        }
    }
    /**
     * 获取卡池fes角色
     */
    fun getGachaFesUnitList() = flow {
        try {
            emit(gachaRepository.getFesUnitIds().unitIds.intArrayList)
        } catch (_: Exception) {

        }
    }
}
