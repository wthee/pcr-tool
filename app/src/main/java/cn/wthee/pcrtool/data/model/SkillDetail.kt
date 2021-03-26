package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.data.view.SkillActionPro
import cn.wthee.pcrtool.data.view.SkillActionText

/**
 * 角色技能信息
 */
data class SkillDetail(
    val skillId: Int,
    val name: String,
    val desc: String,
    val icon_type: Int,
    val level: Int,
    val atk: Int,
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
            it.getActionDesc()?.let { actionDesc ->
                list.add(actionDesc)
            }
        }
        return list
    }

    /**
     * 获取需要显示系数标识的动作
     */
    fun getActionIndexWithCoe(): ArrayList<ShowCoe> {
        val list = arrayListOf<ShowCoe>()
        actions.forEachIndexed { index, skillActionPro ->
            skillActionPro.getActionDesc()?.let { actionDesc ->
                if (actionDesc.showCoe) {
                    var coe = ""
                    Regex("\\{系数.\\}").findAll(actionDesc.action).forEach { result ->
                        coe = result.value
                    }
                    list.add(ShowCoe(index, coe))
                    Regex("动作\\(.\\)").findAll(actionDesc.action).forEach { result ->
                        val next = result.value.substring(3, 4).toInt() - 1
                        list.add(ShowCoe(next, coe))
                    }
                }

            }
        }
        return list
    }

}

data class ShowCoe(
    val actionIndex: Int,
    val coe: String
)

