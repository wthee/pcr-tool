package cn.wthee.pcrtool.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
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
                c.*,
                d.title,
                COALESCE(GROUP_CONCAT( f.value, '-' ), "-") AS unit_ids,
                COALESCE(GROUP_CONCAT( f.item_name, '-' ), "-") AS unit_names
            FROM
                (
                SELECT
                    * 
                FROM
                    (
                    SELECT
                        a.story_id / 1000 AS story_id,
                        a.event_id,
                        substr( a.start_time, 0, 11 ) AS start_time,
                        substr( a.end_time, 0, 11 ) AS end_time 
                    FROM
                        ( SELECT * FROM event_top_adv GROUP BY event_id ) AS a UNION
                    SELECT
                        b.story_group_id AS story_id,
                        b.value AS event_id,
                        substr( b.start_time, 0, 11 ) AS start_time,
                        substr( b.end_time, 0, 11 ) AS end_time 
                    FROM
                        event_story_data AS b 
                    WHERE
                        b.story_group_id NOT IN ( SELECT story_id / 1000 FROM event_top_adv ) 
                    ) 
                ORDER BY
                    start_time 
                ) AS c
                LEFT JOIN event_story_data AS d ON c.story_id = d.story_group_id
                LEFT JOIN odds_name_data AS e ON c.event_id % 10000 = e.odds_file / 100000 % 10000
                LEFT JOIN item_data AS f ON e.name = f.item_name 
            GROUP BY
                event_id 
            ORDER BY
                start_time DESC
        """
    )
    suspend fun getAllEvents(): List<EventData>
}