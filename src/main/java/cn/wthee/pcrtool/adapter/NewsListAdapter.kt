package cn.wthee.pcrtool.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentManager
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.db.entity.NewsTable
import cn.wthee.pcrtool.databinding.ItemNewsBinding
import cn.wthee.pcrtool.ui.tool.news.NewsDetailDialogFragment
import cn.wthee.pcrtool.utils.ResourcesUtil
import com.google.android.material.card.MaterialCardView
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton


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
        val item = getItem(position)!!
        val view = holder.itemView.findViewById<MaterialCardView>(R.id.news_item)
        if (selectItems.find { it.id == item.id } != null) {
            view.setCardForegroundColor(ColorStateList.valueOf(ResourcesUtil.getColor(R.color.colorAlphaBlack)))
        } else {
            view.setCardForegroundColor(ColorStateList.valueOf(ResourcesUtil.getColor(R.color.colorAlpha)))
        }
        holder.bind(item)
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
                    NewsDetailDialogFragment.newInstance(
                        region, data.getTrueId(), data.url
                    ).show(fragmentManager, "detail$data.id")
                }
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