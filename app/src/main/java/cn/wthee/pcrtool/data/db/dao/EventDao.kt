package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.SkipQueryVerification
import androidx.room.Transaction
import cn.wthee.pcrtool.data.db.view.CalendarEvent
import cn.wthee.pcrtool.data.db.view.EventData
import cn.wthee.pcrtool.data.db.view.EventStoryDetail
import cn.wthee.pcrtool.data.db.view.FreeGachaInfo

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
                event.*,
                c.title,
                COALESCE(e.unit_ids, "") as unit_ids
            FROM
                (
                    SELECT
                        a.event_id,
                        (( CASE WHEN a.original_event_id = 0 THEN a.event_id ELSE a.original_event_id END ) % 10000 + 5000 ) AS story_id,
                        a.start_time AS start_time,
                        a.end_time AS end_time 
                    FROM
                        hatsune_schedule AS a UNION
                    SELECT
                        b.event_id,
                        b.story_id / 1000 AS story_id,
                        b.start_time AS start_time,
                        b.end_time AS end_time 
                    FROM
                        event_top_adv AS b 
                    WHERE
                        b.event_id > 20000 
                    GROUP BY
                        b.event_id 
                        
                ) AS event
                LEFT JOIN event_story_data AS c ON c.story_group_id = event.story_id
                LEFT JOIN 
                (
                    SELECT
                        d.story_group_id,
                        GROUP_CONCAT( d.reward_id_2, '-' ) AS unit_ids
                    FROM
                        event_story_detail AS d 
                    GROUP BY
                        d.story_group_id
                ) as e ON c.story_group_id = e.story_group_id
            GROUP BY event.start_time
            ORDER BY event.start_time DESC     
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
}