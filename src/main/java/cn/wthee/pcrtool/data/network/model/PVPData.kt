package cn.wthee.pcrtool.data.network.model

import java.io.Serializable


data class PvpData(
    val atk: List<Atk>,
    val comment: Any,
    val def: List<Def>,
    val disliked: Boolean,
    val down: Int,
    val group: Boolean,
    val id: String,
    val iseditor: Boolean,
    val liked: Boolean,
    val `private`: Boolean,
    val up: Int,
    val updated: String
) : Serializable {

    fun getAtkIdList(): List<Int> {
        val ids = arrayListOf<Int>()
        atk.forEach {
            ids.add(it.id)
        }
        return ids
    }

    fun getAtkIdStr(): String {
        var ids = ""
        atk.forEach {
            ids += "${it.id}-"
        }
        return ids
    }

    fun getDefIdStr(): String {
        var ids = ""
        def.forEach {
            ids += "${it.id}-"
        }
        return ids
    }

}

data class Atk(
    val equip: Boolean,
    val id: Int,
    val star: Int
) : Serializable

data class Def(
    val equip: Boolean,
    val id: Int,
    val star: Int
) : Serializable