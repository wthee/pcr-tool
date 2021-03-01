package cn.wthee.pcrtool.adapter

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
import cn.wthee.pcrtool.ui.tool.news.NewsDetailDialogFragment
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.ResourcesUtil.setTitleBackground
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.android.material.textview.MaterialTextView

/**
 * 公告列表列表适配器，[region] 区分游戏服务器版本
 *
 * 列表项布局 [ItemNewsBinding]
 *
 * 列表项数据 [NewsTable]
 */
class NewsAdapter(
    private val fragmentManager: FragmentManager,
    private val region: Int,
    private val fabCopy: ExtendedFloatingActionButton
) : PagingDataAdapter<NewsTable, NewsAdapter.ViewHolder>(NewsListDiffCallback()) {

    private val selectItems = arrayListOf<SelectedItem>()

    fun getSelected() = selectItems

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
        val item = getItem(position)
        item?.let { data ->
            val view = holder.itemView.findViewById<MaterialTextView>(R.id.news_title)
            if (selectItems.find { it.id == data.id } != null) {
                view.setTextColor(ResourcesUtil.getColor(R.color.red))
            } else {
                view.setTextColor(ResourcesUtil.getColor(R.color.text))
            }
            holder.bind(data)
        }
    }

    inner class ViewHolder(private val binding: ItemNewsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: NewsTable) {
            binding.root.animation =
                AnimationUtils.loadAnimation(MyApplication.context, R.anim.anim_list_item)
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
                    NewsDetailDialogFragment.getInstance(
                        region, data.getTrueId(), data.url
                    ).show(fragmentManager, "detail$data.id")
                }
                //长按复制
                root.setOnLongClickListener {
                    if (selectItems.find { it.id == data.id } != null) {
                        selectItems.removeAll {
                            it.id == data.id
                        }
                    } else {
                        selectItems.add(SelectedItem(data.id, data.title + "\n" + data.url + "\n"))
                    }
                    //显示/隐藏复制按钮
                    if (selectItems.isNotEmpty()) fabCopy.show() else fabCopy.hide()
                    notifyDataSetChanged()
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

class SelectedItem(
    val id: String,
    val content: String
)