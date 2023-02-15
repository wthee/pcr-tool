package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.enums.KeywordType
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.utils.LogReportUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 接口 ViewModel
 */
@HiltViewModel
class CommonApiViewModel @Inject constructor(
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    /**
     * 获取关键词
     */
    fun getKeywords(keywordType: KeywordType) = flow {
        try {
            val data = apiRepository.getKeywords(keywordType.type).data ?: arrayListOf()
            emit(data)
        } catch (e: Exception) {
            LogReportUtil.upload(e, "getKeywords#keywordType:$keywordType")
        }
    }
}