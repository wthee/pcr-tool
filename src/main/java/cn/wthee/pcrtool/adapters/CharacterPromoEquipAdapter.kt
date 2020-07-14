package cn.wthee.pcrtool.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.model.EquipmentData
import cn.wthee.pcrtool.databinding.ItemEquipmentPromotionBinding
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsFragment
import cn.wthee.pcrtool.utils.ActivityUtil
import cn.wthee.pcrtool.utils.Constants.EQUIPMENT_URL
import cn.wthee.pcrtool.utils.Constants.UNKNOW_EQUIP_ID
import cn.wthee.pcrtool.utils.Constants.WEBP
import cn.wthee.pcrtool.utils.GlideUtil


class EquipmentPromotionAdapter :
    ListAdapter<EquipmentData, EquipmentPromotionAdapter.ViewHolder>(EquipDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEquipmentPromotionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemEquipmentPromotionBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(equip: EquipmentData) {
            //设置数据
            binding.apply {
                //装备名称
                name.text = equip.equipmentName
                //加载装备图片
                val picUrl = EQUIPMENT_URL + equip.equipmentId + WEBP
                GlideUtil.load(picUrl, itemPic, R.drawable.error, null)
                //显示星级
                val stars = arrayOf(star0, star1, star2, star3, star4)
                when (equip.promotionLevel) {
                    2 -> stars[0].visibility = View.VISIBLE
                    3 -> {
                        stars[0].visibility = View.VISIBLE
                        stars[1].visibility = View.VISIBLE
                        stars[2].visibility = View.VISIBLE
                    }
                    4, 5 -> {
                        stars.forEach {
                            it.visibility = View.VISIBLE
                        }
                    }
                }
                itemPic.transitionName = "pic1_${equip.equipmentId}"
                //设置点击跳转
                root.setOnClickListener {
                    if (equip.equipmentId != UNKNOW_EQUIP_ID) {
                        EquipmentDetailsFragment.getInstance(equip, true).show(
                            ActivityUtil.instance.currentActivity?.supportFragmentManager!!,
                            "details"
                        )
                    }
                }
            }
        }
    }

}

class EquipDiffCallback : DiffUtil.ItemCallback<EquipmentData>() {

    override fun areItemsTheSame(
        oldItem: EquipmentData,
        newItem: EquipmentData
    ): Boolean {
        return oldItem.equipmentId == newItem.equipmentId
    }

    override fun areContentsTheSame(
        oldItem: EquipmentData,
        newItem: EquipmentData
    ): Boolean {
        return oldItem.equipmentId == newItem.equipmentId
    }
}