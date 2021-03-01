package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.AttrValue
import cn.wthee.pcrtool.data.model.int
import cn.wthee.pcrtool.databinding.ItemCharacterAttrBinding

/**
 * 角色面板属性列表适配器
 *
 * 列表项布局 [ItemCharacterAttrBinding]
 *
 * 列表项数据 [AttrValue]
 */
class CharacterAttrAdapter :
    ListAdapter<AttrValue, CharacterAttrAdapter.ViewHolder>(CharacterAttrDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCharacterAttrBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCharacterAttrBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: AttrValue) {
            binding.apply {
                value.animation =
                    AnimationUtils.loadAnimation(MyApplication.context, R.anim.anim_scale)
                titleAttr.text = data.title
                value.text = data.value.int.toString()
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