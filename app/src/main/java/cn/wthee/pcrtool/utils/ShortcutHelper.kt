package cn.wthee.pcrtool.utils

import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.DatabaseUpdater
import cn.wthee.pcrtool.ui.MainActivity

class ShortcutHelper(private val context: Context) {

    fun create() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            val shortcutManager =
                context.getSystemService(ShortcutManager::class.java)
            val pvpShortcut = getShortcut(
                "open_tool_pvp",
                ResourcesUtil.getString(R.string.tool_pvp),
                R.drawable.ic_pvp,
                "tool://pvp"
            )

            val newsShortcut = getShortcut(
                "open_tool_news",
                ResourcesUtil.getString(R.string.tool_news),
                R.drawable.ic_news,
                "tool://news"
            )

            val calShortcut = if (DatabaseUpdater.getDatabaseType() == 1) {
                getShortcut(
                    "open_tool_calendar",
                    ResourcesUtil.getString(R.string.tool_calendar_title),
                    R.drawable.ic_calendar,
                    "tool://calendar"
                )
            } else {
                getShortcut(
                    "open_tool_calendar_jp",
                    ResourcesUtil.getString(R.string.tool_calendar_title_jp),
                    R.drawable.ic_calendar,
                    "tool://calendar_jp"
                )
            }
            shortcutManager!!.dynamicShortcuts = listOf(pvpShortcut, newsShortcut, calShortcut)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N_MR1)
    private fun getShortcut(id: String, label: String, iconId: Int, action: String): ShortcutInfo {
        // <shortcut
        //        android:enabled="true"
        //        android:icon="@drawable/ic_pvp"
        //        android:shortcutId="open_tool_pvp"
        //        android:shortcutShortLabel="@string/tool_pvp">
        //        <intent
        //            android:action="tool://pvp"
        //            android:data="tool://pvp"
        //            android:targetClass="cn.wthee.pcrtool.MainActivity"
        //            android:targetPackage="cn.wthee.pcrtool" />
        //        <categories android:name="android.shortcut.conversation" />
        //    </shortcut>
        val intent = Intent().apply {
            this.action = Intent.ACTION_VIEW
            data = Uri.parse(action)
            setClass(context, MainActivity::class.java)
//            setClassName(context, "cn.wthee.pcrtool.MainActivity")
        }
        return ShortcutInfo.Builder(context, id)
            .setShortLabel(label)
            .setCategories(setOf("android.shortcut.conversation"))
            .setIcon(Icon.createWithResource(context, iconId))
            .setIntent(intent)
            .build()
    }

}