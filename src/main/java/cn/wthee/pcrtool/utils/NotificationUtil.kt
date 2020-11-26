package cn.wthee.pcrtool.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R

object NotificationUtil {

    private const val channelId = "1"
    val context: Context = MyApplication.context
    val notificationManager: NotificationManager =
        context.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager

    //前台通知
    fun createForeground(service: Service, content: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        val notification = NotificationCompat.Builder(context, channelId)
            .setContentTitle(content)
            .setSmallIcon(R.mipmap.ic_logo)
        service.startForeground(1, notification.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val channel = NotificationChannel(channelId, "PCR Tool", NotificationManager.IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }
}