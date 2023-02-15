package cn.wthee.pcrtool.data.model

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.view.SkillActionDetail
import cn.wthee.pcrtool.data.db.view.SkillActionText
import cn.wthee.pcrtool.data.enums.SkillIndexType
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.LogReportUtil
import cn.wthee.pcrtool.utils.getString

/**
 * 角色技能信息
 */
@Suppress("RegExpRedundantEscape")
data class SkillDetail(
    val skillId: Int = 1,
    val name: String = "?",
    val desc: String = "?",
    val iconType: Int = 1,
    val castTime: Double = 0.0,
    val level: Int = 1,
    val atk: Int = 100,
    val bossUbCooltime: Double = 0.0,
    var enemySkillIndex: Int = 0,
) {
    /**
     * 角色技能效果
     * 在 SkillViewModel#getCharacterSkills 获取并中设置
     */
    var actions = listOf<SkillActionDetail>()

    /**
     * 角色技能下标
     */
    var skillIndexType: SkillIndexType = SkillIndexType.UNKNOWN

    /**
     * 获取技能效果信息
     */
    fun getActionInfo(): ArrayList<SkillActionText> {
        val list = arrayListOf<SkillActionText>()
        actions.forEach {
            it.getActionDesc().let { actionDesc ->
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
                skillActionPro.getActionDesc().let { actionDesc ->
                    if (actionDesc.showCoe) {
                        val coe = Regex("\\{.\\}").findAll(actionDesc.action).first().value
                        list.add(ShowCoe(index, 0, coe))
                        val actionText = getString(R.string.skill_action)
                        Regex("${actionText}\\(.\\)").findAll(actionDesc.action).forEach { result ->
                            val next = result.value.substring(3, 4).toInt() - 1
                            list.add(ShowCoe(next, 1, coe))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            LogReportUtil.upload(e, Constants.EXCEPTION_SKILL + "skill_id:$skillId")
        }

        return list
    }

}

data class ShowCoe(
    val actionIndex: Int,
    val type: Int,
    val coe: String
)

