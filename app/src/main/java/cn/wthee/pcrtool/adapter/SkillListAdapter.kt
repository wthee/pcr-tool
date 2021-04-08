package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.data.view.SkillActionText
import cn.wthee.pcrtool.databinding.ItemSkillBinding
import cn.wthee.pcrtool.ui.common.CommonDialogContainerFragment
import cn.wthee.pcrtool.utils.Constants.SKILL_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.utils.dp
import coil.load
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 角色技能列表适配器
 *
 * 列表项布局 [ItemSkillBinding]
 *
 * 列表项数据 [SkillDetail]
 */
class SkillAdapter(private val fragmentManager: FragmentManager, private val skillType: Int) :
    ListAdapter<SkillDetail, SkillAdapter.ViewHolder>(SkillDiffCallback()) {

    private var mSize = 0

    fun setSize(num: Int) {
        mSize = num
    }

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

    inner class ViewHolder(private val binding: ItemSkillBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")
        fun bind(skill: SkillDetail) {
            //设置数据
            binding.apply {
                when (skillType) {
                    1, 2 -> {
                        //BOSS 技能
                        //技能描述
                        desc.text = "？？？"
                    }
                    else -> {
                        //技能描述
                        desc.text = skill.desc
                    }

                }
                type.text = when (skill.skillId % 1000) {
                    501 -> "EX技能"
                    511 -> "EX技能+"
                    100 -> "SP连结爆发"
                    101 -> "SP技能 1"
                    102 -> "SP技能 2"
                    103 -> "SP技能 3"
                    1, 21 -> "连结爆发"
                    11 -> "连结爆发+"
                    else -> {
                        val skillIndex = skill.skillId % 1000 % 10 - 1
                        if (skill.skillId % 1000 / 10 == 1) {
                            "技能 ${skillIndex}+"
                        } else {
                            "技能 $skillIndex"
                        }
                    }
                }
                name.setTextColor(
                    ResourcesUtil.getColor(
                        when {
                            type.text.contains("连结") -> R.color.color_rank_7_10
                            type.text.contains("EX") -> R.color.color_rank_2_3
                            else -> R.color.color_rank_4_6
                        }
                    )
                )
                //技能名称
                name.text = if (skill.name.isBlank()) type.text else skill.name
                //等级 & 动作时间
                level.text = "技能等级：${skill.level}"
                //加载图片
                MainScope().launch {
                    val picUrl = SKILL_ICON_URL + skill.iconType + WEBP
                    itemPic.load(picUrl) {
                        error(R.drawable.unknown_gray)
                        placeholder(R.drawable.unknown_gray)
                    }
                }
                val actionData = skill.getActionInfo()
                //是否显示参数判断
                try {
                    val showCoeIndex = skill.getActionIndexWithCoe()
                    actionData.mapIndexed { index, skillActionText ->
                        val s = showCoeIndex.filter {
                            it.actionIndex == index
                        }
                        val show = s.isNotEmpty()
                        val str = skillActionText.action
                        if (show) {
                            //系数表达式开始位置
                            val startIndex = str.indexOfFirst { ch -> ch == '<' }
                            if (startIndex != -1) {
                                var coeExpr = str.substring(startIndex, str.length)
                                Regex("\\{.*?\\}").findAll(skillActionText.action).forEach {
                                    if (s[0].type == 0) {
                                        coeExpr = coeExpr.replace(it.value, "")
                                    } else if (s[0].coe != it.value) {
                                        coeExpr = coeExpr.replace(it.value, "")
                                    }
                                }
                                skillActionText.action =
                                    str.substring(0, startIndex) + coeExpr
                            }
                        } else {
                            skillActionText.action =
                                str.replace(Regex("\\{.*?\\}"), "")
                        }
                    }
                } catch (e: Exception) {

                }
                //技能动作属性
                val adapter = SkillActionAdapter(object : CallBack {
                    override fun todo(data: Any?) {
                        data?.let {
                            val skillAction = data as SkillActionText
                            CommonDialogContainerFragment.loadSkillFragment(
                                skillAction.summonUnitId, skillAction.level,
                                skillAction.atk
                            ).show(fragmentManager, "summon_skill")
                        }
                    }
                })
                actions.adapter = adapter
                adapter.submitList(actionData)
                //异常状态属性
                val ailmentAdapter = TagAdapter()
                ailments.adapter = ailmentAdapter
                ailmentAdapter.submitList(getAilments(actionData))
            }
            //修改底部边距
            if (layoutPosition == mSize - 1) {
                val params = binding.root.layoutParams as RecyclerView.LayoutParams
                params.bottomMargin = 42.dp
                binding.root.layoutParams = params
            }
        }

        /**
         * 获取异常状态
         */
        private fun getAilments(data: ArrayList<SkillActionText>): ArrayList<String> {
            val list = arrayListOf<String>()
            data.forEach {
                if (it.tag.isNotEmpty() && !list.contains(it.tag)) {
                    list.add(it.tag)
                }
            }
            return list
        }
    }

}

class SkillDiffCallback : DiffUtil.ItemCallback<SkillDetail>() {

    override fun areItemsTheSame(
        oldItem: SkillDetail,
        newItem: SkillDetail
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(
        oldItem: SkillDetail,
        newItem: SkillDetail
    ): Boolean {
        return oldItem == newItem
    }
}