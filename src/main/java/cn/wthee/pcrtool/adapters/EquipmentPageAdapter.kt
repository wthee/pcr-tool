package cn.wthee.pcrtool.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.FragmentManager
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import cn.wthee.pcrtool.MainActivity
import cn.wthee.pcrtool.MyApplication
import cn.wthee.pcrtool.R
import cn.wthee.pcrtool.database.view.EquipmentMaxData
import cn.wthee.pcrtool.databinding.ItemCommonBinding
import cn.wthee.pcrtool.ui.detail.equipment.EquipmentDetailsFragment
import cn.wthee.pcrtool.utils.Constants.EQUIPMENT_URL
import cn.wthee.pcrtool.utils.Constants.WEBP
import coil.load


class EquipmentPageAdapter(
    private val fragmentManager: FragmentManager
) : PagingDataAdapter<EquipmentMaxData, EquipmentPageAdapter.ViewHolder>(EquipDiffCallback()) {
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

    inner class ViewHolder(private val binding: ItemCommonBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(equip: EquipmentMaxData?) {
            if (equip != null) {
                //设置数据
                binding.apply {
                    val ctx = MyApplication.context
                    pic.animation = AnimationUtils.loadAnimation(ctx, R.anim.anim_scale)
                    //装备名称
                    name.text = equip.equipmentName
                    //加载装备图片
                    val picUrl = EQUIPMENT_URL + equip.equipmentId + WEBP
                    pic.load(picUrl) {
                        error(R.drawable.unknow_gray)
                        placeholder(R.drawable.load_mini)
                    }
                    //设置点击跳转
                    root.setOnClickListener {
                        click(equip)
                    }
                }
            }
        }

        private fun click(equip: EquipmentMaxData) {
            MainActivity.currentEquipPosition = absoluteAdapterPosition
            EquipmentDetailsFragment.getInstance(equip).show(fragmentManager, "details")
        }
    }
}