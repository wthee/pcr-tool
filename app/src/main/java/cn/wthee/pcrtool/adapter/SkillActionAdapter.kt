package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.SkillActionLite
import cn.wthee.pcrtool.databinding.ItemSkillActionBinding
import cn.wthee.pcrtool.utils.ResourcesUtil

/**
 * 角色技能效果列表适配器
 *
 * 列表项布局 [ItemSkillActionBinding]
 *
 * 列表项数据 [String]
 */
class SkillActionAdapter :
    ListAdapter<SkillActionLite, SkillActionAdapter.ViewHolder>(ActionDiffCallback()) {
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
        @SuppressLint("SetTextI18n")
        fun bind(skillAction: SkillActionLite) {
            binding.apply {
                action.animation =
                    AnimationUtils.loadAnimation(MyApplication.context, R.anim.anim_scale)
                //目标描述
                target.text = skillAction.target
                //改变颜色
                val spannable = SpannableStringBuilder(skillAction.action)
                val starts = arrayListOf<Int>()
                val starts0 = arrayListOf<Int>()
                val ends = arrayListOf<Int>()
                val ends0 = arrayListOf<Int>()
                skillAction.action.filterIndexed { index, c ->
                    if (c == '<') {
                        starts.add(index)
                    }
                    if (c == '>') {
                        ends.add(index)
                    }
                    if (c == '[') {
                        starts0.add(index)
                    }
                    if (c == ']') {
                        ends0.add(index)
                    }
                    false
                }
                //忽略越界异常
                try {
                    starts.forEachIndexed { index, _ ->
                        //变色
                        spannable.setSpan(
                            ForegroundColorSpan(
                                ResourcesUtil.getColor(R.color.colorPrimary)
                            ), starts[index], ends[index] + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        //加粗
                        spannable.setSpan(
                            StyleSpan(Typeface.BOLD),
                            starts[index],
                            ends[index] + 1,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    starts0.forEachIndexed { index, _ ->
                        //变色
                        spannable.setSpan(
                            ForegroundColorSpan(
                                ResourcesUtil.getColor(R.color.cool_apk)
                            ), starts0[index], ends0[index] + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        //加粗
                        spannable.setSpan(
                            StyleSpan(Typeface.BOLD),
                            starts0[index],
                            ends0[index] + 1,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                } catch (e: Exception) {

                }


                action.text = spannable
            }
        }
    }

}

private class ActionDiffCallback : DiffUtil.ItemCallback<SkillActionLite>() {

    override fun areItemsTheSame(
        oldItem: SkillActionLite,
        newItem: SkillActionLite
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: SkillActionLite,
        newItem: SkillActionLite
    ): Boolean {
        return oldItem == newItem
    }
}