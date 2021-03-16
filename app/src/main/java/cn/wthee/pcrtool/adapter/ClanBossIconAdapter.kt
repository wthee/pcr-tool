package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ItemClanBinding
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
    private val boss5: String,
    private val date: String,
    private val parentBinding: ItemClanBinding
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
                //角色图片
                val picUrl = Constants.UNIT_ICON_URL + uid + Constants.WEBP
                pic.load(picUrl) {
                    placeholder(R.drawable.unknown_gray)
                    error(R.drawable.unknown_gray)
                }
                //fixme 优化5王显示效果
                if (layoutPosition == 0) {
                    parentBinding.bossIcon.load(Constants.UNIT_ICON_URL + boss5 + Constants.WEBP)
                }

                //角色名
                name.visibility = View.GONE
                pic.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putInt(Constants.UID, uid)
                    bundle.putString(Constants.CLAN_DATE, date)
                    bundle.putInt(Constants.CLAN_BOSS_NO, layoutPosition + 1)
                    //二级页面
                    MainActivity.pageLevel = 2
//                    root.findNavController().navigate(
//                        R.id.action_global_characterPagerFragment,
//                        bundle,
//                        null,
//                        null
//                    )
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