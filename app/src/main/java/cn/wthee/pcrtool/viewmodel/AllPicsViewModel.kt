package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.model.ResponseData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.utils.ImageRequestHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 角色图片 ViewModel
 */
@HiltViewModel
class AllPicsViewModel @Inject constructor(
    private val apiRepository: MyAPIRepository,
    private val unitRepository: UnitRepository,
) : ViewModel() {

    /**
     * 角色立绘数据
     */
    fun getUniCardList(id: Int) = flow {
        val actualId = unitRepository.getActualId(id)
        val picUrls =
            ImageRequestHelper.getInstance().getAllPicUrl(id, actualId)
        val list = arrayListOf<String>()
        list.addAll(picUrls)
        emit(list)
    }

    /**
     * 剧情立绘数据
     */
    fun getStoryList(id: Int, type: Int) = flow {
        val newResponse = ResponseData<ArrayList<String>>()
        val responseData = apiRepository.getStoryList(id)
        responseData.data?.let {
            val pathName = if (type == 0) {
                ImageRequestHelper.CARD_STORY
            } else {
                ImageRequestHelper.EVENT_STORY
            }
            newResponse.data = getStoryUrls(it, pathName)
        }
        newResponse.status = responseData.status
        newResponse.message = responseData.message
        emit(newResponse)
    }

    /**
     * 获取立绘信息
     */
    private fun getStoryUrls(idStr: String, path: String): ArrayList<String> {
        val list = arrayListOf<String>()
        idStr.split(",").sortedBy { it }.forEach {
            if (it != "") {
                list.add(ImageRequestHelper.getInstance().getUrl(path, it))
            }
        }
        return list
    }
}