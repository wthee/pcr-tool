package cn.wthee.pcrtool.ui.common

import android.os.Bundle
import android.widget.FrameLayout
import cn.wthee.pcrtool.R
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * 底部弹窗基类
 */
open class CommonBasicDialogFragment(private val expend: Boolean = false) :
    BottomSheetDialogFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 设置动画
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }

    override fun onStart() {
        super.onStart()
        if (expend) {
            val bottomSheet =
                dialog?.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                //默认展开
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }
}