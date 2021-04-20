package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.enums.NumberSelectType
import cn.wthee.pcrtool.databinding.ItemNumberBinding
import cn.wthee.pcrtool.utils.*

/**
 * 数字选择 列表适配器
 *
 * 列表项布局 [ItemNumberBinding]
 *
 * 列表项数据 [Int]
 */
class NumberSelectAdapter(private val type: NumberSelectType, private val callBack: CallBack) :
    ListAdapter<Int, NumberSelectAdapter.ViewHolder>(RankDiffCallback()) {

    private var selectNumber = 0

    fun getSelect() = selectNumber

    fun setSelect(sel: Int) {
        selectNumber = sel
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNumberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemNumberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(r: Int) {
            //设置数据
            binding.apply {
                number.setBackgroundColor(
                    ResourcesUtil.getColor(
                        if (r == selectNumber) R.color.colorHalfAccent else R.color.colorAlpha
                    )
                )
                when (type) {
                    NumberSelectType.RANK -> {
                        number.text = getFormatText(r, Constants.RANK_UPPER)
                        number.setTextColor(getRankColor(r))
                    }
                    NumberSelectType.SECTION -> {
                        number.text = getZhNumberText(r) + "阶段"
                        number.setTextColor(getSectionTextColor(r))
                    }
                    NumberSelectType.LEVEL -> {
                        number.text = r.toString()
                        number.setTextColor(getLevelTextColor(r))
                    }
                }
                root.setOnClickListener {
                    selectNumber = r
                    callBack.todo()
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