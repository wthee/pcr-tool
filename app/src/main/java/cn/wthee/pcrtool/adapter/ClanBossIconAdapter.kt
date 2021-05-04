package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.navigation.Navigator
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.ClanBattleInfo
import cn.wthee.pcrtool.data.view.ClanBossTargetInfo
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.ui.tool.clan.ClanFragment
import cn.wthee.pcrtool.ui.tool.clan.ClanFragmentDirections
import cn.wthee.pcrtool.utils.Constants
import cn.wthee.pcrtool.utils.ResourcesUtil
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
    private val clan: ClanBattleInfo,
    private val extra: Navigator.Extras? = null,
    private val parentIndex: Int = 0,
    private val callBack: CallBack? = null
) : ListAdapter<ClanBossTargetInfo, ClanBossIconAdapter.ViewHolder>(ClanIconListDiffCallback()) {
    private var selectedIndex = 0

    fun setSelectedIndex(index: Int) {
        selectedIndex = index
    }

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
        val image = holder.itemView.findViewById<AppCompatImageView>(R.id.pic)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            image?.foreground = ColorDrawable(
                ResourcesUtil.getColor(
                    if (MainActivity.pageLevel == 2 && selectedIndex == position)
                        R.color.colorHalfAccent
                    else
                        R.color.colorAlpha
                )
            )
        }
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(target: ClanBossTargetInfo) {
            //设置数据
            binding.apply {
                //角色名
                name.visibility = View.GONE
                //图片
                val picUrl = Constants.UNIT_ICON_URL + target.unitId + Constants.WEBP
                pic.load(picUrl) {
                    placeholder(R.drawable.unknown_gray)
                    error(R.drawable.unknown_gray)
                }
                //点击监听
                root.setOnClickListener {
                    ClanFragment.clickIndex = parentIndex
                    if (MainActivity.pageLevel == 1) {
                        //打开详情
                        val action = ClanFragmentDirections.actionClanFragmentToClanPagerFragment(
                            date,
                            layoutPosition,
                            clan
                        )
                        if (extra != null) {
                            root.findNavController().navigate(action, extra)
                        } else {
                            root.findNavController().navigate(action)
                        }
                    } else {
                        //切换页面
                        setSelectedIndex(layoutPosition)
                        notifyDataSetChanged()
                        callBack?.todo(layoutPosition)
                    }
                }
            }
        }
    }

}

private class ClanIconListDiffCallback : DiffUtil.ItemCallback<ClanBossTargetInfo>() {

    override fun areItemsTheSame(
        oldItem: ClanBossTargetInfo,
        newItem: ClanBossTargetInfo
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: ClanBossTargetInfo,
        newItem: ClanBossTargetInfo
    ): Boolean {
        return oldItem == newItem
    }
}