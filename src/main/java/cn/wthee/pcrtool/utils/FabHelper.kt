package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.pageLevel
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.ui.home.CharacterListFragment

/**
 * 悬浮按钮
 */
object FabHelper {

    fun addBackFab(level: Int = 1, post: Boolean = false) {
        //添加返回
        setIcon(R.drawable.ic_left)
        CharacterListFragment.isPostponeEnterTransition = post
        pageLevel = level
    }

    fun setIcon(resId: Int) {
        MainActivity.fabMain.setImageResource(resId)
    }

}