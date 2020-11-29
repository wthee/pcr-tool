package cn.wthee.pcrtool.utils

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.get
import cn.wthee.pcrtool.R

//星级选择
object StarUtil {

    //显示星数
    fun show(context: Context, partent: ViewGroup, num: Int,  size: Int){
        for (i in 0 until num){
            val starView = AppCompatImageView(context)
            starView.setBackgroundResource(R.drawable.ic_star)
            val params = LinearLayout.LayoutParams(size,size)
            starView.layoutParams = params
            partent.addView(starView, i)
        }
    }

    //显示可点击星数
    fun show(context: Context, partent: ViewGroup, num: Int, max: Int, size: Int, onSelect: OnSelect){
        partent.removeAllViews()
        for (i in 0 until num){
            val starView = AppCompatImageView(context)
            starView.setBackgroundResource(R.drawable.ic_star)
            val params = LinearLayout.LayoutParams(size,size)
            starView.layoutParams = params
            partent.addView(starView, i)
        }
        for (i in 0 until  max - num){
            val starView = AppCompatImageView(context)
            starView.setBackgroundResource(R.drawable.ic_star_dark)
            val params = LinearLayout.LayoutParams(size,size)
            starView.layoutParams = params
            partent.addView(starView, i + num)
        }
        for(i in 0 until num){
            partent[i].setOnClickListener {
                for (j in 0 until max) {
                    if (j > i) {
                        partent[j].setBackgroundResource(R.drawable.ic_star_dark)
                    } else {
                        partent[j].setBackgroundResource(R.drawable.ic_star)
                    }
                }
                onSelect.select(i)
            }
        }
    }

    interface OnSelect{
        fun select(index: Int)
    }

}