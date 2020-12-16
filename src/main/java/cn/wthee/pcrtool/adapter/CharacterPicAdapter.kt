package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ItemPicBinding
import cn.wthee.pcrtool.ui.detail.character.basic.CharacterPicListFragment
import cn.wthee.pcrtool.utils.ResourcesUtil
import coil.load


class CharacterPicAdapter(private val parentFragment: Fragment) :
    ListAdapter<String, CharacterPicAdapter.ViewHolder>(CharacterImageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemPicBinding.inflate(
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

    inner class ViewHolder(private val binding: ItemPicBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(url: String) {
            //设置数据
            binding.apply {
                pic.load(url) {
                    error(R.drawable.error)
                    placeholder(R.drawable.load)
                    listener(
                        onStart = {
                            parentFragment.startPostponedEnterTransition()
                        },
                        onSuccess = { _, _ ->
                            CharacterPicListFragment.hasLoaded[absoluteAdapterPosition] = true
                        }
                    )
                }
                pic.transitionName = url
                root.setOnClickListener {
                    val selected = !CharacterPicListFragment.hasSelected[absoluteAdapterPosition]
                    CharacterPicListFragment.hasSelected[absoluteAdapterPosition] = selected
                    pic.foreground = ResourcesUtil.getDrawable(
                        if (selected) R.color.colorAlphaBlack else R.color.colorAlpha
                    )

                    if (CharacterPicListFragment.hasSelected.contains(true)) {
                        CharacterPicListFragment.downLoadFab.apply {
                            text = parentFragment.resources.getString(
                                R.string.select_pic_count,
                                CharacterPicListFragment.hasSelected.filter { it }.size
                            )
                        }
                    } else {
                        CharacterPicListFragment.downLoadFab.apply {
                            text = "未选择图片"
                        }
                    }
                }
            }
        }
    }
}

class CharacterImageDiffCallback : DiffUtil.ItemCallback<String>() {

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
