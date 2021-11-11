package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import cn.wthee.pcrtool.data.db.entity.*
import cn.wthee.pcrtool.data.db.view.CharacterInfo
import cn.wthee.pcrtool.data.db.view.CharacterInfoPro
import cn.wthee.pcrtool.data.db.view.CharacterStoryAttr
import cn.wthee.pcrtool.data.db.view.PvpCharacterData

/**
 * 角色数据 DAO
 */
@Dao
interface UnitDao {

    /**
     * 根据筛选、排序条件，获取角色分页列表 [CharacterInfo]
     * @param sortType 排序类型
     * @param asc 排序升降 "asc":升序，"desc":降序
     * @param unitName 角色名字
     * @param pos1 站位范围开始
     * @param pos2 站位范围结束
     * @param atkType 0:全部，1：物理，2：魔法
     * @param guild 角色所属公会名
     * @param showAll 0：仅收藏，1：全部
     * @param r6 0：全部，1：仅六星解放
     * @param starIds 收藏的角色编号
     */
    @Transaction
    @Query(
        """
        SELECT
            unit_profile.unit_id,
            unit_data.unit_name,
            unit_data.is_limited,
            unit_data.rarity,
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
            LEFT JOIN (SELECT id,exchange_id,unit_id FROM gacha_exchange_lineup GROUP BY unit_id) AS gacha ON gacha.unit_id = unit_data.unit_id
        WHERE 
            unit_data.unit_name like '%' || :unitName || '%'
        AND
            unit_profile.unit_id in (SELECT MAX(unit_promotion.unit_id) FROM unit_promotion WHERE unit_id = unit_profile.unit_id)
        AND (
            (unit_profile.unit_id IN (:starIds) AND  1 = CASE WHEN  0 = :showAll  THEN 1 END) 
            OR 
            (1 = CASE WHEN  1 = :showAll  THEN 1 END)
        )
        AND 1 = CASE
            WHEN  0 = :r6  THEN 1
            WHEN  rarity_6_quest_id != 0 AND 1 = :r6  THEN 1 
        END
        AND unit_profile.unit_id < 200000 
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
        AND 1 = CASE
            WHEN  0 = :type  THEN 1
            WHEN  1 = :type AND is_limited = 0 THEN 1 
            WHEN  2 = :type AND is_limited = 1 AND rarity = 3 THEN 1 
            WHEN  3 = :type AND is_limited = 1 AND rarity = 1 THEN 1 
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
        CASE WHEN :sortType = 4 AND :asc = 'desc'  THEN unit_data.search_area_width END DESC,
        gacha.exchange_id DESC, gacha.id
            """
    )
    suspend fun getInfoAndData(
        sortType: Int, asc: String, unitName: String, pos1: Int, pos2: Int,
        atkType: Int, guild: String, showAll: Int, r6: Int, starIds: List<Int>,
        type: Int
    ): List<CharacterInfo>

    @Transaction
    @Query(
        """
        SELECT
            unit_profile.unit_id,
            unit_data.unit_name,
            unit_data.is_limited,
            unit_data.rarity,
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
            LEFT JOIN (SELECT id,exchange_id,unit_id FROM gacha_exchange_lineup GROUP BY unit_id) AS gacha ON gacha.unit_id = unit_data.unit_id
        WHERE 
            unit_data.unit_id = :unitId
        """
    )
    suspend fun getInfoAndData(unitId:Int): CharacterInfo

    /**
     * 获取角色详情基本资料
     * @param unitId 角色编号
     */
    @Transaction
    @Query(
        """
        SELECT
            unit_profile.unit_id,
            unit_data.unit_name,
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
            COALESCE( unit_data.comment, "......" ) AS intro,
            unit_data.atk_type,
            COALESCE( rarity_6_quest_data.rarity_6_quest_id, 0 ) AS rarity_6_quest_id,
            unit_data.rarity,
            COALESCE( actual_unit_background.unit_name, "" ) AS actual_name,
            COALESCE(cts.comments, "......") AS comments,
            COALESCE(GROUP_CONCAT(r.description, "-"), "......") AS room_comments
        FROM
            unit_profile
            LEFT JOIN unit_data ON unit_data.unit_id = unit_profile.unit_id
            LEFT JOIN rarity_6_quest_data ON unit_data.unit_id = rarity_6_quest_data.unit_id
            LEFT JOIN actual_unit_background ON ( unit_data.unit_id = actual_unit_background.unit_id - 30 OR unit_data.unit_id = actual_unit_background.unit_id - 31 )
            LEFT JOIN (SELECT unit_id, GROUP_CONCAT( description, '-' ) AS comments FROM unit_comments GROUP BY unit_id) AS cts ON cts.unit_id = unit_profile.unit_id
            LEFT JOIN room_unit_comments AS r ON unit_profile.unit_id = r.unit_id
        WHERE 
            unit_profile.unit_id = :unitId 
        GROUP BY unit_profile.unit_id """
    )
    suspend fun getInfoPro(unitId: Int): CharacterInfoPro?

