package cn.wthee.pcrtool.ui.common

import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView

/**
 * 列表页面基类
 */
open class CommonListFragment : Fragment() {

    override fun onResume() {
        super.onResume()
        try {
            val rootView = (view as MotionLayout)
            val toolList = rootView.getChildAt(1) as RecyclerView
            toolList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 0) {
                        rootView.transitionToEnd()
                    }
                }
            })
        } catch (e: Exception) {

        }
    }
}