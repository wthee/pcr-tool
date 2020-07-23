package cn.wthee.pcrtool.adapters

import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ItemAttackPatternBinding
import cn.wthee.pcrtool.ui.detail.character.CharacterSkillViewModel.Companion.iconType1
import cn.wthee.pcrtool.ui.detail.character.CharacterSkillViewModel.Companion.iconType2
import cn.wthee.pcrtool.utils.Constants.SKILL_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.GlideUtil
import cn.wthee.pcrtool.utils.OnBitmap
import cn.wthee.pcrtool.utils.PaletteHelper


class SkillLoopAdapter() :
    ListAdapter<Int, SkillLoopAdapter.ViewHolder>(SkillLoopDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemAttackPatternBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemAttackPatternBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(atkId: Int) {
            //设置数据
            binding.apply {
                val ctx = MyApplication.getContext()
                //加载动画
                root.animation =
                    AnimationUtils.loadAnimation(ctx, R.anim.anim_scale)
                //加载图片
                if (atkId == 1) {
                    skillNo.visibility = View.VISIBLE
                } else {
                    skillNo.visibility = View.GONE
                    val iconType = if (atkId == 1001) {
                        skillOrder.text = 1.toString()
                        iconType1
                    } else {
                        skillOrder.text = 2.toString()
                        iconType2
                    }
                    val picUrl =
                        SKILL_ICON_URL + iconType + WEBP
                    GlideUtil.loadReturnBitmap(
                        picUrl,
                        skillIcon,
                        R.drawable.error,
                        object : OnBitmap {
                            override fun returnBitmap(bitmap: Bitmap) {
                                //字体颜色
                                skillOrder.setTextColor(
                                    PaletteHelper.createPaletteSync(bitmap)
                                        .getVibrantColor(Color.WHITE)
                                )
                            }
                        })

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