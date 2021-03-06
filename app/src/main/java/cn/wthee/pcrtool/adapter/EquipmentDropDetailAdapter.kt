package cn.wthee.pcrtool.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.data.view.EquipmentIdWithOdd
import cn.wthee.pcrtool.databinding.ItemEquipmentDropDetailBinding
import cn.wthee.pcrtool.utils.Constants.EQUIPMENT_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import coil.load

/**
 * 副本掉落装备列表适配器
 *
 * 列表项布局 [ItemEquipmentDropDetailBinding]
 *
 * 列表项数据 [EquipmentIdWithOdd]
 */
class EquipmentDropDetailAdapter(private val eid: Int) :
    ListAdapter<EquipmentIdWithOdd, EquipmentDropDetailAdapter.ViewHolder>(OddDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemEquipmentDropDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))

    }

    inner class ViewHolder(private val binding: ItemEquipmentDropDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("ClickableViewAccessibility")
        fun bind(equip: EquipmentIdWithOdd) {
            //设置数据
            binding.apply {
                //装备名称
                odd.text = MyApplication.context.getString(R.string.percent, equip.odd)
                //加载装备图片
                val picUrl = EQUIPMENT_URL + equip.eid + WEBP
                itemPic.load(picUrl) {
                    error(R.drawable.unknown_gray)
                    placeholder(R.drawable.unknown_gray)
                }
                if (eid == equip.eid) {
                    odd.setTextColor(Color.RED)
                }
            }
        }
    }

}

class OddDiffCallback : DiffUtil.ItemCallback<EquipmentIdWithOdd>() {

    override fun areItemsTheSame(
        oldItem: EquipmentIdWithOdd,
        newItem: EquipmentIdWithOdd
    ): Boolean {
        return oldItem.eid == newItem.eid
    }

    override fun areContentsTheSame(
        oldItem: EquipmentIdWithOdd,
        newItem: EquipmentIdWithOdd
    ): Boolean {
        return oldItem == newItem
    }
}