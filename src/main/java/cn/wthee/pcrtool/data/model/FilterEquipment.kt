package cn.wthee.pcrtool.data.model

import com.google.gson.Gson

class FilterEquipment(
    var all: Boolean,
    var craft: Int
) {
    fun initData() {
        this.all = true
        this.craft = 0
    }

    //转化为 json 字符串
    fun toJsonString(): String = Gson().toJson(this)

}