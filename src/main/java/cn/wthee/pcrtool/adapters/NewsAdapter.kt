package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.data.model.News
import cn.wthee.pcrtool.databinding.ItemNewsBinding
import cn.wthee.pcrtool.ui.tool.news.ToolNewsDetailFragment


class NewsAdapter(
    private val fragmentManager: FragmentManager,
    private val region: Int
) : PagingDataAdapter<News, NewsAdapter.ViewHolder>(NewsListDiffCallback()) {
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
        holder.bind(getItem(position)!!)
    }

    inner class ViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: News) {
            //设置数据
            binding.apply {
                newsTitle.text = data.title
                newsDate.text = data.date
                val adapter = NewsTagAdapter()
                tags.adapter = adapter
                adapter.submitList(data.tag)
                //点击查看
                root.setOnClickListener {
                    ToolNewsDetailFragment.newInstance(
                        region, data.id, data.url
                    ).show(fragmentManager, "detail$data.id")
                }
            }
        }
    }

}

private class NewsListDiffCallback : DiffUtil.ItemCallback<News>() {

    override fun areItemsTheSame(
        oldItem: News,
        newItem: News
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: News,
        newItem: News
    ): Boolean {
        return oldItem == newItem
    }
}