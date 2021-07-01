package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.data.db.view.SkillActionPro
import cn.wthee.pcrtool.data.db.view.SkillActionText
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.UMengLogUtil

/**
 * 角色技能信息
 */
data class SkillDetail(
    val skillId: Int,
    val name: String,
    val desc: String,
    val iconType: Int,
    val castTime: Double,
    val level: Int,
    val atk: Int,
    val bossUbCooltime: Double,
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
        try {
            actions.forEachIndexed { index, skillActionPro ->
                skillActionPro.getActionDesc()?.let { actionDesc ->
                    if (actionDesc.showCoe) {
                        val coe = Regex("\\{.\\}").findAll(actionDesc.action).first().value
                        list.add(ShowCoe(index, 0, coe))
                        Regex("动作\\(.\\)").findAll(actionDesc.action).forEach { result ->
                            val next = result.value.substring(3, 4).toInt() - 1
                            list.add(ShowCoe(next, 1, coe))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            UMengLogUtil.upload(e, Constants.EXCEPTION_SKILL + "skill_id:$skillId")
        }

        return list
    }

}

data class ShowCoe(
    val actionIndex: Int,
    val type: Int,
    val coe: String
)

