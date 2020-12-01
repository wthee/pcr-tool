package cn.wthee.pcrtool.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import cn.wthee.pcrtool.data.view.EventData

@Dao
interface EventDao {

    @Transaction
    @Query(
        """
            SELECT
                c.*,
                d.title 
            FROM
                (
                SELECT
                    * 
                FROM
                    (
                    SELECT
                        a.story_id / 1000 AS story_id,
                        substr( a.start_time, 0, 11 ) AS start_time,
                        substr( a.end_time, 0, 11 ) AS end_time 
                    FROM
                        ( SELECT * FROM event_top_adv GROUP BY event_id ) AS a UNION
                    SELECT
                        b.story_group_id AS story_id,
                        substr( b.start_time, 0, 11 ) AS start_time,
                        substr( b.end_time, 0, 11 ) AS end_time 
                    FROM
                        event_story_data AS b 
                        WHERE b.story_group_id not in (SELECT story_id/1000 FROM event_top_adv)
                    ) 
                ORDER BY
                    start_time 
                ) AS c
                LEFT JOIN event_story_data AS d ON c.story_id = d.story_group_id
                ORDER BY start_time desc
        """
    )
    suspend fun getAllEvents(): List<EventData>
}