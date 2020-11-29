package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.enums.Response
import java.io.Serializable

class ResultData<T>(
    val status: Response,
    val data: T,
    val message: String
) : Serializable {

    constructor(status: Response, data: T) : this(status, data, "")
}