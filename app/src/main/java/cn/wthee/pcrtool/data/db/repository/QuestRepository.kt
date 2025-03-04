package cn.wthee.pcrtool.data.db.repository

import cn.wthee.pcrtool.data.db.dao.QuestDao
import cn.wthee.pcrtool.utils.LogReportUtil
import javax.inject.Inject

/**
 * 主线地图 Repository
 *
 * @param questDao
 */
class QuestRepository @Inject constructor(private val questDao: QuestDao) {

    suspend fun getEquipDropQuestList(equipId: Int) = try {
        var query = ""
        if (equipId != 0) {
            query = equipId.toString()
        }
        questDao.getEquipDropQuestList(query)
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getEquipDropQuestList#equipId:$equipId")
        emptyList()
    }

    suspend fun getTalentQuestList(talentType: Int) = try {
        val pattern = Regex("(\\d+)-(\\d+)")

        var rewardList = questDao.getTalentQuestRewardList()
        val questList = questDao.getTalentQuestList(talentType)
        questList.forEach { quest ->
            //设置掉落信息
            val reward = rewardList.find { it.questId % 1000 == quest.questId % 1000 }
            reward?.let {
                quest.rewardId2 = reward.rewardId2
                quest.rewardId3 = reward.rewardId3
                quest.rewardNum2 = reward.rewardNum2
                quest.rewardNum3 = reward.rewardNum3
            }
            //格式化关卡名
            val matchResult = pattern.find(quest.questName)
            if (matchResult != null) {
                val (x, y) = matchResult.destructured
                quest.questName = "$x-$y"
            }
        }
        questList
    } catch (e: Exception) {
        LogReportUtil.upload(e, "getTalentQuestList#talentType:$talentType")
        emptyList()
    }
}