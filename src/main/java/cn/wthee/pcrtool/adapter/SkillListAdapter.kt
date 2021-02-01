package cn.wthee.pcrtool.adapter

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.bean.CharacterSkillInfo
import cn.wthee.pcrtool.data.db.view.SkillActionLite
import cn.wthee.pcrtool.databinding.ItemSkillBinding
import cn.wthee.pcrtool.utils.Constants.SKILL_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.PaletteUtil
import coil.load

/**
 * 角色技能列表适配器
 *
 * 列表项布局 [ItemSkillBinding]
 *
 * 列表项数据 [CharacterSkillInfo]
 */
class SkillAdapter : ListAdapter<CharacterSkillInfo, SkillAdapter.ViewHolder>(SkillDiffCallback()) {
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
                val ctx = MyApplication.context
                //加载动画
                root.animation =
                    AnimationUtils.loadAnimation(ctx, R.anim.anim_scale)
                //技能名称
                name.text = skill.name
                //技能描述
                desc.text = skill.desc
                type.text = when (skill.skillId % 1000) {
                    1 -> "连结爆发"
                    11 -> "连结爆发+"
                    2 -> "技能1"
                    12 -> "技能1+"
                    3 -> "技能2"
                    13 -> "技能2+"
                    501 -> "EX技能"
                    511 -> "EX技能+"
                    101 -> "SP技能1"
                    102 -> "SP技能2"
                    103 -> "SP连结爆发"
                    else -> ""
                }
                //加载图片
                val picUrl = SKILL_ICON_URL + skill.icon_type + WEBP
                itemPic.load(picUrl) {
                    target {
                        val bitmap = (it as BitmapDrawable).bitmap
                        //字体颜色
                        name.setTextColor(
                            PaletteUtil.createPaletteSync(bitmap)
                                .getDarkVibrantColor(Color.BLACK)
                        )
                        itemPic.background = it
                    }
                }
                val actionData = skill.getActionInfo()
                //技能属性
                val adapter = SkillActionAdapter()
                actions.adapter = adapter
                adapter.submitList(getActions(actionData))
                //异常状态属性
                val ailmentAdapter = TagAdapter()
                ailments.adapter = ailmentAdapter
                ailmentAdapter.submitList(getAilments(actionData))
            }

        }

        /**
         * 获取动作
         */
        private fun getActions(data: ArrayList<SkillActionLite>): ArrayList<String> {
            val list = arrayListOf<String>()
            data.forEach {
                if (it.action.isNotEmpty()) {
                    list.add(it.action)
                }
            }
            return list
        }

        /**
         * 获取异常状态
         */
        private fun getAilments(data: ArrayList<SkillActionLite>): ArrayList<String> {
            val list = arrayListOf<String>()
            data.forEach {
                if (it.ailmentName.isNotEmpty() && !list.contains(it.ailmentName)) {
                    list.add(it.ailmentName)
                }
            }
            return list
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
        return oldItem == newItem
    }
}