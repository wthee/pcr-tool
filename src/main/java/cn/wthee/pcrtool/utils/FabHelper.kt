package cn.wthee.pcrtool.utils

import androidx.fragment.app.FragmentActivity
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
        MainActivity.fabMain.setImageResource(resId)
    }

    fun goBack(activity: FragmentActivity) {
        if (canBack && !isHome) {
            setIcon(R.drawable.ic_function)
            activity.findNavController(R.id.nav_host_fragment).navigateUp()
        }
    }

}