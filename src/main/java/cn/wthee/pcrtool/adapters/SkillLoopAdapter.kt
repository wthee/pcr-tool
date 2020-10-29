package cn.wthee.pcrtool.adapters

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.ui.detail.character.CharacterSkillViewModel.Companion.iconType1
import cn.wthee.pcrtool.ui.detail.character.CharacterSkillViewModel.Companion.iconType2
import cn.wthee.pcrtool.utils.Constants.SKILL_ICON_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.PaletteHelper
import cn.wthee.pcrtool.utils.ResourcesUtil
import coil.load


class SkillLoopAdapter :
    ListAdapter<Int, SkillLoopAdapter.ViewHolder>(SkillLoopDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemCommonBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(atkId: Int) {
            //设置数据
            binding.apply {
                val ctx = MyApplication.context
                //加载图片 TODO 优化判断
                Log.e("skill", atkId.toString())
                if (atkId == 1) {
                    name.text = "普攻"
                    name.setTextColor(ctx.getColor(R.color.colorPrimary))
                    pic.setBackgroundResource(R.drawable.unknow_gray)
                    val drawable = ResourcesUtil.getDrawable(R.drawable.ic_pvp)
                    drawable?.setTint(ResourcesUtil.getColor(R.color.colorAccent))
                    pic.foreground = drawable
                } else {
                    //技能图标
                    val iconType = if (atkId == 1001 || atkId == 2001) {
                        name.text = "技能1"
                        iconType1
                    } else {
                        name.text = "技能2"
                        iconType2
                    }
                    //图标地址
                    val picUrl = SKILL_ICON_URL + iconType + WEBP
                    pic.load(picUrl) {
                        target {
                            val bitmap = (it as BitmapDrawable).bitmap
                            //字体颜色
                            name.setTextColor(
                                PaletteHelper.createPaletteSync(bitmap)
                                    .getLightVibrantColor(Color.BLACK)
                            )
                            pic.background = it
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
        return oldItem == newItem
    }
}