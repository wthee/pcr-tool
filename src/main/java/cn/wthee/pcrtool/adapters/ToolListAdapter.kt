package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.databinding.FragmentToolsDialogBinding
import cn.wthee.pcrtool.databinding.ItemToolBinding
import cn.wthee.pcrtool.databinding.LayoutLevelExpBinding
import cn.wthee.pcrtool.ui.main.CharacterViewModel
import cn.wthee.pcrtool.ui.setting.ToolsDialogFragment
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch


class ToolListAdapter(
    private val fragment: ToolsDialogFragment,
    private val parent: FragmentToolsDialogBinding,
    private val characterViewModel: CharacterViewModel
) :
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
        holder.bind(getItem(position), fragment, parent, characterViewModel)
    }

    class ViewHolder(private val binding: ItemToolBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: Item,
            fragment: ToolsDialogFragment,
            parent: FragmentToolsDialogBinding,
            characterViewModel: CharacterViewModel
        ) {
            binding.apply {
                item.text = data.name
                val drawable = ResourcesCompat.getDrawable(
                    MyApplication.getContext().resources,
                    data.resInt, null
                )
                drawable?.setBounds(0, 0, 100, 100)
                item.setCompoundDrawables(drawable, null, null, null)
                item.setOnClickListener {
                    when (adapterPosition) {
                        0 -> {
                            val toolContent = LayoutLevelExpBinding.inflate(fragment.layoutInflater)
                            MainScope().launch {
                                val list = characterViewModel.getLevelExp()
                                val adapter = CharacterLevelExpAdapter()
                                toolContent.listLevel.adapter = adapter
                                adapter.submitList(list)
                            }
                            parent.toolContent.addView(toolContent.root)
                        }
                    }
                    parent.list.isNestedScrollingEnabled = false
                }
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