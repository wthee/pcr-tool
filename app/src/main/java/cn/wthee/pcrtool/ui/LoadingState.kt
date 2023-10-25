package cn.wthee.pcrtool.ui


/**
 * 页面状态：共用
 */
enum class LoadingState {
    Loading,
    Success,
    NoData,
    Error;


    fun isSuccess(success: Boolean) = if (success) Success else this

    fun isError(error: Boolean) = if (error) Error else this

    fun isNoData(noData: Boolean) = if (noData) NoData else this
}

fun <T> updateLoadingState(list: List<T>?) = when {
    !list.isNullOrEmpty() -> LoadingState.Success
    list != null && list.isEmpty() -> LoadingState.NoData
    else -> LoadingState.Error
}