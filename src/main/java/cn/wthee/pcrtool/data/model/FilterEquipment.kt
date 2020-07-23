package cn.wthee.pcrtool.data.model

import com.google.gson.Gson

class FilterEquipment(
    var all: Boolean,
    var craft0: Boolean,
    var craft1: Boolean
) {
    fun initData() {
        this.all = true
        this.craft0 = true
        this.craft1 = true
    }

    //转化为 json 字符串
    fun toJsonString(): String = Gson().toJson(this)

    //是否有筛选项
    fun hasFilter() = hasCraftFilter()

    fun hasCraftFilter() = !(craft0 && craft1)
}