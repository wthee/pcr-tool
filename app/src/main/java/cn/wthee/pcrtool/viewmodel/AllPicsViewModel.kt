package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.utils.ImageResourceHelper
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
            ImageResourceHelper.getInstance().getAllPicUrl(id, actualId)
        val list = arrayListOf<String>()
        list.addAll(picUrls)
        emit(list)
    }

    /**
     * 剧情立绘数据
     */
    fun getStoryList(id: Int, type: Int) = flow {
        val data = apiRepository.getStoryList(id).data
        data?.let {
            val pathName =
                if (type == 0) ImageResourceHelper.CARD_STORY else ImageResourceHelper.EVENT_STORY
            emit(getStoryUrls(it, pathName))
        }
    }

    /**
     * 获取立绘信息
     */
    private fun getStoryUrls(idStr: String, path: String): ArrayList<String> {
        val list = arrayListOf<String>()
        idStr.split(",").sortedBy { it }.forEach {
            if (it != "") {
                list.add(ImageResourceHelper.getInstance().getUrl(path, it))
            }
        }
        return list
    }
}