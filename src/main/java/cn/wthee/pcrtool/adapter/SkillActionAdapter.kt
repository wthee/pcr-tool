package cn.wthee.pcrtool.adapter

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ItemSkillActionBinding

/**
 * 角色技能效果列表适配器
 *
 * 列表项布局 [ItemSkillActionBinding]
 *
 * 列表项数据 [String]
 */
class SkillActionAdapter :
    ListAdapter<String, SkillActionAdapter.ViewHolder>(ActionDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSkillActionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemSkillActionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(fixed: String) {
            binding.apply {
                action.animation =
                    AnimationUtils.loadAnimation(MyApplication.context, R.anim.anim_scale)
                //改变颜色
                val spannable = SpannableStringBuilder(fixed)
                val start0 = fixed.indexOfFirst { it == '<' }
                val start1 = fixed.indexOfLast { it == '<' }
                val end0 = fixed.indexOfFirst { it == '>' }
                val end1 = fixed.indexOfLast { it == '>' }
                if (start0 != -1 && end0 != -1) {
                    spannable.setSpan(
                        ForegroundColorSpan(
                            MyApplication.context.getColor(R.color.colorPrimary)
                        ), start0, end0 + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                if (start1 != -1 && end1 != -1) {
                    spannable.setSpan(
                        ForegroundColorSpan(
                            MyApplication.context.getColor(R.color.colorPrimary)
                        ), start1, end1 + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
                action.text = spannable
            }
        }
    }

}

private class ActionDiffCallback : DiffUtil.ItemCallback<String>() {

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