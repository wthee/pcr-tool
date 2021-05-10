package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import cn.wthee.pcrtool.data.db.entity.EventStoryDetail
import cn.wthee.pcrtool.data.db.view.DropEvent
import cn.wthee.pcrtool.data.db.view.EventData

/**
 * 活动记录 DAO
 */
@Dao
interface EventDao {

    /**
     * 获取所有活动记录 [EventData]
     */
    @Transaction
    @Query(
        """
            SELECT
                event.*,
                c.title,
                COALESCE( GROUP_CONCAT( f.value, '-' ), "-" ) AS unit_ids,
                COALESCE( GROUP_CONCAT( f.item_name, '-' ), "-" ) AS unit_names 
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
                LEFT JOIN odds_name_data AS e ON event.event_id % 10000 = e.odds_file / 100000 % 10000
                LEFT JOIN item_data AS f ON e.name = f.item_name 
            GROUP BY event.start_time
            ORDER BY event.start_time DESC     
        """
    )
    suspend fun getAllEvents(): List<EventData>


    /**
     * 根据 [storyId] 获取活动剧情列表
     */
    @Query("SELECT * FROM event_story_detail WHERE story_group_id = :storyId")
    suspend fun getStoryDetails(storyId: Int): List<EventStoryDetail>


    /**
     * 获取加倍活动信息
     */
    @Transaction
    @Query(
        """
        SELECT
            COALESCE( GROUP_CONCAT( campaign_category, '-' ), "0" ) AS type,
            value,
            start_time,
            end_time 
        FROM
            campaign_schedule 
        WHERE
            campaign_category IN ( 31, 32, 34, 37, 38, 39, 45 ) 
        GROUP BY
            start_time,
            end_time,
            value 
        ORDER BY
            start_time DESC 
            LIMIT 100 OFFSET 0
    """
    )
    suspend fun getDropEvent(): List<DropEvent>

}