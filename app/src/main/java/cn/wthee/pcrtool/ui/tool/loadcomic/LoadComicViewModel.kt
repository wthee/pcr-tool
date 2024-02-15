package cn.wthee.pcrtool.ui.tool.loadcomic

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.wthee.pcrtool.data.network.ApiRepository
import cn.wthee.pcrtool.ui.LoadState
import cn.wthee.pcrtool.ui.updateLoadState
import cn.wthee.pcrtool.utils.ImageRequestHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * 页面状态：过场漫画
 */
@Immutable
data class LoadComicUiState(
    val comicList: ArrayList<String>? = null,
    val loadState: LoadState = LoadState.Loading
)

/**
 * 过场漫画 ViewModel
 */
@HiltViewModel
class LoadComicViewModel @Inject constructor(
    private val apiRepository: ApiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoadComicUiState())
    val uiState: StateFlow<LoadComicUiState> = _uiState.asStateFlow()

    init {
        getComicList()
    }

    /**
     * 漫画数据
     */
    private fun getComicList() {
        viewModelScope.launch {
            val responseData = apiRepository.getLoadComicList()
            responseData.data.let { data ->
                if (data != null) {
                    val list = arrayListOf<String>()
                    data.forEach {
                        list.add(ImageRequestHelper.getInstance().getResourcePrefixUrl() + it)
                    }

                    _uiState.update {
                        it.copy(
                            comicList = list,
                            loadState = updateLoadState(list)
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            loadState = LoadState.Error
                        )
                    }
                }
            }
        }
    }
}