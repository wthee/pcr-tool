package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.view.CharacterExperienceAll
import cn.wthee.pcrtool.databinding.ItemLevelExpBinding


class CharacterLevelExpAdapter() :
    ListAdapter<CharacterExperienceAll, CharacterLevelExpAdapter.ViewHolder>(LevelDiffCallback()) {
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
        fun bind(data: CharacterExperienceAll) {
            binding.apply {
                root.animation =
                    AnimationUtils.loadAnimation(
                        MyApplication.getContext(),
                        R.anim.anim_translate_y
                    )
                level.text = data.level.toString()
                expAbs.text = data.expUnitAbs.toString()
                expTeamAbs.text = data.expTeamAbs.toString()
            }
        }
    }

}

private class LevelDiffCallback : DiffUtil.ItemCallback<CharacterExperienceAll>() {

    override fun areItemsTheSame(
        oldItem: CharacterExperienceAll,
        newItem: CharacterExperienceAll
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: CharacterExperienceAll,
        newItem: CharacterExperienceAll
    ): Boolean {
        return oldItem.level == newItem.level
    }
}