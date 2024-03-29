package cn.wthee.pcrtool.utils

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R

/**
 * 再浏览器中打开
 */
object BrowserUtil {

    /**
     * 在浏览器中打开 [url]
     *
     * @param url 链接
     */
    fun open(url: String) {
        val mContext = MyApplication.context
        try {
            val builder = CustomTabsIntent.Builder()
                .setStartAnimations(mContext, R.anim.slide_in, android.R.anim.fade_out)
                .setExitAnimations(mContext, android.R.anim.fade_in, android.R.anim.fade_out)
                .setInitialActivityHeightPx(500, CustomTabsIntent.ACTIVITY_HEIGHT_ADJUSTABLE)
                .setShowTitle(true)

            val customTabsIntent = builder.build()
            customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            customTabsIntent.launchUrl(mContext, url.toUri())
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.data = Uri.parse(url)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            mContext.startActivity(
                Intent.createChooser(
                    intent,
                    getString(R.string.open_browser)
                )
            )
        }
    }

}