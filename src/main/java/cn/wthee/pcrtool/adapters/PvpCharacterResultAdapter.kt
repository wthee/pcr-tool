package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.data.model.Result
import cn.wthee.pcrtool.databinding.ItemPvpResultBinding


class PvpCharacterResultAdapter :
    ListAdapter<Result, PvpCharacterResultAdapter.ViewHolder>(PvpResultDiffCallback()) {
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
        fun bind(data: Result) {
            //设置数据
            binding.apply {
                val adapter = PvpCharacterResultItemAdapter()
                atkCharacters.adapter = adapter
                adapter.submitList(data.atk)
                up.text = "👍\n${data.up.toString()}"
                down.text = "👎\n${data.down.toString()}"
            }
        }
    }
}

class PvpResultDiffCallback : DiffUtil.ItemCallback<Result>() {

    override fun areItemsTheSame(
        oldItem: Result,
        newItem: Result
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Result,
        newItem: Result
    ): Boolean {
        return oldItem == newItem
    }
}
