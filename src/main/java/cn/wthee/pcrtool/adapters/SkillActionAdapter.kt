package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.entity.SkillAction
import cn.wthee.pcrtool.databinding.ItemSkillActionBinding


class SkillActionAdapter :
    ListAdapter<SkillAction, SkillActionAdapter.ViewHolder>(ActionDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSkillActionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemSkillActionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(act: SkillAction) {
            binding.apply {
                action.animation =
                    AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.anim_scale)
                action.text = act.getFixedDesc()
            }
        }
    }

}

private class ActionDiffCallback : DiffUtil.ItemCallback<SkillAction>() {

    override fun areItemsTheSame(
        oldItem: SkillAction,
        newItem: SkillAction
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: SkillAction,
        newItem: SkillAction
    ): Boolean {
        return oldItem == newItem
    }
}