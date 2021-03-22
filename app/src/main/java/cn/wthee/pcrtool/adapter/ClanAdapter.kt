package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.get
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.ClanBattleInfo
import cn.wthee.pcrtool.databinding.ItemClanBinding
import cn.wthee.pcrtool.utils.fillZero
import cn.wthee.pcrtool.utils.getSectionTextColor

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
            ItemClanBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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
                //时间
                val ymd = clan.start_time.split("/")
                title.text = ymd[1]
                title.setTextColor(getSectionTextColor(clan.section))
                //起止日期
                date.text = ymd[0]
                //图片
                val startYear = clan.start_time.substring(0, 4)
                val list = clan.getUnitIdList(1)
                val date = "$startYear 年 ${clan.release_month.toString().fillZero()} 月"
                val adapter = ClanBossIconAdapter(date, clan)
                icons.adapter = adapter
                adapter.submitList(list)
                root.setOnClickListener {
                    try {
                        icons[0].findViewById<AppCompatImageView>(R.id.pic).callOnClick()
                    } catch (e: Exception) {
                    }
                }
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