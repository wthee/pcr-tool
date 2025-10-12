package cn.wthee.pcrtool.data.db.view.skilltype

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.data.enums.DotType
import cn.wthee.pcrtool.utils.getString
import cn.wthee.pcrtool.utils.getTarget
import cn.wthee.pcrtool.utils.getValueText

//110：持续伤害易伤
fun SkillActionDetail.dotUp(): String {
    val value = getValueText(1, actionValue1, actionValue2)
    //影响
    val effectTypeList = if (actionValue3.toInt() == -1) {
        arrayListOf()
    } else {
        arrayListOf(actionValue3, actionValue4, actionValue5, actionValue6)
    }

    //获取异常状态名称
    val effectTypeNameStr = if (effectTypeList.isEmpty()) {
        ""
    } else {
        val effectTypeNameList = arrayListOf<String>()
        for (type in effectTypeList) {
            //-1 后面的忽略
            if (type.toInt() == -1) {
                break
            }
            //获取名称
            effectTypeNameList.add(getString(DotType.getByType(type.toInt()).typeNameId))
        }
        val effectTypeNameStr = effectTypeNameList.distinct().joinToString("、")
        "⌈$effectTypeNameStr⌋"
    }
    //倍率
    val multiple = (1 + actionValue1 / 100).toInt()

    //上线
    val limit =
        getString(R.string.skill_action_damage_limit_int, actionValue7.toInt())
    return getString(
        R.string.skill_action_type_desc_110,
        getTarget(),
        effectTypeNameStr,
        multiple,
        limit
    )
}