package cn.wthee.pcrtool.ui


/**
 * 页面状态：共用
 */
enum class LoadingState {
    Loading,
    Success,
    NoData,
    Error
}

fun <T> updateLoadingState(list: List<T>?) = when {
    !list.isNullOrEmpty() -> LoadingState.Success
    list != null && list.isEmpty() -> LoadingState.NoData
    else -> LoadingState.Error
}