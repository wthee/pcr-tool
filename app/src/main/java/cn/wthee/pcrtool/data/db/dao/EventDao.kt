package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import androidx.room.Transaction
import cn.wthee.pcrtool.data.db.view.*

/**
 * 活动记录 DAO
 * 调整 id，避免重复添加日历事项
 */
@Dao
interface EventDao {

    /**
     * 获取所有活动记录
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            event.event_id,
            ( event.original_event_id % 10000 + 5000 ) AS story_id,
            event.original_event_id,
            event.start_time,
            event.end_time,
            c.title,
            COALESCE( e.unit_ids, '' ) AS unit_ids,
            enemy_parameter.enemy_id AS boss_enemy_id,
            enemy_parameter.unit_id AS boss_unit_id 
        FROM
            (
            SELECT
                a.event_id,
                ( CASE WHEN a.original_event_id = 0 THEN a.event_id ELSE a.original_event_id END ) AS original_event_id,
                a.start_time AS start_time,
                a.end_time AS end_time 
            FROM
                hatsune_schedule AS a UNION
            SELECT
                b.event_id,
                b.original_event_id,
                b.start_time,
                b.end_time 
            FROM
                shiori_event_list AS b 
            ) AS event
            LEFT JOIN event_story_data AS c ON c.story_group_id = story_id
            LEFT JOIN ( SELECT d.story_group_id, GROUP_CONCAT( d.reward_id_2, '-' ) AS unit_ids FROM event_story_detail AS d GROUP BY d.story_group_id ) AS e ON c.story_group_id = e.story_group_id
            LEFT JOIN hatsune_special_battle AS battle ON battle.event_id = original_event_id
            LEFT JOIN wave_group_data AS wave ON wave.wave_group_id = battle.wave_group_id
            LEFT JOIN enemy_parameter ON wave.enemy_id_1 = enemy_parameter.enemy_id 
        GROUP BY
            event.start_time 
        ORDER BY
            event.start_time DESC  
            LIMIT 0,:limit
        """
    )
    suspend fun getAllEvents(limit: Int): List<EventData>


    /**
     * 获取活动剧情列表
     * @param storyId 剧情活动编号
     */
    @SkipQueryVerification
    @Query("SELECT * FROM event_story_detail WHERE story_group_id = :storyId")
    suspend fun getStoryDetails(storyId: Int): List<EventStoryDetail>


    /**
     * 获取加倍活动信息
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            80000 + MAX( id ) AS id,
            COALESCE( GROUP_CONCAT( campaign_category, '-' ), '0' ) AS type,
            value,
            start_time,
            end_time 
        FROM
            campaign_schedule 
        WHERE
            campaign_category IN ( 31, 41, 32, 42, 39, 49, 34, 37, 38, 45 )
            AND id < 5000
        GROUP BY
            start_time,
            end_time,
            value 
        ORDER BY
            campaign_schedule.id DESC 
            LIMIT 0, 100
    """
    )
    suspend fun getDropEvent(): List<CalendarEvent>

    /**
     * 获取露娜塔信息
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            90000 + tower_schedule_id as id,
            1 AS type,
            0 AS value,
            start_time,
            end_time
        FROM
            tower_schedule 
        ORDER BY
            tower_schedule.tower_schedule_id DESC
        LIMIT 0,:limit
    """
    )
    suspend fun getTowerEvent(limit: Int): List<CalendarEvent>

    /**
     * 获取特殊地下城信息
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
       SELECT
            secret_dungeon_schedule.dungeon_area_id AS id,
            -1 AS type,
            0 AS value,
            start_time,
            end_time 
        FROM
            secret_dungeon_schedule 
        ORDER BY
            secret_dungeon_schedule.dungeon_area_id DESC
        LIMIT 0,:limit
    """
    )
    suspend fun getSpDungeonEvent(limit: Int): List<CalendarEvent>

    /**
     * 获取免费十连信息
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
        SELECT
            70000 + a.id as id,
            COALESCE( b.relation_count, 0 ) AS max_count,
            a.start_time,
        CASE
                WHEN b.end_time IS NOT NULL THEN
                b.end_time ELSE a.end_time 
            END AS end_time 
        FROM
            campaign_freegacha AS a
            LEFT JOIN campaign_freegacha AS b ON a.campaign_id = b.relation_id 
        WHERE
            a.freegacha_10 = 1
        ORDER BY a.start_time DESC
        LIMIT 0,:limit
    """
    )
    suspend fun getFreeGachaEvent(limit: Int): List<FreeGachaInfo>

    /**
     * 获取最新公会战日程
     */
    @SkipQueryVerification
    @Transaction
    @Query(
        """
       SELECT
            60000 + a.clan_battle_id AS id,
            a.release_month,
            a.start_time 
        FROM
            clan_battle_schedule AS a 
        ORDER BY
            a.clan_battle_id DESC
        LIMIT 0,:limit
    """
    )
    suspend fun getClanBattleEvent(limit: Int): List<ClanBattleEvent>

    /**
     * 获取生日信息
     */
    @SkipQueryVerification
    @Query(
        """
        SELECT
            CAST((CASE WHEN unit_profile.birth_month LIKE '%-%' OR unit_profile.birth_month LIKE '%?%' OR unit_profile.birth_month LIKE '%？%' OR unit_profile.birth_month = 0 THEN 999 ELSE unit_profile.birth_month END) AS INTEGER) AS birth_month_int,
            CAST((CASE WHEN unit_profile.birth_day LIKE '%-%' OR unit_profile.birth_day LIKE '%?%' OR unit_profile.birth_day LIKE '%？%' OR unit_profile.birth_day = 0 THEN 999 ELSE unit_profile.birth_day END) AS INTEGER) AS birth_day_int,
            GROUP_CONCAT(unit_id,'-') as unit_ids,
            GROUP_CONCAT(unit_name,'-') as unit_names
        FROM
            unit_profile
        WHERE unit_id < 200000 
        AND unit_profile.unit_id in (SELECT MAX(unit_promotion.unit_id) FROM unit_promotion WHERE unit_id = unit_profile.unit_id)
        GROUP BY birth_month_int, birth_day_int
        ORDER BY birth_month_int, birth_day_int
    """
    )
    suspend fun getBirthdayList(): List<BirthdayData>
}