    /**
     * 根据位置范围 [start] <= x <= [end] 获取角色列表
     * @param start 开始位置
     * @param end 结束位置
     */
    @Query("SELECT unit_id, search_area_width as position, -1 as type FROM unit_data WHERE search_area_width >= :start AND search_area_width <= :end AND comment <> \"\" ORDER BY search_area_width")
    suspend fun getCharacterByPosition(start: Int, end: Int): List<PvpCharacterData>

    /**
     * 获取角色列表
     * @param unitIds 角色编号
     */
    @Query("SELECT unit_id, search_area_width as position, -1 as type FROM unit_data WHERE unit_id IN (:unitIds)  AND comment <> \"\" ORDER BY search_area_width")
    suspend fun getCharacterByIds(unitIds: List<Int>): List<PvpCharacterData>

    /**
     * 获取角色所需装备数据
     * @param unitId 角色编号
     * @param rank 角色rank
     */
    @Query("SELECT * FROM unit_promotion WHERE unit_promotion.unit_id = :unitId AND unit_promotion.promotion_level = :rank ")
    suspend fun getRankEquipment(unitId: Int, rank: Int): UnitPromotion

    /**
     * 获取角色 Rank 属性状态
     * @param unitId 角色编号
     * @param rank 角色rank
     */
    @Query("SELECT * FROM unit_promotion_status WHERE unit_promotion_status.unit_id = :unitId AND unit_promotion_status.promotion_level = :rank ")
    suspend fun getRankStatus(unitId: Int, rank: Int): UnitPromotionStatus?

    /**
     * 获取角色角色星级提供的属性
     * @param unitId 角色编号
     * @param rarity 角色星级
     */
    @Query("SELECT * FROM unit_rarity WHERE unit_rarity.unit_id = :unitId AND unit_rarity.rarity = :rarity ")
    suspend fun getRarity(unitId: Int, rarity: Int): UnitRarity

    /**
     * 获取角色 Rank 最大值
     * @param unitId 角色编号
     */
    @Query("SELECT MAX( promotion_level ) FROM unit_promotion WHERE unit_id = :unitId")
    suspend fun getMaxRank(unitId: Int): Int

    /**
     * 获取角色星级最大值
     * @param unitId 角色编号
     */
    @Query("SELECT MAX( rarity ) FROM unit_rarity  WHERE unit_id = :unitId")
    suspend fun getMaxRarity(unitId: Int): Int

    /**
     * 获取所有公会信息
     */
    @Query("SELECT * FROM guild")
    suspend fun getGuilds(): List<GuildData>

    /**
     * 获取所有公会信息
     */
    @Query("SELECT * FROM guild_additional_member WHERE guild_id = :guildId")
    suspend fun getGuildAddMembers(guildId: Int): GuildAdditionalMember?

    /**
     * 获取已六星角色 id 列表
     */
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

    /**
     * 获取角色剧情属性
     * @param unitId 角色编号
     */
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
            c.chara_type = ( SELECT chara_type FROM chara_identity WHERE unit_id =:unitId)
    """
    )
    suspend fun getCharacterStoryStatus(unitId: Int): List<CharacterStoryAttr>

    /**
     * 获取角色最大等级
     */
    @Query("SELECT MAX( unit_level ) - 1 FROM experience_unit")
    suspend fun getMaxLevel(): Int

    /**
     * 获取角色 Rank 奖励
     */
    @Query("SELECT * FROM promotion_bonus WHERE unit_id = :unitId AND promotion_level = :rank")
    suspend fun getRankBonus(rank: Int, unitId: Int): UnitPromotionBonus?

    /**
     * 获取战力系数
     */
    @Query("SELECT * FROM unit_status_coefficient WHERE coefficient_id = 1")
    suspend fun getCoefficient(): UnitStatusCoefficient

    /**
     * 获取特殊六星 id
     */
    @Query("SELECT cutin1_star6 FROM unit_data WHERE cutin_1 = :unitId AND cutin1_star6 <> :unitId")
    suspend fun getCutinId(unitId: Int): Int?
}