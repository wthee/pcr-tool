package cn.wthee.pcrtool.data.db.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import cn.wthee.pcrtool.data.db.entity.*
import cn.wthee.pcrtool.data.db.view.*

//角色筛选条件
const val characterWhere =
    """ 
        WHERE 
            unit_profile.unit_name like '%' || :unitName || '%'
        AND (
            (unit_profile.unit_id IN (:starIds) AND  1 = CASE WHEN  0 = :showAll  THEN 1 END) 
            OR 
            (1 = CASE WHEN  1 = :showAll  THEN 1 END)
        )
        AND 1 = CASE
            WHEN  0 = :r6  THEN 1
            WHEN  rarity_6_quest_id != 0 AND 1 = :r6  THEN 1 
        END
        AND unit_profile.unit_id NOT IN (106701,110201,113801,900103,906601)
        AND 1 = CASE
            WHEN  unit_data.search_area_width >= :pos1 AND unit_data.search_area_width <= :pos2  THEN 1 
        END
        AND 1 = CASE
            WHEN  0 = :atkType  THEN 1
            WHEN  unit_data.atk_type = :atkType  THEN 1 
        END
        AND 1 = CASE
            WHEN  "全部" = :guild  THEN 1 
            WHEN  unit_profile.guild = :guild  THEN 1 
        END     
    """

//角色数据DAO
@Dao
interface CharacterDao {

    //获取角色列表所需数据
    @Transaction
    @Query(
        """
        SELECT
            unit_profile.unit_id,
            unit_profile.unit_name,
            COALESCE( unit_data.kana, "" ) AS kana,
            CAST((CASE WHEN unit_profile.age LIKE '%?%' OR  unit_profile.age LIKE '%？%' OR unit_profile.age = 0 THEN 999 ELSE unit_profile.age END) AS INTEGER) AS age_int,
            unit_profile.guild,
            unit_profile.race,
            CAST((CASE WHEN unit_profile.height LIKE '%?%' OR  unit_profile.height LIKE '%？%' OR unit_profile.height = 0 THEN 999 ELSE unit_profile.height END) AS INTEGER) AS height_int,
            CAST((CASE WHEN unit_profile.weight LIKE '%?%' OR  unit_profile.weight LIKE '%？%' OR unit_profile.weight = 0 THEN 999 ELSE unit_profile.weight END) AS INTEGER) AS weight_int,
            unit_data.search_area_width,
            unit_data.atk_type,
            COALESCE( rarity_6_quest_data.rarity_6_quest_id, 0 ) AS rarity_6_quest_id,
            COALESCE(SUBSTR( unit_data.start_time, 0, 11), "2015/04/01") AS start_time
        FROM
            unit_profile
            LEFT JOIN unit_data ON unit_data.unit_id = unit_profile.unit_id
            LEFT JOIN rarity_6_quest_data ON unit_data.unit_id = rarity_6_quest_data.unit_id

        $characterWhere
        ORDER BY 
        CASE WHEN :sortType = 0 AND :asc = 'asc'  THEN start_time END ASC,
        CASE WHEN :sortType = 0 AND :asc = 'desc'  THEN start_time END DESC,
        CASE WHEN :sortType = 1 AND :asc = 'asc'  THEN age_int END ASC,
        CASE WHEN :sortType = 1 AND :asc = 'desc'  THEN age_int END DESC,
        CASE WHEN :sortType = 2 AND :asc = 'asc'  THEN height_int END ASC,
        CASE WHEN :sortType = 2 AND :asc = 'desc'  THEN height_int END DESC,
        CASE WHEN :sortType = 3 AND :asc = 'asc'  THEN weight_int END ASC,
        CASE WHEN :sortType = 3 AND :asc = 'desc'  THEN weight_int END DESC,
        CASE WHEN :sortType = 4 AND :asc = 'asc'  THEN unit_data.search_area_width END ASC,
        CASE WHEN :sortType = 4 AND :asc = 'desc'  THEN unit_data.search_area_width END DESC
            """
    )
    fun getInfoAndData(
        sortType: Int, asc: String, unitName: String, pos1: Int, pos2: Int,
        atkType: Int, guild: String, showAll: Int, r6: Int, starIds: List<Int>
    ): PagingSource<Int, CharacterInfo>

    //角色数量
    @Transaction
    @Query(
        """
        SELECT
            COUNT(unit_profile.unit_id)
        FROM
            unit_profile
            LEFT JOIN unit_data ON unit_data.unit_id = unit_profile.unit_id
            LEFT JOIN rarity_6_quest_data ON unit_data.unit_id = rarity_6_quest_data.unit_id
        $characterWhere
            """
    )
    suspend fun getInfoAndDataCount(
        unitName: String, pos1: Int, pos2: Int,
        atkType: Int, guild: String, showAll: Int, r6: Int, starIds: List<Int>
    ): Int

