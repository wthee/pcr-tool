package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.utils.getString
import kotlinx.serialization.Serializable

/**
 * 通用接口返回数据
 */
@Serializable
data class ResponseData<T>(
    var status: Int = 1,
    var data: T? = null,
    var message: String = ""
)

/**
 * 错误
 */
inline fun <reified T> error(): ResponseData<T> =
    ResponseData(-1, null, getString(R.string.response_error))

/**
 * 中断
 */
inline fun <reified T> cancel(): ResponseData<T> =
    ResponseData(-2, null, getString(R.string.response_cancel))

