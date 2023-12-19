package cn.wthee.pcrtool.ui.media

import androidx.compose.runtime.Immutable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.db.repository.UnitRepository
import cn.wthee.pcrtool.data.enums.AllPicsType
import cn.wthee.pcrtool.data.network.MyAPIRepository
import cn.wthee.pcrtool.navigation.NavRoute
import cn.wthee.pcrtool.ui.LoadingState
import cn.wthee.pcrtool.ui.updateLoadingState
import cn.wthee.pcrtool.utils.ImageRequestHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：立绘列表
 */
@Immutable
data class PictureUiState(
    val unitCardList: ArrayList<String> = arrayListOf(),
    val storyCardList: ArrayList<String> = arrayListOf(),
    val comicList: ArrayList<String> = arrayListOf(),
    val storyLoadState: LoadingState = LoadingState.Loading,
    val comicLoadState: LoadingState = LoadingState.Loading,
    val pageCount: Int = 1
)

/**
 * 角色图片 ViewModel
 */
@HiltViewModel
class PictureViewModel @Inject constructor(
    private val apiRepository: MyAPIRepository,
    private val unitRepository: UnitRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    //角色或剧情id
    private val id: Int? = savedStateHandle[NavRoute.UNIT_ID]

    //类型
    private val type: Int? = savedStateHandle[NavRoute.ALL_PICS_TYPE]

    private val _uiState = MutableStateFlow(PictureUiState())
    val uiState: StateFlow<PictureUiState> = _uiState.asStateFlow()

    init {
        if (id != null && type != null) {
            //角色相关
            if (AllPicsType.getByValue(type) == AllPicsType.CHARACTER) {
                _uiState.update {
                    it.copy(
                        pageCount = 3
                    )
                }
                //角色立绘
                getUnitCardList(id)
                //1格漫画
                getComicList(id)
            }
            //剧情立绘
            getStoryCardList(id, type)

        }
    }

    /**
     * 角色立绘数据
     * @param id 角色iid
     */
    private fun getUnitCardList(id: Int) {
        viewModelScope.launch {
            val actualId = unitRepository.getActualId(id)
            val picUrls =
                ImageRequestHelper.getInstance().getAllPicUrl(id, actualId)
            val list = arrayListOf<String>()
            list.addAll(picUrls)
            _uiState.update {
                it.copy(
                    unitCardList = list
                )
            }
        }
    }

    /**
     * 剧情立绘数据
     *
     * @param id 剧情活动id
     */
    private fun getStoryCardList(id: Int, type: Int) {
        viewModelScope.launch {
            val responseData = apiRepository.getStoryList(id)
            responseData.data.let { data ->
                val pathName = if (type == 0) {
                    ImageRequestHelper.CARD_STORY
                } else {
                    ImageRequestHelper.EVENT_STORY
                }

                if (data != null) {
                    val list = getStoryUrls(data, pathName)
                    _uiState.update {
                        it.copy(
                            storyCardList = list,
                            storyLoadState = updateLoadingState(list)
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            storyLoadState = LoadingState.Error
                        )
                    }
                }
            }
        }
    }

    /**
     * 漫画数据
     *
     * @param id 角色id
     */
    private fun getComicList(id: Int) {
        viewModelScope.launch {
            val responseData = apiRepository.getComicType(id)
            responseData.data.let { data ->
                if (data != null) {
                    val url = ImageRequestHelper.getInstance().getComicUrl(id, data)
                    val list = arrayListOf<String>()
                    if (data != "") {
                        list.add(url)
                    }

                    _uiState.update {
                        it.copy(
                            comicList = list,
                            comicLoadState = updateLoadingState(list)
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            comicLoadState = LoadingState.Error
                        )
                    }
                }
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
                list.add(ImageRequestHelper.getInstance().getUrl(path, it))
            }
        }
        return list
    }
}