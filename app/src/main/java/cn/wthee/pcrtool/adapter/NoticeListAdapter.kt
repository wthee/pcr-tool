package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.BuildConfig
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.AppNotice
import cn.wthee.pcrtool.databinding.ItemNoticeBinding
import cn.wthee.pcrtool.utils.BrowserUtil
import cn.wthee.pcrtool.utils.ResourcesUtil.setTitleBackground

/**
 * 通知公告列表列表适配器
 *
 * 列表项布局 [ItemNoticeBinding]
 *
 * 列表项数据 [AppNotice]
 */
class NoticeListAdapter(private val callBack: CallBack) :
    ListAdapter<AppNotice, NoticeListAdapter.ViewHolder>(NoticeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNoticeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class ViewHolder(private val binding: ItemNoticeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(data: AppNotice) {
            binding.root.animation =
                AnimationUtils.loadAnimation(binding.root.context, R.anim.anim_list_item)
            //设置数据
            binding.apply {
                newsDate.text = data.date.substring(0, 10)
                //设置标签
                val colorId = when (data.type) {
                    0 -> {
                        title.text = data.message
                        val remoteVersion = data.title.replace(".", "").toInt()
                        tag.text = if (remoteVersion > BuildConfig.VERSION_CODE) {
                            tip.visibility = View.VISIBLE
                            "新版本："
                        } else {
                            tip.visibility = View.GONE
                            "当前版本 "
                        } + data.title
                        R.color.news_update
                    }
                    1 -> {
                        tag.text = data.title
                        title.text = data.message
                        title.maxLines = 3
                        R.color.news_system
                    }
                    2 -> {
                        tag.text = "通知"
                        title.text = data.title
                        R.color.news_main
                    }
                    else -> {
                        tag.visibility = View.GONE
                        R.color.news_main
                    }
                }
                tag.setTitleBackground(colorId)
                //点击查看详情
                root.setOnClickListener {
                    when (data.type) {
                        0 -> {
                            if (data.title != BuildConfig.VERSION_NAME) {
                                BrowserUtil.open(root.context, data.url, "前往下载应用")
                            }
                        }
                        1, 2 -> {
                            callBack.todo(data)
                        }
                    }
                }
            }
        }
    }

}

private class NoticeDiffCallback : DiffUtil.ItemCallback<AppNotice>() {

    override fun areItemsTheSame(
        oldItem: AppNotice,
        newItem: AppNotice
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: AppNotice,
        newItem: AppNotice
    ): Boolean {
        return oldItem == newItem
    }
}