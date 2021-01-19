package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ItemTagBinding
import cn.wthee.pcrtool.utils.ResourcesUtil.setTitleBackground


class NewsTagAdapter :
    ListAdapter<String, NewsTagAdapter.ViewHolder>(NewsTagDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTagBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemTagBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(tag: String) {
            //设置数据
            binding.apply {
                val colorId = when (tag) {
                    "公告", "更新", "アップデート" -> R.color.news_update
                    "系統", "メンテナンス" -> R.color.news_system
                    else -> R.color.colorPrimary
                }
                val fTag = when (tag) {
                    "アップデート" -> "更新"
                    "系統", "メンテナンス" -> "系统"
                    "お知らせ" -> "新闻"
                    "活動", "イベント" -> "活动"
                    "グッズ" -> "周边"
                    else -> tag
                }
                text1.text = fTag
                text1.setTitleBackground(colorId)
            }
        }
    }

}

private class NewsTagDiffCallback : DiffUtil.ItemCallback<String>() {

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