package cn.wthee.pcrtool.ui.common

import android.os.Bundle
import cn.wthee.pcrtool.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * 底部弹窗基类
 */
open class CommonBasicDialogFragment : BottomSheetDialogFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // 设置动画
        dialog?.window?.attributes?.windowAnimations = R.style.DialogAnimation
    }
}