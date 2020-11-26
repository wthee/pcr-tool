package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentManager
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.entity.NewsTable
import cn.wthee.pcrtool.databinding.ItemNewsBinding
import cn.wthee.pcrtool.ui.tool.news.ToolNewsDetailDialogFragment
import cn.wthee.pcrtool.utils.ClipboardUtli


class NewsAdapter(
    private val fragmentManager: FragmentManager,
    private val region: Int
) : PagingDataAdapter<NewsTable, NewsAdapter.ViewHolder>(NewsListDiffCallback()) {
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
        fun bind(data: NewsTable) {
            //设置数据
            binding.apply {
                newsTitle.text = data.title
                newsDate.text = data.date
                val adapter = NewsTagAdapter()
                tags.adapter = adapter
                val tags = data.getTagList()
                if (data.getTagList().size > 1) tags.remove("お知らせ")
                adapter.submitList(tags)

                root.animation =
                    AnimationUtils.loadAnimation(MyApplication.context, R.anim.anim_translate_y)

                //点击查看
                root.setOnClickListener {
                    ToolNewsDetailDialogFragment.newInstance(
                        region, data.getTrueId(), data.url
                    ).show(fragmentManager, "detail$data.id")
                }
                root.setOnLongClickListener {
                    ClipboardUtli.add(data.url)
                    return@setOnLongClickListener true
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