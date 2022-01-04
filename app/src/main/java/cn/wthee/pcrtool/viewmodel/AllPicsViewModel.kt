package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
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
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    /**
     * 立绘数据
     */
    fun getStoryList(id: Int) = flow {
        val firstNum = id.toString().substring(0, 1).toInt()
        val data = apiRepository.getStoryList(id).data
        data?.let {
            if (firstNum == 1) {
                val picUrls =
                    ImageResourceHelper.getInstance().getAllPicUrl(id)
                val list = arrayListOf<String>()
                list.addAll(picUrls)
                list.addAll(getStoryUrls(it, ImageResourceHelper.CARD_STORY))
                emit(list)
            } else {
                emit(getStoryUrls(it, ImageResourceHelper.EVENT_STORY))
            }
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