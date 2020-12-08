package cn.wthee.pcrtool.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.data.network.model.PvpData
import cn.wthee.pcrtool.databinding.ItemPvpResultBinding


class PvpCharacterResultAdapter(
    private val activity: Activity
) :
    ListAdapter<PvpData, PvpCharacterResultAdapter.ViewHolder>(PvpResultDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPvpResultBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class ViewHolder(private val binding: ItemPvpResultBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PvpData) {
            //设置数据
            binding.apply {
                val adapter = PvpCharacterResultItemAdapter(activity)
                atkCharacters.adapter = adapter
                adapter.submitList(data.atk)
                up.text = "${data.up}"
                down.text = "${data.down}"
            }
        }
    }
}

class PvpResultDiffCallback : DiffUtil.ItemCallback<PvpData>() {

    override fun areItemsTheSame(
        oldItem: PvpData,
        newItem: PvpData
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: PvpData,
        newItem: PvpData
    ): Boolean {
        return oldItem == newItem
    }
}
