package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.databinding.ItemPvpResultIconBinding
import cn.wthee.pcrtool.ui.tool.pvp.PvpFragment.Companion.r6Ids
import cn.wthee.pcrtool.utils.Constants.UNIT_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import coil.Coil
import coil.request.ImageRequest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 竞技场查询结果角色图标列表适配器
 *
 * 列表项布局 [ItemPvpResultIconBinding]
 *
 * 列表项数据 [Int] unit_id
 */
class PvpResultItemAdapter :
    ListAdapter<Int, PvpResultItemAdapter.ViewHolder>(PvpResultItemDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPvpResultIconBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class ViewHolder(private val binding: ItemPvpResultIconBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uid: Int) {
            //设置数据
            binding.apply {
                var id = uid
                id += if (r6Ids.contains(id)) 60 else 30
                val picUrl = UNIT_ICON_URL + id + WEBP
                val coil = Coil.imageLoader(root.context)
                val request = ImageRequest.Builder(root.context)
                    .data(picUrl)
                    .build()
                MainScope().launch {
                    icon.setImageDrawable(coil.execute(request).drawable)
                }
            }
        }
    }
}

class PvpResultItemDiffCallback : DiffUtil.ItemCallback<Int>() {

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
