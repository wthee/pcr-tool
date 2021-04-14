package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.get
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.ClanBattleInfo
import cn.wthee.pcrtool.databinding.ItemClanBinding
import cn.wthee.pcrtool.ui.tool.clan.ClanFragment
import cn.wthee.pcrtool.utils.fillZero
import cn.wthee.pcrtool.utils.getSectionTextColor

/**
 * 团队战记录列表适配器
 *
 * 列表项布局 [ItemClanBinding]
 *
 * 列表项数据 [ClanBattleInfo]
 */
class ClanAdapter(private val callback: CallBack) :
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
                    AnimationUtils.loadAnimation(root.context, R.anim.anim_list_item)
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

                root.transitionName = clan.clan_battle_id.toString()
                val extra = FragmentNavigatorExtras(
                    root to root.transitionName
                )
                val adapter =
                    ClanBossIconAdapter(date, clan, extra = extra, parentIndex = layoutPosition)
                icons.adapter = adapter
                adapter.submitList(list)
                root.setOnClickListener {
                    try {
                        ClanFragment.clickIndex = layoutPosition
                        icons[0].callOnClick()
                    } catch (e: Exception) {
                    }
                }
            }
            startEnter()
        }

        private fun startEnter() {
            if (absoluteAdapterPosition == ClanFragment.clickIndex) {
                callback.todo()
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