package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.LeaderboardInfo
import cn.wthee.pcrtool.databinding.ItemLeaderBinding


class CharacterLeaderAdapter :
    ListAdapter<LeaderboardInfo, CharacterLeaderAdapter.ViewHolder>(LeaderDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemLeaderBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemLeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LeaderboardInfo) {
            binding.apply {
                root.animation =
                    AnimationUtils.loadAnimation(
                        MyApplication.context,
                        R.anim.anim_translate_y
                    )
                level.text = data.name
            }
        }
    }

}

private class LeaderDiffCallback : DiffUtil.ItemCallback<LeaderboardInfo>() {

    override fun areItemsTheSame(
        oldItem: LeaderboardInfo,
        newItem: LeaderboardInfo
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: LeaderboardInfo,
        newItem: LeaderboardInfo
    ): Boolean {
        return oldItem == newItem
    }
}