package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import androidx.room.Transaction
import cn.wthee.pcrtool.data.db.view.*

//fixme 8月台服更新后，可取消判断
const val limitedIds = """
    (
		106101,
		107001,
		107101,
		107501,
		107701,
		107801,
		107901,
		108101,
		108301,
		108401,
		108601,
		108701,
		108801,
		109101,
		110001,
		110301,
		110401,
		110601
	)
"""

//角色最大编号
const val maxUnitId = 200000

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
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            unit_profile.unit_id,
            unit_data.unit_name,
            unit_data.is_limited,
            unit_data.rarity,
            COALESCE( unit_data.kana, '' ) AS kana,
            CAST((CASE WHEN unit_profile.age LIKE '%-%' OR unit_profile.age LIKE '%?%' OR  unit_profile.age LIKE '%？%' OR unit_profile.age = 0 THEN 999 ELSE unit_profile.age END) AS INTEGER) AS age_int,
            unit_profile.guild,
            unit_profile.race,
            CAST((CASE WHEN unit_profile.height LIKE '%-%' OR unit_profile.height LIKE '%?%' OR  unit_profile.height LIKE '%？%' OR unit_profile.height = 0 THEN 999 ELSE unit_profile.height END) AS INTEGER) AS height_int,
            CAST((CASE WHEN unit_profile.weight LIKE '%-%' OR unit_profile.weight LIKE '%?%' OR  unit_profile.weight LIKE '%？%' OR unit_profile.weight = 0 THEN 999 ELSE unit_profile.weight END) AS INTEGER) AS weight_int,
            CAST((CASE WHEN unit_profile.birth_month LIKE '%-%' OR unit_profile.birth_month LIKE '%?%' OR  unit_profile.birth_month LIKE '%？%' OR unit_profile.birth_month = 0 THEN 999 ELSE unit_profile.birth_month END) AS INTEGER) AS birth_month_int,
            CAST((CASE WHEN unit_profile.birth_day LIKE '%-%' OR unit_profile.birth_day LIKE '%?%' OR  unit_profile.birth_day LIKE '%？%' OR unit_profile.birth_day = 0 THEN 999 ELSE unit_profile.birth_day END) AS INTEGER) AS birth_day_int,
            unit_data.search_area_width,
            unit_data.atk_type,
            COALESCE( rarity_6_quest_data.rarity_6_quest_id, 0 ) AS r6Id,
            COALESCE(SUBSTR( unit_data.start_time, 0, 11), '2015/04/01') AS start_time
        FROM
            unit_profile
            LEFT JOIN unit_data ON unit_data.unit_id = unit_profile.unit_id
            LEFT JOIN rarity_6_quest_data ON unit_data.unit_id = rarity_6_quest_data.unit_id
            LEFT JOIN (SELECT id,exchange_id,unit_id FROM gacha_exchange_lineup GROUP BY unit_id) AS gacha ON gacha.unit_id = unit_data.unit_id
        WHERE 
            unit_data.unit_name like '%' || :unitName || '%'
        AND unit_profile.unit_id in (SELECT MAX(unit_promotion.unit_id) FROM unit_promotion WHERE unit_id = unit_profile.unit_id)
        AND unit_profile.unit_id < $maxUnitId
        AND (
            (unit_profile.unit_id IN (:starIds) AND  1 = CASE WHEN  0 = :showAll  THEN 1 END) 
            OR 
            (1 = CASE WHEN  1 = :showAll  THEN 1 END)
        )
        AND 1 = CASE
            WHEN  0 = :r6  THEN 1
            WHEN  r6Id != 0 AND 1 = :r6  THEN 1 
            WHEN  r6Id = 0 AND 2 = :r6  THEN 1 
        END
        AND 1 = CASE
            WHEN  unit_data.search_area_width >= :pos1 AND unit_data.search_area_width <= :pos2  THEN 1 
        END
        AND 1 = CASE
            WHEN  0 = :atkType  THEN 1
            WHEN  unit_data.atk_type = :atkType  THEN 1 
        END
        AND 1 = CASE
            WHEN 0 = :guildId THEN 1 
            WHEN -1 = :guildId THEN unit_profile.guild_id = ''
            WHEN  unit_profile.guild_id = :guildId  THEN 1 
        END     
        AND 1 = CASE
            WHEN  0 = :type  THEN 1
            WHEN  1 = :type AND is_limited = 0 AND unit_profile.unit_id NOT IN ${limitedIds} THEN 1 
            WHEN  2 = :type AND ((is_limited = 1 AND rarity = 3) OR unit_profile.unit_id IN ${limitedIds}) THEN 1 
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
        CASE WHEN :sortType = 5 AND :asc = 'asc'  THEN birth_month_int END ASC,
        CASE WHEN :sortType = 5 AND :asc = 'desc'  THEN birth_month_int END DESC,
        CASE WHEN :sortType = 5 AND :asc = 'asc'  THEN birth_day_int END ASC,
        CASE WHEN :sortType = 5 AND :asc = 'desc'  THEN birth_day_int END DESC,
        gacha.exchange_id DESC, gacha.id
        LIMIT :limit
            """
    )
    suspend fun getInfoAndData(
        sortType: Int, asc: String, unitName: String, pos1: Int, pos2: Int,
        atkType: Int, guildId: Int, showAll: Int, r6: Int, starIds: List<Int>,
        type: Int,
        limit: Int,
    ): List<CharacterInfo>

    /**
     * 角色数量
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            COUNT(*)
        FROM
            unit_profile
            LEFT JOIN unit_data ON unit_profile.unit_id = unit_data.unit_id 
        WHERE
            unit_profile.unit_id IN ( SELECT MAX( unit_promotion.unit_id ) FROM unit_promotion WHERE unit_id = unit_profile.unit_id ) 
            AND unit_profile.unit_id < $maxUnitId 
            AND unit_data.unit_id IS NOT NULL
        """
    )
    suspend fun getCount(): Int

    /**
     * 角色信息
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            unit_profile.unit_id,
            unit_data.unit_name,
            unit_data.is_limited,
            unit_data.rarity,
            COALESCE( unit_data.kana, '' ) AS kana,
            CAST((CASE WHEN unit_profile.age LIKE '%-%' OR unit_profile.age LIKE '%?%' OR  unit_profile.age LIKE '%？%' OR unit_profile.age = 0 THEN 999 ELSE unit_profile.age END) AS INTEGER) AS age_int,
            unit_profile.guild,
            unit_profile.race,
            CAST((CASE WHEN unit_profile.height LIKE '%-%' OR unit_profile.height LIKE '%?%' OR  unit_profile.height LIKE '%？%' OR unit_profile.height = 0 THEN 999 ELSE unit_profile.height END) AS INTEGER) AS height_int,
            CAST((CASE WHEN unit_profile.weight LIKE '%-%' OR unit_profile.weight LIKE '%?%' OR  unit_profile.weight LIKE '%？%' OR unit_profile.weight = 0 THEN 999 ELSE unit_profile.weight END) AS INTEGER) AS weight_int,
            CAST((CASE WHEN unit_profile.birth_month LIKE '%-%' OR unit_profile.birth_month LIKE '%?%' OR  unit_profile.birth_month LIKE '%？%' OR unit_profile.birth_month = 0 THEN 999 ELSE unit_profile.birth_month END) AS INTEGER) AS birth_month_int,
            CAST((CASE WHEN unit_profile.birth_day LIKE '%-%' OR unit_profile.birth_day LIKE '%?%' OR  unit_profile.birth_day LIKE '%？%' OR unit_profile.birth_day = 0 THEN 999 ELSE unit_profile.birth_day END) AS INTEGER) AS birth_day_int,
            unit_data.search_area_width,
            unit_data.atk_type,
            COALESCE( rarity_6_quest_data.rarity_6_quest_id, 0 ) AS r6Id,
            COALESCE(SUBSTR( unit_data.start_time, 0, 11), '2015/04/01') AS start_time
        FROM
            unit_profile
            LEFT JOIN unit_data ON unit_data.unit_id = unit_profile.unit_id
            LEFT JOIN rarity_6_quest_data ON unit_data.unit_id = rarity_6_quest_data.unit_id
            LEFT JOIN (SELECT id,exchange_id,unit_id FROM gacha_exchange_lineup GROUP BY unit_id) AS gacha ON gacha.unit_id = unit_data.unit_id
        WHERE 
            unit_data.unit_id = :unitId
        """
    )
    suspend fun getInfoAndData(unitId: Int): CharacterInfo

    /**
     * 获取角色详情基本资料
     * @param unitId 角色编号
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            unit_profile.unit_id,
            unit_data.unit_name,
            COALESCE( unit_data.kana, '' ) AS kana,
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
            COALESCE( unit_data.comment, '......' ) AS intro,
            unit_data.atk_type,
            COALESCE( rarity_6_quest_data.rarity_6_quest_id, 0 ) AS r6Id,
            unit_data.rarity,
            COALESCE( actual_unit_background.unit_name, '' ) AS actual_name,
            COALESCE(cts.comments, '......') AS comments
        FROM
            unit_profile
            LEFT JOIN unit_data ON unit_data.unit_id = unit_profile.unit_id
            LEFT JOIN rarity_6_quest_data ON unit_data.unit_id = rarity_6_quest_data.unit_id
            LEFT JOIN actual_unit_background ON ( unit_data.unit_id = actual_unit_background.unit_id - 30 OR unit_data.unit_id = actual_unit_background.unit_id - 31 )
            LEFT JOIN (SELECT unit_id, GROUP_CONCAT( description, '-' ) AS comments FROM unit_comments GROUP BY unit_id) AS cts ON cts.unit_id = unit_profile.unit_id
        WHERE 
            unit_profile.unit_id = :unitId 
        GROUP BY unit_profile.unit_id """
    )
    suspend fun getInfoPro(unitId: Int): CharacterInfoPro?

    /**
     * 获取角色小屋对话
     * @param unitId 角色编号
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            b.unit_id,
            b.unit_name,
            COALESCE( GROUP_CONCAT( a.description, '-' ), '......') AS room_comments 
        FROM
            room_unit_comments AS a
            LEFT JOIN unit_data AS b ON a.unit_id = b.unit_id 
        WHERE
            a.unit_id = :unitId 
        GROUP BY
            a.unit_id """
    )
    suspend fun getRoomComments(unitId: Int): RoomCommentData?

    /**
     * 获取多角色卡关联角色编号
     */
    @SkipQueryVerification
    @Query("""SELECT unit_data.unit_id FROM unit_data WHERE unit_data.original_unit_id = :unitId """)
    suspend fun getMultiIds(unitId: Int): List<Int>

    /**
     * 根据位置范围 [start] <= x <= [end] 获取角色列表
     * @param start 开始位置
     * @param end 结束位置
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            a.unit_id,
            b.search_area_width AS position, 
            - 1 AS type 
        FROM
            unit_profile AS a
            LEFT JOIN unit_data AS b ON a.unit_id = b.unit_id 
        WHERE
            search_area_width >= :start 
            AND search_area_width <= :end
            AND a.unit_id < $maxUnitId  
            AND b.search_area_width > 0
        ORDER BY
            b.search_area_width
    """
    )
    suspend fun getCharacterByPosition(start: Int, end: Int): List<PvpCharacterData>

    /**
     * 获取角色列表
     * @param unitIds 角色编号
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            a.unit_id,
            b.search_area_width AS position, 
            - 1 AS type 
        FROM
            unit_profile AS a
            LEFT JOIN unit_data AS b ON a.unit_id = b.unit_id 
        WHERE
            a.unit_id IN (:unitIds) 
            AND a.unit_id < $maxUnitId
            AND b.search_area_width > 0
        ORDER BY
            b.search_area_width
    """
    )
    suspend fun getCharacterByIds(unitIds: List<Int>): List<PvpCharacterData>

    /**
     * 获取角色所需装备数据
     * @param unitId 角色编号
     * @param rank 角色rank
     */
    @SkipQueryVerification
    @Query("SELECT * FROM unit_promotion WHERE unit_promotion.unit_id = :unitId AND unit_promotion.promotion_level = :rank ")
    suspend fun getRankEquipment(unitId: Int, rank: Int): UnitPromotion

    /**
     * 获取角色 Rank 属性状态
     * @param unitId 角色编号
     * @param rank 角色rank
     */
    @SkipQueryVerification
    @Query("SELECT * FROM unit_promotion_status WHERE unit_promotion_status.unit_id = :unitId AND unit_promotion_status.promotion_level = :rank ")
    suspend fun getRankStatus(unitId: Int, rank: Int): UnitPromotionStatus?

    /**
     * 获取角色角色星级提供的属性
     * @param unitId 角色编号
     * @param rarity 角色星级
     */
    @SkipQueryVerification
    @Query("SELECT * FROM unit_rarity WHERE unit_rarity.unit_id = :unitId AND unit_rarity.rarity = :rarity ")
    suspend fun getRarity(unitId: Int, rarity: Int): UnitRarity

    /**
     * 获取角色 Rank 最大值
     * @param unitId 角色编号
     */
    @SkipQueryVerification
    @Query("SELECT MAX( promotion_level ) FROM unit_promotion WHERE unit_id = :unitId")
    suspend fun getMaxRank(unitId: Int): Int

    /**
     * 获取角色星级最大值
     * @param unitId 角色编号
     */
    @SkipQueryVerification
    @Query("SELECT MAX( rarity ) FROM unit_rarity  WHERE unit_id = :unitId")
    suspend fun getMaxRarity(unitId: Int): Int

    /**
     * 获取所有公会信息
     */
    @SkipQueryVerification
    @Query("SELECT guild_id, guild_name FROM guild WHERE member1 != 0")
    suspend fun getGuilds(): List<GuildData>

    /**
     * 获取所有公会成员信息
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            guild_id,
            guild_name,
            description,
            guild_master,
            GROUP_CONCAT( unit_id, '-' ) AS unit_ids,
            GROUP_CONCAT( unit_name, '-' ) AS unit_names 
        FROM
            (
            SELECT
                guild.guild_id,
                guild.guild_name,
                guild.description,
                ( CASE WHEN guild.guild_master == guild.member1 THEN guild.guild_master ELSE guild.member1 END ) AS guild_master,
                unit_profile.unit_id,
                unit_profile.unit_name 
            FROM
                guild
                LEFT JOIN unit_profile ON guild.guild_id = unit_profile.guild_id 
                AND unit_profile.unit_id < $maxUnitId
                AND unit_profile.unit_id = ( SELECT MAX( unit_promotion.unit_id ) FROM unit_promotion WHERE unit_id = unit_profile.unit_id ) 
            ) 
        WHERE
            guild_master != 0 
        GROUP BY
            guild_id
    """
    )
    suspend fun getAllGuildMembers(): List<GuildAllMember>

    /**
     * 获取新增公会成员信息
     */
    @SkipQueryVerification
    @Query("SELECT * FROM guild_additional_member WHERE guild_id = :guildId")
    suspend fun getGuildAddMembers(guildId: Int): GuildAdditionalMember?

    /**
     * 获取无公会成员信息
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT 
            GROUP_CONCAT(unit_id,'-') as unit_ids,
            GROUP_CONCAT(unit_name,'-') as unit_names
        FROM
            unit_profile
        WHERE unit_profile.unit_id in (SELECT MAX(unit_promotion.unit_id) FROM unit_promotion WHERE unit_id = unit_profile.unit_id)
        AND guild_id = ''
        GROUP BY guild_id
    """
    )
    suspend fun getNoGuildMembers(): NoGuildMemberInfo?

    /**
     * 获取已六星角色 id 列表
     */
    @SkipQueryVerification
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
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            a.story_id,
            c.title,
            c.sub_title,
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
            LEFT JOIN chara_identity AS b ON b.unit_id / 100 IN (
                a.chara_id_1,
                a.chara_id_2,
                a.chara_id_3,
                a.chara_id_4,
                a.chara_id_5,
                a.chara_id_6,
                a.chara_id_7,
                a.chara_id_8,
                a.chara_id_9,
                a.chara_id_10 
            )
            LEFT JOIN story_detail AS c ON a.story_id = c.story_id
        WHERE b.unit_id = :unitId
    """
    )
    suspend fun getCharacterStoryStatus(unitId: Int): List<CharacterStoryAttr>

    /**
     * 获取角色最大等级
     */
    @SkipQueryVerification
    @Query("SELECT MAX( unit_level ) - 1 FROM experience_unit")
    suspend fun getMaxLevel(): Int

    /**
     * 获取角色 Rank 奖励
     */
    @SkipQueryVerification
    @Query("SELECT * FROM promotion_bonus WHERE unit_id = :unitId AND promotion_level = :rank")
    suspend fun getRankBonus(rank: Int, unitId: Int): UnitPromotionBonus?

    /**
     * 获取战力系数
     */
    @SkipQueryVerification
    @Query("SELECT * FROM unit_status_coefficient WHERE coefficient_id = 1")
    suspend fun getCoefficient(): UnitStatusCoefficient

    /**
     * 获取特殊六星 id
     */
    @SkipQueryVerification
    @Query("SELECT unit_id FROM unit_conversion WHERE original_unit_id = :unitId")
    suspend fun getCutinId(unitId: Int): Int?

    /**
     * 获取召唤物基本信息
     */
    @SkipQueryVerification
    @Query("SELECT unit_id, unit_name, search_area_width, normal_atk_cast_time, atk_type  FROM unit_data WHERE unit_id = :unitId ")
    suspend fun getSummonData(unitId: Int): SummonData

    /**
     * 获取现实中角色 id
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            b.unit_id 
        FROM
            unit_profile AS a
            LEFT JOIN actual_unit_background AS b ON a.unit_id / 100 = b.unit_id / 100
        WHERE a.unit_id = :unitId
    """
    )
    suspend fun getActualId(unitId: Int): Int?

}