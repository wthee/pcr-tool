package cn.wthee.pcrtool.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import cn.wthee.pcrtool.data.db.entity.ComicData
import cn.wthee.pcrtool.data.network.MyAPIRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 漫画 ViewModel
 */
@HiltViewModel
@ExperimentalPagingApi
class ComicViewModel @Inject constructor(
    private val apiRepository: MyAPIRepository
) : ViewModel() {

    var comic = MutableLiveData<List<ComicData>>()

    /**
     * 漫画数据
     */
    fun getComic() {
        viewModelScope.launch {
            if (comic.value == null || comic.value!!.isEmpty()) {
                val data = apiRepository.getComic().data ?: arrayListOf()
                comic.postValue(data)
            }
        }
    }
}