    //获取角色详情数据
    @Transaction
    @Query(
        """
        SELECT
            unit_profile.unit_id,
            unit_profile.unit_name,
            COALESCE( unit_data.kana, "" ) AS kana,
            CAST((CASE WHEN unit_profile.age LIKE '%?%' OR  unit_profile.age LIKE '%？%' OR unit_profile.age = 0 THEN 999 ELSE unit_profile.age END) AS INTEGER) AS age_int,
            unit_profile.guild,
            unit_profile.race,
            CAST((CASE WHEN unit_profile.height LIKE '%?%' OR  unit_profile.height LIKE '%？%' OR unit_profile.height = 0 THEN 999 ELSE unit_profile.height END) AS INTEGER) AS height_int,
            CAST((CASE WHEN unit_profile.weight LIKE '%?%' OR  unit_profile.weight LIKE '%？%' OR unit_profile.weight = 0 THEN 999 ELSE unit_profile.weight END) AS INTEGER) AS weight_int,
            unit_profile.birth_month,
            unit_profile.birth_day,
            unit_profile.blood_type,
            unit_profile.favorite,
            unit_profile.voice,
            unit_profile.catch_copy,
            unit_profile.self_text,
            unit_data.search_area_width,
            COALESCE( unit_data.comment, "" ) AS intro,
            unit_data.atk_type,
            COALESCE( rarity_6_quest_data.rarity_6_quest_id, 0 ) AS rarity_6_quest_id,
            unit_data.rarity,
            COALESCE( actual_unit_background.unit_name, "" ) AS actual_name,
            COALESCE(cts.comments, "") AS comments
        FROM
            unit_profile
            LEFT JOIN unit_data ON unit_data.unit_id = unit_profile.unit_id
            LEFT JOIN rarity_6_quest_data ON unit_data.unit_id = rarity_6_quest_data.unit_id
            LEFT JOIN actual_unit_background ON ( unit_data.unit_id = actual_unit_background.unit_id - 30 OR unit_data.unit_id = actual_unit_background.unit_id - 31 )
            LEFT JOIN (SELECT unit_id, GROUP_CONCAT( description, '-' ) AS comments FROM unit_comments GROUP BY unit_id) AS cts ON cts.unit_id = unit_profile.unit_id
        WHERE 
            unit_profile.unit_id = :uid """
    )
    suspend fun getInfoPro(uid: Int): CharacterInfoPro

    //根据位置获取角色
    @Query("SELECT unit_id, search_area_width as position FROM unit_data WHERE search_area_width >= :start AND search_area_width <= :end AND comment <> \"\" ORDER BY search_area_width")
    suspend fun getCharacterByPosition(start: Int, end: Int): List<PvpCharacterData>

    //角色Rank所需装备id
    @Query("SELECT * FROM unit_promotion WHERE unit_promotion.unit_id = :unitId AND unit_promotion.promotion_level = :rank ")
    suspend fun getRankEquipment(unitId: Int, rank: Int): CharacterPromotion

    //角色Rank属性状态
    @Query("SELECT * FROM unit_promotion_status WHERE unit_promotion_status.unit_id = :unitId AND unit_promotion_status.promotion_level = :rank ")
    suspend fun getRankStatus(unitId: Int, rank: Int): CharacterPromotionStatus

    //角色星级信息
    @Query("SELECT * FROM unit_rarity WHERE unit_rarity.unit_id = :unitId AND unit_rarity.rarity = :rarity ")
    suspend fun getRarity(unitId: Int, rarity: Int): CharacterRarity

    //角色Rank最大值
    @Query("SELECT MAX( promotion_level ) FROM unit_promotion WHERE unit_id = :id")
    suspend fun getMaxRank(id: Int): Int

    //角色星级最大值
    @Query("SELECT MAX( rarity ) FROM unit_rarity  WHERE unit_id = :id")
    suspend fun getMaxRarity(id: Int): Int

    //角色技能
    @Query("SELECT * FROM unit_skill_data  WHERE unit_id = :id")
    suspend fun getCharacterSkill(id: Int): CharacterSkillData

    //技能数据
    @Query("SELECT * FROM skill_data  WHERE skill_id = :sid")
    suspend fun getSkillData(sid: Int): SkillData

    //角色技能详情
    @Query("SELECT * FROM skill_action  WHERE action_id IN (:aid)")
    suspend fun getSkillActions(aid: List<Int>): List<SkillAction>

    //角色最大等级
    @Query("SELECT MAX( unit_level ) - 1 FROM experience_unit")
    suspend fun getMaxLevel(): Int

    //角色动作循环
    @Query("SELECT * FROM unit_attack_pattern where unit_id = :unitId")
    suspend fun getAttackPattern(unitId: Int): List<AttackPattern>

    //公会信息
    @Query("SELECT * FROM guild")
    suspend fun getGuilds(): List<GuildData>

    //获取已六星角色
    @Transaction
    @Query(
        """
        SELECT
            unit_profile.unit_id 
        FROM
            unit_profile
            LEFT JOIN rarity_6_quest_data ON unit_profile.unit_id = rarity_6_quest_data.unit_id 
        WHERE
            rarity_6_quest_data.unit_id <> 0"""
    )
    suspend fun getR6Ids(): List<Int>

    //角色掉落
    @Transaction
    @Query(
        """
        SELECT
            a.quest_id,
            a.quest_name,
            b.item_id,
            b.item_name
        FROM
            quest_data AS a
            LEFT JOIN item_data AS b ON a.reward_image_1 = b.item_id
        WHERE a.area_id / 1000 = 12
        AND b.item_id % 10000 = :unitId / 100
        """
    )
    suspend fun getItemDropInfos(unitId: Int): List<ItemDropInfo>

    //获取角色剧情属性
    @Transaction
    @Query(
        """
        SELECT
            * 
        FROM
            (
            SELECT
                b.chara_type,
                a.status_type_1,
                a.status_rate_1,
                a.status_type_2,
                a.status_rate_2,
                a.status_type_3,
                a.status_rate_3,
                a.status_type_4,
                a.status_rate_4,
                a.status_type_5,
                a.status_rate_5 
            FROM
                chara_story_status AS a
                LEFT JOIN chara_identity AS b ON a.chara_id_1 = b.unit_id / 100 
            ) AS c 
        WHERE
            c.chara_type IN ( SELECT chara_type FROM chara_identity WHERE unit_id =:unitId)
    """
    )
    suspend fun getCharacterStoryStatus(unitId: Int): List<CharacterStoryAttr>
}