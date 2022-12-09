package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.utils.getString
import java.io.Serializable

/**
 * 通用接口返回数据
 */
class ResponseData<T>(
    var status: Int = 1,
    var data: T? = null,
    var message: String = ""
) : Serializable

/**
 * 错误
 */
fun <T> error(): ResponseData<T> = ResponseData(-1, null, getString(R.string.respon_error))

/**
 * 中断
 */
fun <T> cancel(): ResponseData<T> = ResponseData(-2, null, getString(R.string.respon_cancel))

