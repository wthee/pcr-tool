package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.ClanBattleInfo
import cn.wthee.pcrtool.databinding.ItemClanBinding
import cn.wthee.pcrtool.utils.fillZero
import cn.wthee.pcrtool.utils.intArrayList

/**
 * 团队战记录列表适配器
 *
 * 列表项布局 [ItemClanBinding]
 *
 * 列表项数据 [ClanBattleInfo]
 */
class ClanAdapter :
    ListAdapter<ClanBattleInfo, ClanAdapter.ViewHolder>(ClanDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemClanBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemClanBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(clan: ClanBattleInfo) {
            //设置数据
            binding.apply {
                root.animation =
                    AnimationUtils.loadAnimation(MyApplication.context, R.anim.anim_list_item)
                //起止日期
                val startDate = clan.start_time.substring(0, 10)
                title.text = startDate
                //时间
                days.text = "${clan.release_month.toString().fillZero()} 月"

                //图片
                val adapter = ClanBossIconAdapter("${startDate.substring(0, 4)} 年 ${days.text}")
                icons.adapter = adapter
                adapter.submitList(clan.enemyIds.intArrayList())

            }
        }


    }

}

private class ClanDiffCallback : DiffUtil.ItemCallback<ClanBattleInfo>() {

    override fun areItemsTheSame(
        oldItem: ClanBattleInfo,
        newItem: ClanBattleInfo
    ): Boolean {
        return oldItem.clan_battle_id == newItem.clan_battle_id
    }

    override fun areContentsTheSame(
        oldItem: ClanBattleInfo,
        newItem: ClanBattleInfo
    ): Boolean {
        return oldItem == newItem
    }
}