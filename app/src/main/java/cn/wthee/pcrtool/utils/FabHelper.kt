package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.R

/**
 * TODO 悬浮按钮返回
 */
object FabHelper {

    /**
     * 根据页面等级 [level]，是否加载动画 [post]，设置页面返回按钮
     */
    fun addBackFab(level: Int = 1, post: Boolean = false) {
        //添加返回
        setIcon(R.drawable.ic_left)
//        CharacterListFragment.isPostponeEnterTransition = post
//        pageLevel = level
    }

    /**
     * 修改按钮图标
     */
    fun setIcon(resId: Int) {
//        MainActivity.fabMain.setImageResource(resId)
    }

}