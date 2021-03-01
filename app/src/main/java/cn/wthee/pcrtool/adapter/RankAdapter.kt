package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ItemRankBinding
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.getRankColor
import cn.wthee.pcrtool.utils.getRankText

/**
 * Rank 列表适配器
 *
 * 列表项布局 [ItemRankBinding]
 *
 * 列表项数据 [Int]
 */
class RankAdapter(private val dialog: DialogFragment) :
    ListAdapter<Int, RankAdapter.ViewHolder>(RankDiffCallback()) {

    private var selectedRank = 0

    fun getRank() = selectedRank

    fun setRank(sel: Int) {
        selectedRank = sel
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRankBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemRankBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(r: Int) {
            //设置数据
            binding.apply {
                rank.setBackgroundColor(
                    ResourcesUtil.getColor(
                        if (r == selectedRank) R.color.colorHalfAccent else R.color.colorAlpha
                    )
                )

                rank.text = getRankText(r)
                rank.setTextColor(getRankColor(r))
                root.setOnClickListener {
                    selectedRank = r
                    dialog.dismiss()
                }
            }
        }
    }

}

private class RankDiffCallback : DiffUtil.ItemCallback<Int>() {

    override fun areItemsTheSame(
        oldItem: Int,
        newItem: Int
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Int,
        newItem: Int
    ): Boolean {
        return oldItem == newItem
    }
}