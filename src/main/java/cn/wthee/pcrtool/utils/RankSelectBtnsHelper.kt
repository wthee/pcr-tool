package cn.wthee.pcrtool.utils

import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.LayoutRankSelectBinding

class RankSelectBtnsHelper(
    private val binding: LayoutRankSelectBinding
) {

    fun initRank(selRank: Int) {
        setRank(selRank)
    }

    fun setOnClickListener(maxRank: Int, onClickListener: OnClickListener) {
        var selRank = maxRank
        binding.rankAdd.setOnClickListener {
            if (selRank != maxRank) {
                selRank++
                if (selRank == maxRank) {
                    it.isEnabled = false
                } else {
                    binding.rankReduce.isEnabled = true
                }
                setRank(selRank)
                onClickListener.onChange(selRank)
            }
        }
        binding.rankReduce.setOnClickListener {
            if (selRank != Constants.CHARACTER_MIN_RANK) {
                selRank--
                if (selRank == 2) {
                    it.isEnabled = false
                } else {
                    binding.rankAdd.isEnabled = true
                }
                setRank(selRank)
                onClickListener.onChange(selRank)
            }
        }
    }

    //设置rank
    private fun setRank(num: Int) {
        binding.apply {
            rank.text = num.toString()
            rank.setTextColor(getRankColor(num))
            rankTitle.setTextColor(getRankColor(num))
        }
    }


    interface OnClickListener {

        fun onChange(rank: Int)
    }
}

//rank 颜色
fun getRankColor(rank: Int): Int {
    val color = when (rank) {
        in 2..3 -> R.color.color_rank_2_3
        in 4..6 -> R.color.color_rank_4_6
        in 7..10 -> R.color.color_rank_7_10
        in 11..17 -> R.color.color_rank_11_17
        in 18..99 -> R.color.color_rank_18
        else -> {
            R.color.color_rank_2_3
        }
    }
    return ResourcesUtil.getColor(color)
}