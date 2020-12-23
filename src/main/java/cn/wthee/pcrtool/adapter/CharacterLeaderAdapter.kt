package cn.wthee.pcrtool.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.network.model.LeaderboardData
import cn.wthee.pcrtool.databinding.ItemLeaderBinding
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.ResourcesUtil
import coil.load


class CharacterLeaderAdapter(private val context: Context) :
    ListAdapter<LeaderboardData, CharacterLeaderAdapter.ViewHolder>(LeaderDiffCallback()) {
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

    inner class ViewHolder(private val binding: ItemLeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: LeaderboardData) {
            binding.apply {
                root.animation =
                    AnimationUtils.loadAnimation(context, R.anim.anim_translate_y)
                icon.load(data.icon) {
                    placeholder(R.drawable.unknown_gray)
                    error(R.drawable.unknown_gray)
                }
//                name.text = data.name
                all.text = data.all
                all.setTextColor(getColor(data.all))
                pvp.text = data.pvp
                pvp.setTextColor(getColor(data.pvp))
                clan.text = data.clan
                clan.setTextColor(getColor(data.clan))
                tower.text = data.tower
                tower.setTextColor(getColor(data.tower))

                root.setOnClickListener {
                    BrowserUtil.open(context, data.url)
                }
            }
        }

        fun getColor(lv: String) = ResourcesUtil.getColor(
            when (lv) {
                "SSS" -> R.color.color_rank_18
                "SS" -> R.color.color_rank_11_17
                "S" -> R.color.color_rank_7_10
                "A" -> R.color.color_rank_4_6
                "B" -> R.color.color_rank_2_3
                "C" -> R.color.cool_apk
                else -> R.color.cool_apk
            }
        )

    }

}

private class LeaderDiffCallback : DiffUtil.ItemCallback<LeaderboardData>() {

    override fun areItemsTheSame(
        oldItem: LeaderboardData,
        newItem: LeaderboardData
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: LeaderboardData,
        newItem: LeaderboardData
    ): Boolean {
        return oldItem == newItem
    }
}