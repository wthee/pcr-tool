package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.PvpPositionData
import cn.wthee.pcrtool.databinding.ItemPvpPositionBinding
import cn.wthee.pcrtool.utils.ResourcesUtil


/**
 * 竞技场角色列表适配器
 *
 * 列表项布局：分组名 [ItemPvpPositionBinding]，
 */
class PvpPositionAdapter(private val floatWindow: Boolean) :
    ListAdapter<PvpPositionData, PvpPositionAdapter.ViewHolder>(PvpPositionDataDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPvpPositionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemPvpPositionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(item: PvpPositionData) {
            binding.apply {
                positionType.text = ResourcesUtil.getString(
                    when (item.positionType) {
                        1 -> R.string.position_1
                        2 -> R.string.position_2
                        else -> R.string.position_3
                    }
                )
                val adapter = PvpIconAdapter(floatWindow)
                listPosition.adapter = adapter
                adapter.submitList(item.list)
            }
        }

    }


}

private class PvpPositionDataDiffCallback : DiffUtil.ItemCallback<PvpPositionData>() {

    override fun areItemsTheSame(
        oldItem: PvpPositionData,
        newItem: PvpPositionData
    ): Boolean {
        return oldItem.positionType == newItem.positionType
    }

    override fun areContentsTheSame(
        oldItem: PvpPositionData,
        newItem: PvpPositionData
    ): Boolean {
        return oldItem == newItem
    }
}