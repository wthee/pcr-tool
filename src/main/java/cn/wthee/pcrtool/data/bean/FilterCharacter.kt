package cn.wthee.pcrtool.data.bean

import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.DataStoreRead
import cn.wthee.pcrtool.utils.DataStoreUtil
import com.google.gson.Gson
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.Serializable

/**
 * 角色信息筛选
 *
 * fixme starIds获取问题
 */
class FilterCharacter(
    var all: Boolean,
    var positon: Int,
    var atk: Int,
    var r6: Boolean,
    var guild: String
) : Serializable {
    var starIds = arrayListOf<Int>()
        set(value) {
            val list = arrayListOf<Int>()
            value.forEach {
                list.add(it.toInt())
            }
            field = list
        }

    fun addOrRemove(vararg id: Int) {
        val list = starIds
        id.forEach {
            if (list.contains(it)) {
                list.remove(it)
            } else {
                list.add(it)
            }
        }
        //保存
        MainScope().launch {
            DataStoreUtil.save(Constants.SP_STAR_CHARACTER, Gson().toJson(list))
            DataStoreUtil.get(Constants.SP_STAR_CHARACTER, object : DataStoreRead {
                override fun read(str: String?) {
                    val newStarIds = DataStoreUtil.fromJson<ArrayList<Int>>(str)
                    starIds = newStarIds ?: arrayListOf()
                }
            })
        }
    }


    fun initData() {
        this.all = true
        this.positon = 0
        this.atk = 0
        this.r6 = false
        this.guild = "全部"
    }

    fun position() = when (this.positon) {
        1 -> arrayListOf(0, 299)
        2 -> arrayListOf(300, 599)
        3 -> arrayListOf(600, 9999)
        else -> arrayListOf(0, 9999)
    }

}