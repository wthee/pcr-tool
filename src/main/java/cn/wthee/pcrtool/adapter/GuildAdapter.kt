package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.GuildData
import cn.wthee.pcrtool.databinding.ItemGuildBinding

/**
 * 公会列表适配器
 *
 * 列表项布局 [ItemGuildBinding]
 *
 * 列表项数据 [GuildData]
 */
class GuildAdapter :
    ListAdapter<GuildData, GuildAdapter.ViewHolder>(GuildDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemGuildBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemGuildBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(guild: GuildData) {
            //设置数据
            binding.apply {
                root.animation =
                    AnimationUtils.loadAnimation(MyApplication.context, R.anim.anim_list_item)
                //公会名
                title.text = guild.guildName
                //介绍
                guild.description.let {
                    if (it.isEmpty()) {
                        subTitle.visibility = View.GONE
                    }
                    subTitle.text = guild.description
                }
                //角色图片
                val adapter = IconListAdapter()
                icons.adapter = adapter
                adapter.submitList(guild.getMemberIds())
            }
        }
    }

}

private class GuildDiffCallback : DiffUtil.ItemCallback<GuildData>() {

    override fun areItemsTheSame(
        oldItem: GuildData,
        newItem: GuildData
    ): Boolean {
        return oldItem.guildId == newItem.guildId
    }

    override fun areContentsTheSame(
        oldItem: GuildData,
        newItem: GuildData
    ): Boolean {
        return oldItem == newItem
    }
}