package cn.wthee.pcrtool.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import cn.wthee.pcrtool.data.model.entity.*


//角色数据DAO
@Dao
interface CharacterDao {

    @Transaction
    @Query(
        """
        SELECT 
            unit_profile.unit_id, 
            unit_profile.unit_name, 
            unit_data.kana, 
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
            coalesce(unit_data.comment, "") as comment, 
            unit_data.atk_type, 
            coalesce(rarity_6_quest_data.rarity_6_quest_id, 0) as rarity_6_quest_id, 
            unit_data.rarity, 
            CAST(SUBSTR(unit_data.start_time, 0, 4 ) || SUBSTR(unit_data.start_time, 6, 2 ) || SUBSTR(unit_data.start_time, 9, 2 ) AS INTEGER) AS start_time , 
            coalesce(actual_unit_background.unit_name, "") as actual_name, 
            coalesce(character_love_rankup_text.serif_1, "") as serif_1, 
            coalesce(character_love_rankup_text.serif_2, "") as serif_2, 
            coalesce(character_love_rankup_text.serif_3, "") as serif_3 
        FROM 
            unit_profile 
        LEFT JOIN unit_data ON unit_data.unit_id = unit_profile.unit_id 
        LEFT JOIN rarity_6_quest_data ON unit_data.unit_id = rarity_6_quest_data.unit_id 
        LEFT JOIN actual_unit_background ON (unit_data.unit_id = actual_unit_background.unit_id - 30 OR unit_data.unit_id = actual_unit_background.unit_id - 31) 
        LEFT JOIN character_love_rankup_text ON character_love_rankup_text.chara_id = unit_data.unit_id / 100 
        WHERE 
            unit_profile.unit_name like '%' || :unitName || '%' """
    )
    suspend fun getInfoAndData(unitName: String): List<CharacterBasicInfo>

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
    suspend fun getAttackPattern(unitId: Int): AttackPattern

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
             coalesce((a.total_exp - c.total_exp), 0) AS exp_team_abs, 
             b.total_exp AS exp_unit, 
             coalesce((b.total_exp - d.total_exp) , 0) AS exp_unit_abs 
         FROM 
            experience_team AS a 
         LEFT JOIN ( SELECT team_level + 1 AS team_level, total_exp FROM experience_team ) AS c ON a.team_level = c.team_level 
         LEFT JOIN experience_unit AS b ON a.team_level = b.unit_level 
         LEFT JOIN ( SELECT unit_level + 1 AS unit_level, total_exp FROM experience_unit ) AS d ON b.unit_level = d.unit_level
         ORDER BY level DESC"""
    )
    suspend fun getLevelExp(): List<CharacterExperienceAll>
}