package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import androidx.room.Transaction
import cn.wthee.pcrtool.data.db.view.QuestDetail
import cn.wthee.pcrtool.data.db.view.TalentQuestData
import cn.wthee.pcrtool.data.db.view.TalentQuestRewardData

const val minEquipId = 100000

/**
 * 主线地图 DAO
 */
@Dao
interface QuestDao {


    /**
     * 获取装备掉落区域信息
     * @param equipId 装备编号
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT 
            quest_id, 
            quest_name, 
            quest_id / 1000000 % 10 AS quest_type, 
            reward_1 || '-' || reward_2 || '-' || reward_3 || '-' || reward_4 || '-' || reward_5 AS rewards, 
            odd_1 || '-' || odd_2 || '-' || odd_3 || '-' || odd_4 || '-' || odd_5 AS odds  
        FROM 
            (SELECT DISTINCT 
                a.quest_id, 
                a.quest_name, 
                GROUP_CONCAT( c.reward_id_1, '-' ) AS reward_1, 
                GROUP_CONCAT( c.odds_1, '-' ) AS odd_1, 
                GROUP_CONCAT( c.reward_id_2, '-' ) AS reward_2, 
                GROUP_CONCAT( c.odds_2, '-' ) AS odd_2, 
                GROUP_CONCAT( c.reward_id_3, '-' ) AS reward_3, 
                GROUP_CONCAT( c.odds_3, '-' ) AS odd_3, 
                GROUP_CONCAT( c.reward_id_4, '-' ) AS reward_4, 
                GROUP_CONCAT( c.odds_4, '-' ) AS odd_4, 
                GROUP_CONCAT( c.reward_id_5, '-' ) AS reward_5, 
                GROUP_CONCAT( c.odds_5, '-' ) AS odd_5  
            FROM 
                quest_data a 
            LEFT JOIN wave_group_data b ON b.wave_group_id IN ( a.wave_group_id_1, a.wave_group_id_2, a.wave_group_id_3 ) 
            LEFT JOIN enemy_reward_data c ON c.drop_reward_id IN ( b.drop_reward_id_1, b.drop_reward_id_2, b.drop_reward_id_3, b.drop_reward_id_4, b.drop_reward_id_5 )
            AND (c.reward_id_1 > $minEquipId  OR c.reward_id_1 LIKE '250%')
            WHERE 
                a.quest_id < 18000000
            GROUP BY 
                a.quest_id  
            ORDER BY 
                a.quest_id ASC, a.quest_name ASC 
            )  
        WHERE 
            rewards LIKE '%' || :equipId || '%'
        ORDER BY quest_id DESC
        """
    )
    suspend fun getEquipDropQuestList(equipId: String): List<QuestDetail>


    /**
     * 获取深域关卡掉落
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            talent_quest_data.quest_id,
            talent_quest_data.quest_name,
            reward.reward_id_2,
            reward.reward_num_2,
            reward.reward_id_3,
            reward.reward_num_3 
        FROM
            talent_quest_data
            LEFT JOIN talent_quest_clear_reward_01 AS reward ON reward.reward_group_id = talent_quest_data.clear_reward_group 
        WHERE
            reward.reward_group_id IS NOT NULL
        ORDER BY
            talent_quest_data.area_id,
            talent_quest_data.quest_id DESC
    """
    )
    suspend fun getTalentQuestRewardList(): List<TalentQuestRewardData>

    /**
     * 获取深域关卡敌人信息
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            quest.area_id / 1000 % 10 AS talent_id,
            quest.quest_id,
            quest.quest_name,
            wave.enemy_id_1,
            (SELECT unit_id FROM talent_quest_enemy_parameter WHERE talent_quest_enemy_parameter.enemy_id = wave.enemy_id_1) AS unit_id_1,
            wave.enemy_id_2,
            (SELECT unit_id FROM talent_quest_enemy_parameter WHERE talent_quest_enemy_parameter.enemy_id = wave.enemy_id_2) AS unit_id_2,
            wave.enemy_id_3,
            (SELECT unit_id FROM talent_quest_enemy_parameter WHERE talent_quest_enemy_parameter.enemy_id = wave.enemy_id_3) AS unit_id_3,
            wave.enemy_id_4,
            (SELECT unit_id FROM talent_quest_enemy_parameter WHERE talent_quest_enemy_parameter.enemy_id = wave.enemy_id_4) AS unit_id_4,
            wave.enemy_id_5,
            (SELECT unit_id FROM talent_quest_enemy_parameter WHERE talent_quest_enemy_parameter.enemy_id = wave.enemy_id_5) AS unit_id_5
        FROM
            talent_quest_data AS quest
            LEFT JOIN talent_quest_wave_group_data AS wave ON quest.wave_group_id_1 = wave.wave_group_id
        WHERE quest.area_id / 1000 % 10 = :talentType
        ORDER BY quest.quest_id DESC
    """
    )
    suspend fun getTalentQuestList(talentType: Int): List<TalentQuestData>
}