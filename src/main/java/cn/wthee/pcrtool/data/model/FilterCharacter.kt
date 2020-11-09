package cn.wthee.pcrtool.data.model

import com.google.gson.Gson

class FilterCharacter(
    var all: Boolean,
    var positon: Int,
    var atk: Int,
    var guild: String
) {
    fun initData() {
        this.positon = 0
        this.atk = 0
        this.guild = "全部"
    }

    //转化为 json 字符串
    fun toJsonString(): String = Gson().toJson(this)

    fun getPositon() = when (this.positon) {
        1 -> arrayListOf(0, 299)
        2 -> arrayListOf(300, 599)
        3 -> arrayListOf(600, 9999)
        else -> arrayListOf(0, 9999)
    }
}