package cn.wthee.pcrtool.data.bean

import cn.wthee.pcrtool.data.db.view.SkillActionLite
import cn.wthee.pcrtool.data.db.view.SkillActionPro

/**
 * 角色技能信息
 */
data class CharacterSkillInfo(
    val skillId: Int,
    val name: String,
    val desc: String,
    val icon_type: Int
) {
    //角色技能效果
    var actions = listOf<SkillActionPro>()

    /**
     * 获取技能效果信息
     */
    fun getActionInfo(): ArrayList<SkillActionLite> {
        val list = arrayListOf<SkillActionLite>()
        actions.forEach {
            list.add(it.getFixedDesc())
        }
        return list
    }


}

