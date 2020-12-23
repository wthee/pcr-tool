package cn.wthee.pcrtool.utils

import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton

object RecyclerViewHelper {

    fun RecyclerView.setScrollToTopListener(fab: ExtendedFloatingActionButton) {
        fab.hide()
        //滚动监听
        addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.canScrollVertically(-1)) {
                    //滚动到顶部之前，显示回到顶部按钮
                    fab.show()
                } else {
                    fab.hide()
                }
            }
        })
        //回到顶部
        fab.setOnClickListener {
            smoothScrollToPosition(0)
        }
    }
}