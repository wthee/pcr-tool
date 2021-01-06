package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.data.db.entity.PvpLikedData
import cn.wthee.pcrtool.databinding.ItemPvpLikedBinding
import cn.wthee.pcrtool.utils.dp


class PvpLikedAdapter(
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
                type.text = data.type.toString()
                val adapter = PvpCharacterResultItemAdapter()
                likedCharacters.adapter = adapter
                adapter.submitList(data.getIds())
                val params0 = atk.layoutParams
                val params1 = def.layoutParams
                if (isFloat) {
                    params0.width = 14.dp
                    params0.height = 14.dp
                    params1.width = 14.dp
                    params1.height = 14.dp
                } else {
                    params0.width = 24.dp
                    params0.height = 24.dp
                    params1.width = 24.dp
                    params1.height = 24.dp
                }
//                atk.layoutParams = params0
//                def.layoutParams = params1
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
