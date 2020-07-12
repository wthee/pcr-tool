package cn.wthee.pcrtool.utils

import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R

object FabHelper {

    fun addBackFab(fragment: Fragment) {
        //添加返回按钮
        setIcon(R.drawable.ic_back)
        MainActivity.fab.apply {
            setOnClickListener {
                goBack(fragment)
            }
            text = fragment.getString(R.string.back)
        }
    }

    private fun setIcon(resId: Int) {
        MainActivity.fab.setIconResource(resId)
    }

    fun goBack(fragment: Fragment) {
        setIcon(R.drawable.ic_function)
        fragment.findNavController().navigateUp()
        MainActivity.fab.text = fragment.getString(R.string.function)
    }

}