package cn.wthee.pcrtool.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.data.db.entity.PvpLikedData
import cn.wthee.pcrtool.databinding.ItemPvpLikedBinding


class PvpLikedAdapter(
    private val activity: Activity,
    private val isFloat: Boolean
) :
    ListAdapter<PvpLikedData, PvpLikedAdapter.ViewHolder>(PvpLikedDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPvpLikedBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ViewHolder(private val binding: ItemPvpLikedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: PvpLikedData) {
            //设置数据
            binding.apply {
                atkIds.text = data.atks
                defIds.text = data.defs
                val adapter = PvpCharacterResultItemAdapter(activity)
                likedCharacters.adapter = adapter
                adapter.submitList(data.getIds())
                if (isFloat) {
                    rightTitle.visibility = View.GONE
                } else {
                    rightTitle.visibility = View.VISIBLE
                }
            }
        }
    }
}

class PvpLikedDiffCallback : DiffUtil.ItemCallback<PvpLikedData>() {

    override fun areItemsTheSame(
        oldItem: PvpLikedData,
        newItem: PvpLikedData
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: PvpLikedData,
        newItem: PvpLikedData
    ): Boolean {
        return oldItem == newItem
    }
}
