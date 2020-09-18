package cn.wthee.pcrtool.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.databinding.FragmentToolsDialogBinding
import cn.wthee.pcrtool.databinding.ItemToolBinding
import cn.wthee.pcrtool.ui.tool.ToolsDialogFragment
import cn.wthee.pcrtool.ui.tool.ToolsDialogFragment.Companion.toolPosition
import cn.wthee.pcrtool.utils.ToastUtil
import coil.load
import com.google.android.material.transition.MaterialArcMotion
import com.google.android.material.transition.MaterialContainerTransform


class ToolListAdapter(
    private val parent: FragmentToolsDialogBinding
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
        holder.bind(getItem(position), parent)
    }

    class ViewHolder(private val binding: ItemToolBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            data: Item,
            parent: FragmentToolsDialogBinding
        ) {
            binding.apply {
                toolName.text = data.name
                val drawable = ResourcesCompat.getDrawable(
                    MyApplication.getContext().resources,
                    data.resInt, null
                )
                toolIcon.load(drawable)
                root.setOnClickListener {
                    when (adapterPosition) {
                        0 -> {
                            toolPosition = 0
                            //过渡动画
                            val transform = MaterialContainerTransform().apply {
                                startView = it
                                endView = parent.toolHead.root
                                addTarget(endView!!)
                                setPathMotion(MaterialArcMotion())
                                scrimColor = Color.TRANSPARENT
                            }
                            TransitionManager.beginDelayedTransition(parent.root, transform)
                            //布局显示/隐藏
                            parent.toolContent.visibility = View.VISIBLE
                            parent.list.visibility = View.GONE
                            //工具详情头部
                            parent.toolHead.toolIcon.load(drawable)
                            parent.toolHead.toolName.text = data.name

                            ToolsDialogFragment.isShownDetail = true
                        }
                        1 -> {
                            toolPosition = 1
                            //过渡动画
                            ToastUtil.short("更多功能，将在后续更新中实装~")
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