package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.SkillDetail
import cn.wthee.pcrtool.data.view.SkillActionText
import cn.wthee.pcrtool.databinding.ItemSkillBinding
import cn.wthee.pcrtool.utils.Constants.SKILL_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.PaletteUtil
import cn.wthee.pcrtool.utils.dp
import coil.Coil
import coil.load
import coil.request.ImageRequest
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

/**
 * 角色技能列表适配器
 *
 * 列表项布局 [ItemSkillBinding]
 *
 * 列表项数据 [SkillDetail]
 */
class SkillAdapter(private val fragmentManager: FragmentManager) :
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
                val ctx = MyApplication.context
                //加载动画
                root.animation =
                    AnimationUtils.loadAnimation(ctx, R.anim.anim_scale)
                //技能描述
                desc.text = skill.desc
                type.text = when (skill.skillId % 1000) {
                    1, 21 -> "连结爆发"
                    11 -> "连结爆发+"
                    2, 22 -> "技能1"
                    12 -> "技能1+"
                    3, 23 -> "技能2"
                    13 -> "技能2+"
                    501 -> "EX技能"
                    511 -> "EX技能+"
                    100 -> "SP连结爆发"
                    101 -> "SP技能1"
                    102 -> "SP技能2"
                    103 -> "SP技能3"
                    else -> {
                        type.visibility = View.GONE
                        ""
                    }
                }
                //技能名称
                name.text = if (skill.name.isBlank()) type.text else skill.name
                //等级
                level.text = "技能等级：${skill.level}"
                //加载图片
                val picUrl = SKILL_ICON_URL + skill.icon_type + WEBP
                itemPic.load(picUrl) {
                    error(R.drawable.unknown_gray)
                    placeholder(R.drawable.unknown_gray)
                    listener(
                        onSuccess = { _, _ ->
                            val coil = Coil.imageLoader(MyApplication.context)
                            val request = ImageRequest.Builder(MyApplication.context)
                                .data(picUrl)
                                .build()
                            MainScope().launch {
                                val drawable = coil.execute(request).drawable
                                //字体颜色
                                name.setTextColor(
                                    PaletteUtil.createPaletteSync((drawable as BitmapDrawable).bitmap)
                                        .getDarkVibrantColor(Color.BLACK)
                                )
                            }
                        }
                    )
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
                        if (show) {
                            Regex("\\{.*?\\}").findAll(skillActionText.action).forEach {
                                if (it.value != s[0].coe) {
                                    skillActionText.action =
                                        skillActionText.action.replace(it.value, "")
                                }
                            }
                        } else {
                            skillActionText.action =
                                skillActionText.action.replace(Regex("\\{.*?\\}"), "")
                        }
                    }
                } catch (e: Exception) {

                }


                //技能动作属性
                val adapter = SkillActionAdapter(fragmentManager)
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