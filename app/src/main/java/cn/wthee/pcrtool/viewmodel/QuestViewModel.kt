package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.QuestRepository
import cn.wthee.pcrtool.utils.LogReportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 主线地图 ViewModel
 *
 * @param questRepository
 */
@HiltViewModel
class QuestViewModel @Inject constructor(
    private val questRepository: QuestRepository
) : ViewModel() {

    /**
     * 获取装备掉落关卡信息
     *
     */
    fun getQuestList() = flow {
        try {
            val infos = questRepository.getEquipDropQuestList(0)
            emit(infos)
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getQuestList")
        }
    }
}
