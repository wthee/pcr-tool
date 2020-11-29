package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.data.model.SkillLoop
import cn.wthee.pcrtool.databinding.ItemSkillLoopBinding


class SkillLoopAllAdapter :
    ListAdapter<SkillLoop, SkillLoopAllAdapter.ViewHolder>(SkillLoopAllDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSkillLoopBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemSkillLoopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(loop: SkillLoop) {
            //设置数据
            binding.apply {
                loopTitle.text = loop.loopTitle
                val adapter = SkillLoopAdapter()
                loopList.adapter = adapter
                adapter.submitList(loop.loopList)
            }
        }
    }

}

private class SkillLoopAllDiffCallback : DiffUtil.ItemCallback<SkillLoop>() {

    override fun areItemsTheSame(
        oldItem: SkillLoop,
        newItem: SkillLoop
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: SkillLoop,
        newItem: SkillLoop
    ): Boolean {
        return oldItem.loopTitle == newItem.loopTitle
    }
}