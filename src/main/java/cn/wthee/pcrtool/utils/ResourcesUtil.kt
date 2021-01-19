package cn.wthee.pcrtool.utils

import android.content.res.Resources
import androidx.core.content.res.ResourcesCompat
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import com.google.android.material.textview.MaterialTextView

/**
 * 应用内资源获取
 */
object ResourcesUtil {

    private val resource: Resources = MyApplication.context.resources

    fun getColor(resId: Int) = ResourcesCompat.getColor(resource, resId, null)

    fun getDrawable(resId: Int) = ResourcesCompat.getDrawable(resource, resId, null)

    fun MaterialTextView.setTitleBackground(resId: Int) {
        val drawable = getDrawable(R.drawable.bg_text_view)
        drawable?.setTint(getColor(resId))
        this.background = drawable
    }
}