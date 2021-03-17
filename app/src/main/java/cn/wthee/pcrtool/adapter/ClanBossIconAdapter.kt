package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.ClanBattleInfo
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.utils.Constants
import coil.load

/**
 * 角色图标列表适配器
 *
 * 列表项布局 [ItemCommonBinding]
 *
 * 列表项数据 [Int] unit_id
 */
class ClanBossIconAdapter(
    private val date: String,
    private val clan: ClanBattleInfo
) :
    ListAdapter<Int, ClanBossIconAdapter.ViewHolder>(ClanIconListDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCommonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(uid: Int) {
            //设置数据
            binding.apply {
                //角色名
                name.visibility = View.GONE
                //图片
                val picUrl = Constants.UNIT_ICON_URL + uid + Constants.WEBP
                pic.load(picUrl) {
                    placeholder(R.drawable.unknown_gray)
                    error(R.drawable.unknown_gray)
                }
                pic.setOnClickListener {
                    if (MainActivity.pageLevel == 1) {
                        //打开详情页
                        val bundle = Bundle()
                        bundle.putString(Constants.CLAN_DATE, date)
                        bundle.putInt(Constants.CLAN_BOSS_NO, layoutPosition)
                        bundle.putSerializable(Constants.CLAN_DATA, clan)
                        root.findNavController().navigate(
                            R.id.action_clanFragment_to_clanDetailFragment,
                            bundle,
                            null,
                            null
                        )
                    } else {
                        //切换页面，选中效果
                    }

                }
            }
        }
    }

}

private class ClanIconListDiffCallback : DiffUtil.ItemCallback<Int>() {

    override fun areItemsTheSame(
        oldItem: Int,
        newItem: Int
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Int,
        newItem: Int
    ): Boolean {
        return oldItem == newItem
    }
}