package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 召唤物 ViewModel
 *
 * @param unitRepository
 */
@HiltViewModel
class SummonViewModel @Inject constructor(
    private val unitRepository: UnitRepository
) : ViewModel() {

    /**
     * 获取召唤物基本信息
     */
    fun getSummonData(unitId: Int) = flow {
        emit(unitRepository.getSummonData(unitId))
    }

}
