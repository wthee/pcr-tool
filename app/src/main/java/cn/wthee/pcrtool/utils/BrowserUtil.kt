package cn.wthee.pcrtool.utils

import android.content.Intent
import android.net.Uri
import androidx.browser.customtabs.CustomTabsIntent
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R

object BrowserUtil {

    /**
     * 在浏览器中打开 [url]
     *
     * @param url 链接
     */
    fun open(url: String) {
        val mContext = MyApplication.context
        try {
            val builder = CustomTabsIntent.Builder().apply {
                setStartAnimations(mContext, R.anim.fade_in, R.anim.fade_out)
                setExitAnimations(mContext, R.anim.fade_out, R.anim.fade_in)
                setShowTitle(true)
            }
            val customTabsIntent = builder.build()
            customTabsIntent.intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            customTabsIntent.launchUrl(mContext, Uri.parse(url))
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