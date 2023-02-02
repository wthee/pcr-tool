package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import cn.wthee.pcrtool.ui.common.DateRange
import cn.wthee.pcrtool.utils.LogReportUtil
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
    fun getGachaHistory(dateRange: DateRange) = flow {
        try {
            var list = gachaRepository.getGachaHistory(Int.MAX_VALUE)
            if (dateRange.hasFilter()) {
                list = list.filter {
                    dateRange.predicate(it.startTime)
                }
            }

            emit(list.sortedWith(compareGacha()))
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getGachaHistory:${dateRange}")
        }
    }

    /**
     * 获取卡池fes角色
     */
    fun getGachaFesUnitList() = flow {
        try {
            emit(gachaRepository.getFesUnitIds().unitIds.intArrayList)
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getGachaFesUnitList")
        }
    }
}
