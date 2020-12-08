package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.RankCompareData
import cn.wthee.pcrtool.data.model.int
import cn.wthee.pcrtool.databinding.ItemRankCompareAttrBinding
import cn.wthee.pcrtool.utils.ResourcesUtil


class RankCompareAdapter(private val hideTitle: Boolean = false) :
    ListAdapter<RankCompareData, RankCompareAdapter.ViewHolder>(RankCompareDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRankCompareAttrBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemRankCompareAttrBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: RankCompareData) {
            binding.apply {
                if (hideTitle) {
                    titleAttr.visibility = View.GONE
                }
                titleAttr.text = data.title
                value0.text = data.attr0.int.toString()
                value1.text = data.attr1.int.toString()
                valueCompare.text = data.attrCompare.int.toString()
                valueCompare.setTextColor(
                    ResourcesUtil.getColor(
                        when {
                            data.attrCompare < 0 -> R.color.red
                            data.attrCompare > 0 -> R.color.cool_apk
                            else -> R.color.text
                        }
                    )
                )
            }
        }
    }

}

private class RankCompareDiffCallback : DiffUtil.ItemCallback<RankCompareData>() {

    override fun areItemsTheSame(
        oldItem: RankCompareData,
        newItem: RankCompareData
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: RankCompareData,
        newItem: RankCompareData
    ): Boolean {
        return oldItem == newItem
    }
}