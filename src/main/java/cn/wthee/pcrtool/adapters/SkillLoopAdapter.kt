package cn.wthee.pcrtool.adapters

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ItemSkillLoopBinding
import cn.wthee.pcrtool.ui.detail.character.CharacterSkillViewModel.Companion.iconType1
import cn.wthee.pcrtool.ui.detail.character.CharacterSkillViewModel.Companion.iconType2
import cn.wthee.pcrtool.utils.Constants.SKILL_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.PaletteHelper
import coil.load


class SkillLoopAdapter() :
    ListAdapter<Int, SkillLoopAdapter.ViewHolder>(SkillLoopDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSkillLoopBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemSkillLoopBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(atkId: Int) {
            //设置数据
            binding.apply {
                val ctx = MyApplication.getContext()
                //加载动画
                root.animation = AnimationUtils.loadAnimation(ctx, R.anim.anim_scale)
                //加载图片
                if (atkId == 1) {
                    skillOrder.text = "普攻"
                    skillIcon.setBackgroundResource(R.drawable.skill_0)
                    skillIconX.visibility = View.VISIBLE
                } else {
                    //技能图标
                    val iconType = if (atkId == 1001) {
                        skillOrder.text = "技能1"
                        iconType1
                    } else {
                        skillOrder.text = "技能2"
                        iconType2
                    }
                    //图标地址
                    val picUrl =
                        SKILL_ICON_URL + iconType + WEBP

                    skillIcon.load(picUrl) {
                        error(R.drawable.unknow)
                        target {
                            val bitmap = (it as BitmapDrawable).bitmap
                            //字体颜色
                            skillOrder.setTextColor(
                                PaletteHelper.createPaletteSync(bitmap)
                                    .getLightVibrantColor(Color.BLACK)
                            )
                            skillIcon.background = it
                        }
                    }
                }
            }
        }
    }

}

private class SkillLoopDiffCallback : DiffUtil.ItemCallback<Int>() {

    override fun areItemsTheSame(
        oldItem: Int,
        newItem: Int
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: Int,
        newItem: Int
    ): Boolean {
        return oldItem.equals(newItem)
    }
}