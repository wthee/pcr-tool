package cn.wthee.pcrtool.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import cn.wthee.pcrtool.database.entity.*
import cn.wthee.pcrtool.database.view.CharacterExperienceAll
import cn.wthee.pcrtool.database.view.CharacterInfo
import cn.wthee.pcrtool.database.view.CharacterInfoPro
import cn.wthee.pcrtool.database.view.PvpCharacterData


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
            CAST((CASE WHEN unit_profile.age ='??' THEN 999 ELSE unit_profile.age END) AS INTEGER) AS age_int,
            unit_profile.guild,
            unit_profile.race,
            CAST((CASE WHEN unit_profile.height ='??' OR  unit_profile.height = 0 THEN 999 ELSE unit_profile.height END) AS INTEGER) AS height_int,
            CAST((CASE WHEN unit_profile.weight ='??' OR  unit_profile.weight = 0 THEN 999 ELSE unit_profile.weight END) AS INTEGER) AS weight_int,
            unit_data.search_area_width,
            unit_data.atk_type,
            COALESCE(SUBSTR( unit_data.start_time, 0, 11), "2015/04/01") AS start_time
        FROM
            unit_profile
            LEFT JOIN unit_data ON unit_data.unit_id = unit_profile.unit_id
        WHERE 
            unit_profile.unit_name like '%' || :unitName || '%' 
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
        atkType: Int, guild: String
    ): PagingSource<Int, CharacterInfo>

    //获取角色详情数据
    @Transaction
    @Query(
        """
        SELECT
            unit_profile.unit_id,
            unit_profile.unit_name,
            COALESCE( unit_data.kana, "" ) AS kana,
            unit_profile.age,
            unit_profile.guild,
            unit_profile.race,
            unit_profile.height,
            unit_profile.weight,
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
    @Query("SELECT MAX( unit_level ) FROM experience_unit")
    suspend fun getMaxLevel(): Int

    //角色动作循环
    @Query("SELECT * FROM unit_attack_pattern where unit_id = :unitId")
    suspend fun getAttackPattern(unitId: Int): List<AttackPattern>

    //公会信息
    @Query("SELECT * FROM guild")
    suspend fun getGuilds(): List<GuildData>

    //角色升级经验列表
    @Transaction
    @Query(
        """
         SELECT 
             a.team_level AS level, 
             a.total_exp AS exp_team, 
             COALESCE((a.total_exp - c.total_exp), 0) AS exp_team_abs, 
             b.total_exp AS exp_unit, 
             COALESCE((b.total_exp - d.total_exp) , 0) AS exp_unit_abs 
         FROM 
            experience_team AS a 
         LEFT JOIN ( SELECT team_level + 1 AS team_level, total_exp FROM experience_team ) AS c ON a.team_level = c.team_level 
         LEFT JOIN experience_unit AS b ON a.team_level = b.unit_level 
         LEFT JOIN ( SELECT unit_level + 1 AS unit_level, total_exp FROM experience_unit ) AS d ON b.unit_level = d.unit_level
         ORDER BY level DESC"""
    )
    suspend fun getLevelExp(): List<CharacterExperienceAll>

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
}