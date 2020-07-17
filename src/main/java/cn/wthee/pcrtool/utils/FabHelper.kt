package cn.wthee.pcrtool.utils

import androidx.navigation.findNavController
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MainActivity.Companion.canBack
import cn.wthee.pcrtool.MainActivity.Companion.isHome
import cn.wthee.pcrtool.R

object FabHelper {

    fun addBackFab() {
        //添加返回
        setIcon(R.drawable.ic_back)
        isHome = false
    }

    private fun setIcon(resId: Int) {
        MainActivity.fab.setImageResource(resId)
    }

    fun goBack() {
        if (canBack) {
            val activity = ActivityUtil.instance.currentActivity
            setIcon(R.drawable.ic_function)
            activity?.findNavController(R.id.nav_host_fragment)?.navigateUp()
        }
    }

}