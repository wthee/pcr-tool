package cn.wthee.pcrtool.utils

import android.content.Intent
import cn.wthee.pcrtool.R

/**
 * 分享
 */
object ShareIntentUtil {

    /**
     * 分享文本
     */
    fun text(str: String) {
        var shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            str
        )
        shareIntent = Intent.createChooser(shareIntent, getString(R.string.share_to))
        ActivityHelper.instance.currentActivity?.startActivity(shareIntent)
    }
}