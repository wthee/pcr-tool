package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ItemCardBgBinding
import cn.wthee.pcrtool.utils.Constants.CHARACTER_URL
import cn.wthee.pcrtool.utils.Constants.WEPB
import cn.wthee.pcrtool.utils.GlideUtil


class CharacterCardBgAdapter :
    ListAdapter<String, CharacterCardBgAdapter.ViewHolder>(ImageDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCardBgBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemCardBgBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(id: String) {
            with(binding) {
                //加载网络图片
                val picUrl = CHARACTER_URL + id + WEPB
                GlideUtil.load(picUrl, characterPic, R.drawable.error, null)
            }
        }
    }

}

private class ImageDiffCallback : DiffUtil.ItemCallback<String>() {

    override fun areItemsTheSame(
        oldItem: String,
        newItem: String
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: String,
        newItem: String
    ): Boolean {
        return oldItem == newItem
    }
}