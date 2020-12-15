package cn.wthee.pcrtool.data.network.model

import java.io.Serializable

class ResponseData<T>(
    var status: Int = -1,
    var data: T?,
    var message: String
) : Serializable {


}

fun <T> error(): ResponseData<T> = ResponseData(-1, null, "未正常获取数据，请重新查询~")

fun <T> cancel(): ResponseData<T> = ResponseData(-2, null, "查询取消~")

