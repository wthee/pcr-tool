package cn.wthee.pcrtool.data.model

import com.google.gson.Gson

class FilterDataCharacter(
    var all: Boolean,
    var positon1: Boolean,
    var positon2: Boolean,
    var positon3: Boolean,
    var atkPhysical: Boolean,
    var atkMagic: Boolean
) {
    fun initData() {
        this.all = true
        this.positon1 = true
        this.positon2 = true
        this.positon3 = true
        this.atkPhysical = true
        this.atkMagic = true
    }

    //转化为 json 字符串
    fun toJsonString(): String = Gson().toJson(this)

    //是否有筛选项
    fun hasFilter() = hasPositionFilter() || hasAtkFilter()

    fun hasPositionFilter() = !(positon1 && positon2 && positon3)

    fun hasAtkFilter() = !(atkPhysical && atkMagic)
}