package cn.wthee.pcrtool.adapter

import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.utils.Constants.EQUIPMENT_URL
import cn.wthee.pcrtool.utils.Constants.SKILL_ICON_URL
import cn.wthee.pcrtool.utils.Constants.UNKNOWN_EQUIP_ID
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.PaletteUtil
import cn.wthee.pcrtool.utils.ResourcesUtil
import cn.wthee.pcrtool.viewmodel.CharacterSkillViewModel.Companion.iconTypes
import coil.load

/**
 * 技能循环图标列表适配器
 *
 * 列表项布局 [ItemCommonBinding]
 *
 * 列表项数据 [Int] atk_id
 */
class SkillLoopAdapter : ListAdapter<Int, SkillLoopAdapter.ViewHolder>(SkillLoopDiffCallback()) {
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
                //加载图片
                if (atkId == 1) {
                    name.text = "普攻"
                    name.setTextColor(ResourcesUtil.getColor(R.color.colorPrimary))
                    pic.load(EQUIPMENT_URL + UNKNOWN_EQUIP_ID + WEBP)
                } else {
                    //技能图标
                    name.text = when (atkId) {
                        1001 -> "技能1"
                        1002 -> "技能2"
                        2001 -> "SP技能1"
                        2002 -> "SP技能2"
                        2003 -> "SP技能3"
                        else -> ""
                    }
                    val iconType = when (atkId) {
                        1001 -> iconTypes[2]
                        1002 -> iconTypes[3]
                        1003 -> iconTypes[1]
                        2001 -> iconTypes[101]
                        2002 -> iconTypes[102]
                        2003 -> iconTypes[103]
                        else -> ""
                    }
                    //图标地址
                    val picUrl = SKILL_ICON_URL + iconType + WEBP

                    pic.load(picUrl) {
                        placeholder(R.drawable.unknown_gray)
                        error(R.drawable.unknown_gray)
                        target {
                            val bitmap = (it as BitmapDrawable).bitmap
                            //字体颜色
                            name.setTextColor(
                                PaletteUtil.createPaletteSync(bitmap)
                                    .getDarkVibrantColor(Color.BLACK)
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