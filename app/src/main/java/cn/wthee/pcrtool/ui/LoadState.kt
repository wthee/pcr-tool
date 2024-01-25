package cn.wthee.pcrtool.ui


/**
 * 页面状态：共用
 */
enum class LoadState {
    Loading,
    Success,
    NoData,
    Error;

    fun isSuccess(success: Boolean) = if (success) Success else Error

    fun isError(error: Boolean) = if (error) Error else this

    fun isNoData(noData: Boolean) = if (noData) NoData else this
}

fun <T> updateLoadState(list: List<T>?) = when {
    !list.isNullOrEmpty() -> LoadState.Success
    list != null && list.isEmpty() -> LoadState.NoData
    else -> LoadState.Error
}