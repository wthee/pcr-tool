package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ItemCardBgBinding
import cn.wthee.pcrtool.ui.detail.character.CharacterPicDialogFragment
import coil.load


class CharacterPicAdapter() :
    ListAdapter<String, CharacterPicAdapter.ViewHolder>(ImageDiffCallback()) {
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
        fun bind(url: String) {
            with(binding) {
                //加载网络图片
                characterPic.load(url) {
                    error(R.drawable.error)
                    placeholder(R.drawable.load)
                    listener(
                        onSuccess = { _, _ ->
                            CharacterPicDialogFragment.hasLoaded[layoutPosition] = true
                        }
                    )
                }
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