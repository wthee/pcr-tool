package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.entity.PvpCharacterData
import cn.wthee.pcrtool.databinding.ItemCharacterIconBinding
import cn.wthee.pcrtool.ui.tool.pvp.ToolPvpFragment
import cn.wthee.pcrtool.utils.Constants.UNIT_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.ToastUtil
import coil.load


class PvpCharactertAdapter :
    ListAdapter<PvpCharacterData, PvpCharactertAdapter.ViewHolder>(PvpDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCharacterIconBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class ViewHolder(private val binding: ItemCharacterIconBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PvpCharacterData) {
            //设置数据
            binding.apply {
                val ctx = MyApplication.getContext()
                //加载动画
                itemPic.animation =
                    AnimationUtils.loadAnimation(ctx, R.anim.anim_scale)
                //名称
                name.text = if (data.position == 999) "未选择" else data.position.toString()
                //加载图片
                if (data.unitId == 0) {
                    //默认
                    itemPic.load(R.drawable.unknow_gray)
                } else {
                    //角色
                    val picUrl = UNIT_ICON_URL + data.getFixedId() + WEBP
                    itemPic.load(picUrl) {
                        error(R.drawable.unknow_gray)
                        placeholder(R.drawable.load_mini)
                    }
                }
                //设置点击跳转
                root.setOnClickListener {
                    ToolPvpFragment.selects.apply {
                        val empty = PvpCharacterData(0, 999)
                        if (size == 5 && !contains(empty) && !contains(data)) {
                            ToastUtil.short("已选择五名角色，无法继续添加！")
                            return@setOnClickListener
                        }
                        if (!contains(data)) {
                            //添加角色
                            add(data)
                            if (contains(empty)) {
                                remove(empty)
                            }
                        } else {
                            //移除角色
                            remove(data)
                            add(empty)
                        }
                        sortByDescending { it.position }
                    }
                    ToolPvpFragment.pvpCharactertAdapter.apply {
                        submitList(ToolPvpFragment.selects)
                        notifyDataSetChanged()
                    }
                }
            }
        }
    }
}

class PvpDiffCallback : DiffUtil.ItemCallback<PvpCharacterData>() {

    override fun areItemsTheSame(
        oldItem: PvpCharacterData,
        newItem: PvpCharacterData
    ): Boolean {
        return oldItem.unitId == newItem.unitId
    }

    override fun areContentsTheSame(
        oldItem: PvpCharacterData,
        newItem: PvpCharacterData
    ): Boolean {
        return oldItem.unitId == newItem.unitId
    }
}
