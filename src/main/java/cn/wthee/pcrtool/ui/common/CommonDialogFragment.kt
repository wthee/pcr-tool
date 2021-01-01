package cn.wthee.pcrtool.ui.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import androidx.fragment.app.DialogFragment
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.utils.ScreenUtil
import cn.wthee.pcrtool.utils.dp

/**
 * 底部弹窗基类
 */
open class CommonDialogFragment : DialogFragment() {

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(ScreenUtil.getWidth() - 42.dp, ScreenUtil.getHeight() / 2)
            setGravity(Gravity.BOTTOM)
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setWindowAnimations(R.style.DialogAnimation)
            val params = attributes
            params.y = 15.dp
            attributes = params
        }
    }
}