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
import cn.wthee.pcrtool.data.view.SkillActionText
import cn.wthee.pcrtool.databinding.ItemSkillActionBinding
import cn.wthee.pcrtool.utils.ResourcesUtil

/**
 * 角色技能效果列表适配器
 *
 * 列表项布局 [ItemSkillActionBinding]
 *
 * 列表项数据 [SkillActionText]
 */
class SkillActionAdapter :
    ListAdapter<SkillActionText, SkillActionAdapter.ViewHolder>(ActionDiffCallback()) {
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
        fun bind(skillAction: SkillActionText) {
            binding.apply {
                action.animation =
                    AnimationUtils.loadAnimation(MyApplication.context, R.anim.anim_scale)
                //目标描述
                target.text = skillAction.target
                //详细描述
                val spannable = SpannableStringBuilder(skillAction.action)
                val starts = arrayListOf<Int>()
                val starts0 = arrayListOf<Int>()
                val starts1 = arrayListOf<Int>()
                val ends = arrayListOf<Int>()
                val ends0 = arrayListOf<Int>()
                val ends1 = arrayListOf<Int>()
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
                    if (c == '(') {
                        starts1.add(index)
                    }
                    if (c == ')') {
                        ends1.add(index)
                    }
                    false
                }
                //忽略越界异常
                try {
                    //公式
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
                    //数值
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
                    //范围等
                    starts1.forEachIndexed { index, _ ->
                        //变色
                        spannable.setSpan(
                            ForegroundColorSpan(
                                ResourcesUtil.getColor(R.color.textGray)
                            ), starts1[index], ends1[index] + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        //加粗
                        spannable.setSpan(
                            StyleSpan(Typeface.BOLD),
                            starts1[index],
                            ends1[index] + 1,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        //字体大小
//                        spannable.setSpan(
//                            RelativeSizeSpan(0.8f),
//                            starts1[index],
//                            ends1[index] + 1,
//                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//                        )
                    }
                } catch (e: Exception) {

                }


                action.text = spannable
            }
        }
    }

}

private class ActionDiffCallback : DiffUtil.ItemCallback<SkillActionText>() {

    override fun areItemsTheSame(
        oldItem: SkillActionText,
        newItem: SkillActionText
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: SkillActionText,
        newItem: SkillActionText
    ): Boolean {
        return oldItem == newItem
    }
}