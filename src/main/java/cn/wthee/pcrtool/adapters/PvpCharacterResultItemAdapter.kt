package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.Atk
import cn.wthee.pcrtool.databinding.ItemPvpResultIconBinding
import cn.wthee.pcrtool.utils.Constants.UNIT_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import coil.load


class PvpCharacterResultItemAdapter :
    ListAdapter<Atk, PvpCharacterResultItemAdapter.ViewHolder>(PvpResultItemDiffCallback()) {
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
        fun bind(data: Atk) {
            //设置数据
            binding.apply {
                val picUrl = UNIT_ICON_URL + (data.id + 30) + WEBP
                icon.load(picUrl) {
                    error(R.drawable.unknow_gray)
                    placeholder(R.drawable.unknow_gray)
                }
            }
        }
    }
}

class PvpResultItemDiffCallback : DiffUtil.ItemCallback<Atk>() {

    override fun areItemsTheSame(
        oldItem: Atk,
        newItem: Atk
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Atk,
        newItem: Atk
    ): Boolean {
        return oldItem == newItem
    }
}
