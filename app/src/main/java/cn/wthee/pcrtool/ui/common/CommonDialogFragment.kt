package cn.wthee.pcrtool.ui.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import androidx.fragment.app.DialogFragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.dp
import dagger.hilt.android.AndroidEntryPoint

/**
 * 底部弹窗基类
 */
@AndroidEntryPoint
open class CommonDialogFragment : DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val width = ScreenUtil.getWidth() - 42.dp
            setLayout(width, (width / 0.618f).toInt())
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setWindowAnimations(R.style.DialogAnimation)
            val params = attributes
            params.y = 65.dp
            attributes = params
        }
    }
}