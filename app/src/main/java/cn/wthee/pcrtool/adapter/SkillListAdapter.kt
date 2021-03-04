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
import cn.wthee.pcrtool.data.model.CharacterSkillInfo
import cn.wthee.pcrtool.data.view.SkillActionLite
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
 * 列表项数据 [CharacterSkillInfo]
 */
class SkillAdapter : ListAdapter<CharacterSkillInfo, SkillAdapter.ViewHolder>(SkillDiffCallback()) {

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
                    100 -> "SP连结爆发"
                    101 -> "SP技能1"
                    102 -> "SP技能2"
                    103 -> "SP技能3"
                    else -> ""
                }
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
                //技能属性
                val adapter = SkillActionAdapter()
                actions.adapter = adapter
                adapter.submitList(getActions(actionData))
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