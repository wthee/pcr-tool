package cn.wthee.pcrtool.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.GachaRepository
import cn.wthee.pcrtool.data.db.view.GachaUnitInfo
import cn.wthee.pcrtool.data.model.UnitsInGacha
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
            emit(gachaRepository.getGachaHistory(Int.MAX_VALUE))
        } catch (e: Exception) {

        }
    }

    /**
     * 获取卡池角色
     */
    fun getGachaUnits() = flow {
        try {
            val fesUnitInfo = gachaRepository.getFesUnitIds()
            val fesList = arrayListOf<GachaUnitInfo>()
            fesUnitInfo.getIds().forEachIndexed { index, i ->
                fesList.add(
                    GachaUnitInfo(
                        unitId = i,
                        unitName = fesUnitInfo.getNames()[index],
                        isLimited = 1,
                        rarity = 3
                    )
                )
            }

            val units = UnitsInGacha(
                gachaRepository.getGachaUnits(1),
                gachaRepository.getGachaUnits(2),
                gachaRepository.getGachaUnits(3),
                gachaRepository.getGachaUnits(4).filter {
                    !fesUnitInfo.getIds().contains(it.unitId)
                },
                fesList
            )
            emit(units)
        } catch (e: Exception) {
            Log.e("DEBUG", e.message ?: "")
        }
    }

}
