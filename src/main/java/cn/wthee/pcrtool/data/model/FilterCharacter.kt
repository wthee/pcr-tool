package cn.wthee.pcrtool.data.model

import com.google.gson.Gson

class FilterDataCharacter(
    var all: Boolean,
    var positon1: Boolean,
    var positon2: Boolean,
    var positon3: Boolean
) {
    fun initData() {
        this.all = true
        this.positon1 = true
        this.positon2 = true
        this.positon3 = true
    }

    //转化为 json 字符串
    fun toJsonString(): String = Gson().toJson(this)

    //是否有筛选项
    fun hasFilter() = !(positon1 && positon2 && positon3)

    fun hasPositionFilter() = !(positon1 && positon2 && positon3)
}