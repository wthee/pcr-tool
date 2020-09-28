package cn.wthee.pcrtool.data.model

import com.google.gson.Gson

class FilterEquipment(
    var all: Boolean,
    var type: String
) {
    fun initData() {
        this.type = "全部"
    }

    //转化为 json 字符串
    fun toJsonString(): String = Gson().toJson(this)

}