package cn.wthee.pcrtool.data.model

import com.google.gson.Gson

class FilterCharacter(
    var all: Boolean,
    var positon: Int,
    var atk: Int,
    var guild: String
) {
    fun initData() {
        this.all = true
        this.positon = 0
        this.atk = 0
        this.guild = "全部"
    }

    //转化为 json 字符串
    fun toJsonString(): String = Gson().toJson(this)

}