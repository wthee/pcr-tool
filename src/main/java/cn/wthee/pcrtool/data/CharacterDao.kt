package cn.wthee.pcrtool.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import cn.wthee.pcrtool.data.model.*


//角色数据DAO
@Dao
interface CharacterDao {

    @Transaction
    @Query(
        "SELECT " +
                "unit_profile.unit_id, " +
                "unit_profile.unit_name, " +
                "unit_profile.age, " +
                "unit_profile.guild, " +
                "unit_profile.race, " +
                "unit_profile.height, " +
                "unit_profile.weight, " +
                "unit_profile.birth_month, " +
                "unit_profile.birth_day, " +
                "unit_profile.blood_type, " +
                "unit_profile.favorite, " +
                "unit_profile.voice, " +
                "unit_profile.catch_copy, " +
                "unit_profile.self_text, " +
                "unit_data.search_area_width, " +
                "unit_data.comment, " +
                "rarity_6_quest_data.rarity_6_quest_id, " +
                "coalesce(actual_unit_background.unit_name, \"\") as actual_name, " +
                "coalesce(character_love_rankup_text.serif_1, \"\") as serif_1, " +
                "coalesce(character_love_rankup_text.serif_2, \"\") as serif_2, " +
                "coalesce(character_love_rankup_text.serif_3, \"\") as serif_3 " +
                "FROM " +
                "unit_data " +
                "INNER JOIN unit_profile ON unit_data.unit_id = unit_profile.unit_id " +
                "LEFT JOIN rarity_6_quest_data ON unit_data.unit_id = rarity_6_quest_data.unit_id " +
                "LEFT JOIN actual_unit_background ON (unit_data.unit_id = actual_unit_background.unit_id - 30 OR unit_data.unit_id = actual_unit_background.unit_id - 31) " +
                "LEFT JOIN character_love_rankup_text ON character_love_rankup_text.chara_id = unit_data.unit_id / 100 " +
                "WHERE unit_profile.unit_id <> 110201"
    )
    suspend fun getInfoAndData(): List<CharacterBasicInfo>

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

    //角色技能
    @Query("SELECT * FROM skill_data  WHERE skill_id = :sid")
    suspend fun getSkillData(sid: Int): SkillData

    //角色技能详情
    @Query("SELECT * FROM skill_action  WHERE action_id IN (:aid)")
    suspend fun getSkillActions(aid: List<Int>): List<SkillAction>
}