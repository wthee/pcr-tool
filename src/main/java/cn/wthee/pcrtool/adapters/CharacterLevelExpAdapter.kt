package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.entity.CharacterExperience
import cn.wthee.pcrtool.databinding.ItemLevelExpBinding


class CharacterLevelExpAdapter :
    ListAdapter<CharacterExperience, CharacterLevelExpAdapter.ViewHolder>(LevelDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLevelExpBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemLevelExpBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: CharacterExperience) {
            binding.apply {
                root.animation =
                    AnimationUtils.loadAnimation(MyApplication.getContext(), R.anim.anim_scale)
                level.text = "Lv ${data.level.toString()}"
                exp.text = data.exp.toString()
            }
        }
    }

}

private class LevelDiffCallback : DiffUtil.ItemCallback<CharacterExperience>() {

    override fun areItemsTheSame(
        oldItem: CharacterExperience,
        newItem: CharacterExperience
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CharacterExperience,
        newItem: CharacterExperience
    ): Boolean {
        return oldItem.level == newItem.level
    }
}