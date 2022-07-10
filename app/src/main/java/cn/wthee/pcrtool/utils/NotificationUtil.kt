package cn.wthee.pcrtool.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import cn.wthee.pcrtool.R

object NotificationUtil {

    /**
     * 创建通知消息
     */
    fun createNotice(
        context: Context,
        channelId: String,
        channelName: String,
        noticeTitle: String,
        notificationManager: NotificationManager
    ): NotificationCompat.Builder {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
            notificationManager.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(context, channelId)
            .setContentTitle(noticeTitle)
            .setTicker(noticeTitle)
            .setSmallIcon(R.drawable.ic_logo)
    }

}