package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.databinding.ItemToolBinding


class ToolListAdapter :
    ListAdapter<Item, ToolListAdapter.ViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemToolBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemToolBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Item) {
            binding.apply {
                item.text = data.name
                val drawable = ResourcesCompat.getDrawable(
                    MyApplication.getContext().resources,
                    data.resInt, null
                )
                drawable?.setBounds(0, 0, 100, 100)
                item.setCompoundDrawables(drawable, null, null, null)
            }
        }
    }

}

private class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {

    override fun areItemsTheSame(
        oldItem: Item,
        newItem: Item
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Item,
        newItem: Item
    ): Boolean {
        return oldItem.name == newItem.name
    }
}

class Item(
    val name: String,
    val resInt: Int
)