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

    /**
     * 获取颜色
     */
    fun getColor(resId: Int) = ResourcesCompat.getColor(resource, resId, null)

    /**
     * 获取图片
     */
    fun getDrawable(resId: Int) = ResourcesCompat.getDrawable(resource, resId, null)

    /**
     * 设置标题风格
     */
    fun MaterialTextView.setTitleBackground(resId: Int) {
        val drawable = getDrawable(R.drawable.bg_text_view)
        drawable?.setTint(getColor(resId))
        this.background = drawable
    }
}