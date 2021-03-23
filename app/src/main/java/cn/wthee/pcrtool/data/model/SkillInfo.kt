package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.data.view.SkillActionPro
import cn.wthee.pcrtool.data.view.SkillActionText

/**
 * 角色技能信息
 */
data class SkillInfo(
    val skillId: Int,
    val name: String,
    val desc: String,
    val icon_type: Int
) {
    /**
     * 角色技能效果
     * 在 SkillViewModel#getCharacterSkills 获取并中设置
     */
    var actions = listOf<SkillActionPro>()

    /**
     * 获取技能效果信息
     */
    fun getActionInfo(): ArrayList<SkillActionText> {
        val list = arrayListOf<SkillActionText>()
        actions.forEach {
            list.add(it.getActionDesc())
        }
        return list
    }


}

