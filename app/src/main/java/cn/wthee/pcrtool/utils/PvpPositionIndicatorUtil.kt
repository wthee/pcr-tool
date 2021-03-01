package cn.wthee.pcrtool.utils

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ViewPositionIndicatorBinding


fun ViewPositionIndicatorBinding.init(list: RecyclerView, floatWindow: Boolean) {
    val sizeDp = if (floatWindow) 24.dp else 28.dp
    val indexs = arrayListOf(index0, index1, index2)
    val indexForegrounds = arrayListOf(indexForeground0, indexForeground1, indexForeground2)
    indexs.forEachIndexed { index, appCompatImageView ->
        appCompatImageView.setOnClickListener {
            check(appCompatImageView.id)
            list.scrollToPosition(index)
        }

        val params = appCompatImageView.layoutParams
        val params0 = indexForegrounds[index].layoutParams
        params.width = sizeDp - 3.dp
        params.height = sizeDp - 3.dp
        params0.width = sizeDp
        params0.height = sizeDp
        appCompatImageView.layoutParams = params
        indexForegrounds[index].layoutParams = params
    }

    list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val l = recyclerView.layoutManager as LinearLayoutManager
            val id =
                when (l.findLastVisibleItemPosition()) {
                    0 -> R.id.index_0
                    1 -> R.id.index_1
                    else -> R.id.index_2
                }
            this@init.check(id)
        }
    })
}

private fun ViewPositionIndicatorBinding.check(id: Int) {
    this.apply {
        when (id) {
            R.id.index_0 -> {
                indexForeground0.visibility = View.VISIBLE
                indexForeground1.visibility = View.INVISIBLE
                indexForeground2.visibility = View.INVISIBLE
            }
            R.id.index_1 -> {
                indexForeground0.visibility = View.INVISIBLE
                indexForeground1.visibility = View.VISIBLE
                indexForeground2.visibility = View.INVISIBLE
            }
            R.id.index_2 -> {
                indexForeground0.visibility = View.INVISIBLE
                indexForeground1.visibility = View.INVISIBLE
                indexForeground2.visibility = View.VISIBLE
            }
        }
    }
}

