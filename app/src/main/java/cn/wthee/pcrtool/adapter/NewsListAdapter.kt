package cn.wthee.pcrtool.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.entity.NewsTable
import cn.wthee.pcrtool.databinding.ItemNewsBinding
import cn.wthee.pcrtool.utils.ResourcesUtil.setTitleBackground

/**
 * 公告列表列表适配器
 *
 * 列表项布局 [ItemNewsBinding]
 *
 * 列表项数据 [NewsTable]
 */
class NewsAdapter(private val callBack: CallBack) :
    PagingDataAdapter<NewsTable, NewsAdapter.ViewHolder>(NewsListDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    inner class ViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: NewsTable) {
            binding.root.animation =
                AnimationUtils.loadAnimation(binding.root.context, R.anim.anim_list_item)
            //设置数据
            binding.apply {
                newsTitle.text = data.title
                newsDate.text = data.date
                //设置标签
                val tags = data.getTagList()
                if (data.getTagList().size > 1) tags.remove("お知らせ")
                val colorId = when (tags[0]) {
                    "公告", "更新", "アップデート" -> R.color.news_update
                    "系統", "メンテナンス" -> R.color.news_system
                    else -> R.color.news_main
                }
                val fTag = when (tags[0]) {
                    "アップデート" -> "更新"
                    "系統", "メンテナンス" -> "系统"
                    "お知らせ" -> "新闻"
                    "活動", "イベント" -> "活动"
                    "グッズ" -> "周边"
                    else -> tags[0]
                }
                tag.text = fTag
                tag.setTitleBackground(colorId)
                //点击查看
                root.setOnClickListener {
                    callBack.todo(data)
                }
            }
        }
    }

}

private class NewsListDiffCallback : DiffUtil.ItemCallback<NewsTable>() {

    override fun areItemsTheSame(
        oldItem: NewsTable,
        newItem: NewsTable
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: NewsTable,
        newItem: NewsTable
    ): Boolean {
        return oldItem == newItem
    }
}