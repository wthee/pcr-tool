package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.ViewModel
import cn.wthee.pcrtool.data.network.MyAPIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * 漫画 ViewModel
 */
@HiltViewModel
class ComicViewModel @Inject constructor(
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    /**
     * 漫画数据
     */
    fun getComic() = flow {
        val data = apiRepository.getComic().data
        data?.let {
            emit(it)
        }
    }
}