package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.databinding.ItemAttrBinding
import cn.wthee.pcrtool.utils.int

/**
 * 角色面板属性列表适配器
 *
 * 列表项布局 [ItemAttrBinding]
 *
 * 列表项数据 [AttrValue]
 */
class AttrAdapter(private val titleWeight: Float = 1f, private val valueWeight: Float = 1f) :
    ListAdapter<AttrValue, AttrAdapter.ViewHolder>(CharacterAttrDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAttrBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemAttrBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AttrValue) {
            binding.apply {
                val titleParams = titleAttr.layoutParams as LinearLayout.LayoutParams
                titleParams.weight = titleWeight
                titleAttr.layoutParams = titleParams

                val valueParams = value.layoutParams as LinearLayout.LayoutParams
                valueParams.weight = valueWeight
                value.layoutParams = valueParams

                titleAttr.text = data.title
                value.text = if (data.value > 1000000) {
                    "${data.value.int / 10000}万"
                } else {
                    data.value.int.toString()
                }
            }
        }
    }

}

private class CharacterAttrDiffCallback : DiffUtil.ItemCallback<AttrValue>() {

    override fun areItemsTheSame(
        oldItem: AttrValue,
        newItem: AttrValue
    ): Boolean {
        return oldItem.title == newItem.title
    }

    override fun areContentsTheSame(
        oldItem: AttrValue,
        newItem: AttrValue
    ): Boolean {
        return oldItem == newItem
    }
}