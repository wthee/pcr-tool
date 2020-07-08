package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.CharacterSkillInfo
import cn.wthee.pcrtool.databinding.ItemSkillBinding
import cn.wthee.pcrtool.utils.Constants.SKILL_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.GlideUtil


class SkillAdapter :
    ListAdapter<CharacterSkillInfo, SkillAdapter.ViewHolder>(SkillDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSkillBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemSkillBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(skill: CharacterSkillInfo) {
            //设置数据
            binding.apply {
                val ctx = MyApplication.getContext()
                //加载动画
                content.animation =
                    AnimationUtils.loadAnimation(ctx, R.anim.anim_scale)
                //装备名称
                name.text = skill.name
                desc.text = skill.desc
                type.text = when (skill.icon_type / 1000) {
                    1 -> "连结爆发"
                    2 -> "技能"
                    3 -> "EX技能"
                    else -> ""
                }
                //加载装备图片
                val picUrl = SKILL_ICON_URL + skill.icon_type + WEBP
                GlideUtil.load(picUrl, itemPic, R.drawable.error, null)
                //技能属性
                val adapter = SkillActionAdapter()
                actions.adapter = adapter
                val lm = LinearLayoutManager(MyApplication.getContext())
                lm.orientation = LinearLayoutManager.VERTICAL
                actions.layoutManager = lm
                adapter.submitList(skill.actions)
            }
        }
    }

}

class SkillDiffCallback : DiffUtil.ItemCallback<CharacterSkillInfo>() {

    override fun areItemsTheSame(
        oldItem: CharacterSkillInfo,
        newItem: CharacterSkillInfo
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: CharacterSkillInfo,
        newItem: CharacterSkillInfo
    ): Boolean {
        return oldItem.name == newItem.name
    }
}